package app.shosetsu.android.backend.workers.onetime

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import app.shosetsu.android.R
import app.shosetsu.android.activity.MainActivity
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.consts.ACTION_OPEN_APP_UPDATE
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.Notifications
import app.shosetsu.android.common.consts.Notifications.ID_APP_UPDATE
import app.shosetsu.android.common.consts.WorkerTags.APP_UPDATE_WORK_ID
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.domain.usecases.load.LoadRemoteAppUpdateUseCase
import app.shosetsu.lib.exceptions.HTTPException
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import java.io.IOException
import java.net.UnknownHostException

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * shosetsu
 * 06 / 09 / 2020
 *
 * Checks for an app update with a notification representing progress
 */
class AppUpdateCheckWorker(
	appContext: Context,
	params: WorkerParameters
) : CoroutineWorker(appContext, params), DIAware, NotificationCapable {
	override val di: DI by closestDI(applicationContext)

	private val openAppForUpdateIntent: Intent
		get() = Intent(applicationContext, MainActivity::class.java).apply {
			action = ACTION_OPEN_APP_UPDATE
		}
	override val defaultNotificationID: Int = ID_APP_UPDATE

	private val loadRemoteAppUpdateUseCase by instance<LoadRemoteAppUpdateUseCase>()
	override val notificationManager: NotificationManagerCompat by notificationManager()

	override val baseNotificationBuilder: NotificationCompat.Builder
		get() = notificationBuilder(applicationContext, Notifications.CHANNEL_APP_UPDATE)
			.setSubText(applicationContext.getString(R.string.notification_app_update_check))
			.setSmallIcon(R.drawable.app_update)
			.setOnlyAlertOnce(true)
			.setOngoing(true)

	override val notifyContext: Context
		get() = applicationContext


	override suspend fun doWork(): Result {
		notify("Starting")
		val entity = try {
			loadRemoteAppUpdateUseCase()
		} catch (e: HTTPException) {
			logE("Error!", e)
			notify("${e.code}") {
				setOngoing(false)
			}
			return Result.failure()
		} catch (e: UnknownHostException) {
			logE("Error!", e)
			notify("Error! ${e.message}") {
				setOngoing(false)
			}
			return Result.failure()
		} catch (e: IOException) {
			logE("Error!", e)
			notify("Error! ${e.message}") {
				setOngoing(false)
			}
			return Result.failure()
		} catch (e: FilePermissionException) {
			logE("Error!", e)
			notify("Error! ${e.message}") {
				setOngoing(false)
				addReportErrorAction(applicationContext, defaultNotificationID, e)
			}
			return Result.failure()
		} catch (e: Exception) {
			logE("Error!", e)
			notify("Error! ${e.message}") {
				setOngoing(false)
				addReportErrorAction(applicationContext, defaultNotificationID, e)
			}
			return Result.failure()
		}

		if (entity == null) {
			notificationManager.cancel(defaultNotificationID)
		} else {
			notify(
				applicationContext.getString(
					R.string.notification_app_update_available_version,
					entity.version
				)
			) {
				setOngoing(false)
				addAction(
					R.drawable.app_update,
					"",
					PendingIntent.getActivity(
						applicationContext,
						0,
						openAppForUpdateIntent,
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
					)
				)
			}
		}
		return Result.success()
	}

	/**
	 * Manager of [AppUpdateCheckWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {
		private val iSettingsRepository: ISettingsRepository by instance()

		private suspend fun appUpdateOnMetered(): Boolean =
			iSettingsRepository.getBoolean(SettingKey.AppUpdateOnMeteredConnection)

		private suspend fun appUpdateOnlyIdle(): Boolean =
			iSettingsRepository.getBoolean(SettingKey.AppUpdateOnlyWhenIdle)

		override suspend fun getWorkerState(index: Int): WorkInfo.State =
			getWorkerInfoList()[index].state

		override suspend fun getWorkerInfoList(): List<WorkInfo> =
			workerManager.getWorkInfosForUniqueWork(APP_UPDATE_WORK_ID).await()

		override suspend fun getCount(): Int =
			getWorkerInfoList().size

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override suspend fun isRunning(): Boolean = try {
			// Is this running
			val a = (getWorkerState() == WorkInfo.State.RUNNING)

			// Don't run if update is being installed
			val b = !AppUpdateInstallWorker.Manager(context).isRunning()
			a && b
		} catch (e: Exception) {
			false
		}

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 */
		override fun start(data: Data) {
			launchIO {
				logI(LogConstants.SERVICE_NEW)
				workerManager.enqueueUniqueWork(
					APP_UPDATE_WORK_ID,
					ExistingWorkPolicy.REPLACE,
					OneTimeWorkRequestBuilder<AppUpdateCheckWorker>(
					).setConstraints(
						Constraints.Builder().apply {
							setRequiredNetworkType(
								if (appUpdateOnMetered()) CONNECTED else UNMETERED
							)
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
								setRequiresDeviceIdle(appUpdateOnlyIdle())
						}.build()
					).build()
				)
				logI(
					"Worker State ${
						workerManager.getWorkInfosForUniqueWork(APP_UPDATE_WORK_ID).await()[0].state
					}"
				)
			}
		}

		/**
		 * Stops the service.
		 */
		override fun stop(): Operation = workerManager.cancelUniqueWork(APP_UPDATE_WORK_ID)
	}

}