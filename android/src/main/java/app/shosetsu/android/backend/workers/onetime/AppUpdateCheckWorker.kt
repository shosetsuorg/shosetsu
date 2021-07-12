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
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.consts.ACTION_OPEN_APP_UPDATE
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.Notifications
import app.shosetsu.android.common.consts.Notifications.ID_APP_UPDATE
import app.shosetsu.android.common.consts.WorkerTags.APP_UPDATE_WORK_ID
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.load.LoadRemoteAppUpdateUseCase
import app.shosetsu.android.ui.splash.SplashScreen
import app.shosetsu.common.consts.settings.SettingKey.AppUpdateOnMeteredConnection
import app.shosetsu.common.consts.settings.SettingKey.AppUpdateOnlyWhenIdle
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.domain.repositories.base.getBooleanOrDefault
import app.shosetsu.common.dto.handle
import com.github.doomsdayrs.apps.shosetsu.R
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

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
		get() = Intent(applicationContext, SplashScreen::class.java).apply {
			action = ACTION_OPEN_APP_UPDATE
		}
	override val defaultNotificationID: Int = ID_APP_UPDATE

	private val loadRemoteAppUpdateUseCase by instance<LoadRemoteAppUpdateUseCase>()
	override val notificationManager: NotificationManagerCompat by notificationManager()
	private val reportExceptionUseCase by instance<ReportExceptionUseCase>()

	override val baseNotificationBuilder: NotificationCompat.Builder
		get() = notificationBuilder(applicationContext, Notifications.CHANNEL_APP_UPDATE)
			.setSubText(applicationContext.getString(R.string.notification_app_update_check))
			.setSmallIcon(R.drawable.app_update)
			.setOnlyAlertOnce(true)
			.setOngoing(true)

	override val notifyContext: Context
		get() = applicationContext


	override suspend fun doWork(): Result {
		try {
			notify("Starting")
			loadRemoteAppUpdateUseCase().handle(onEmpty = {
				notificationManager.cancel(defaultNotificationID)
			}, onError = {
				logE("Error!", it.exception)
				notify("Error! ${it.code} | ${it.message}") {
					setOngoing(false)
				}
			}) {
				notify(
					applicationContext.getString(
						R.string.notification_app_update_available_version,
						it.version
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
							0
						)
					)
				}
			}
			return Result.success()
		} catch (e: Exception) {
			reportExceptionUseCase(e.toHError())
		}
		return Result.failure()
	}

	/**
	 * Manager of [AppUpdateCheckWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {
		private val iSettingsRepository: ISettingsRepository by instance()

		private suspend fun appUpdateOnMetered(): Boolean =
			iSettingsRepository.getBooleanOrDefault(AppUpdateOnMeteredConnection)


		private suspend fun appUpdateOnlyIdle(): Boolean =
			iSettingsRepository.getBooleanOrDefault(AppUpdateOnlyWhenIdle)

		override fun getWorkerState(index: Int): WorkInfo.State =
			workerManager.getWorkInfosForUniqueWork(APP_UPDATE_WORK_ID).get()[index].state

		override val count: Int
			get() = workerManager.getWorkInfosForUniqueWork(APP_UPDATE_WORK_ID).get().size

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override fun isRunning(): Boolean = try {
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