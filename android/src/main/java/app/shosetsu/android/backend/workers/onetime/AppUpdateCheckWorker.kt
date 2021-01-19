package app.shosetsu.android.backend.workers.onetime

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.getSystemService
import androidx.work.*
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.common.consts.ACTION_OPEN_APP_UPDATE
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.Notifications
import app.shosetsu.android.common.consts.Notifications.ID_APP_UPDATE
import app.shosetsu.android.common.consts.WorkerTags.APP_UPDATE_WORK_ID
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.load.LoadRemoteAppUpdateUseCase
import app.shosetsu.android.ui.splash.SplashScreen
import app.shosetsu.common.consts.settings.SettingKey.AppUpdateOnMeteredConnection
import app.shosetsu.common.consts.settings.SettingKey.AppUpdateOnlyWhenIdle
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.transmogrify
import com.github.doomsdayrs.apps.shosetsu.R
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

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
) : CoroutineWorker(appContext, params), KodeinAware {
	override val kodein: Kodein by closestKodein(applicationContext)
	private val openAppForUpdateIntent: Intent
		get() = Intent(applicationContext, SplashScreen::class.java).apply {
			action = ACTION_OPEN_APP_UPDATE
		}

	private val loadRemoteAppUpdateUseCase by instance<LoadRemoteAppUpdateUseCase>()
	private val notificationManager: NotificationManager by lazy { appContext.getSystemService()!! }
	private val reportExceptionUseCase by instance<ReportExceptionUseCase>()

	private val progressNotification by lazy {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Notification.Builder(appContext, Notifications.CHANNEL_APP_UPDATE)
		} else {
			// Suppressed due to lower API
			@Suppress("DEPRECATION")
			Notification.Builder(appContext)
		}
			.setContentTitle(applicationContext.getString(R.string.notification_app_update_check))
			.setSmallIcon(R.drawable.app_update)
			.setOnlyAlertOnce(true)
	}

	override suspend fun doWork(): Result {
		try {
			val pr = progressNotification
			pr.setOngoing(true)
			notificationManager.notify(ID_APP_UPDATE, pr.build())
			val result = loadRemoteAppUpdateUseCase()
			pr.setOngoing(false)
			result.handle(onEmpty = {
				notificationManager.cancel(ID_APP_UPDATE)
			}, onError = {
				logE("Error!", it.exception)
				pr.setContentText("Error! ${it.code} | ${it.message}")
				notificationManager.notify(ID_APP_UPDATE, pr.build())
			}) {
				pr.setContentText(
					applicationContext.getString(R.string.notification_app_update_available)
							+ " " + it.version
				)
				notificationManager.notify(ID_APP_UPDATE, pr.build())
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
			iSettingsRepository.getBoolean(AppUpdateOnMeteredConnection).transmogrify {
				it
			} ?: AppUpdateOnMeteredConnection.default


		private suspend fun appUpdateOnlyIdle(): Boolean =
			iSettingsRepository.getBoolean(AppUpdateOnlyWhenIdle).transmogrify {
				it
			} ?: AppUpdateOnlyWhenIdle.default

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override fun isRunning(): Boolean = try {
			// Is this running
			val a = (workerManager.getWorkInfosForUniqueWork(APP_UPDATE_WORK_ID)
				.get()[0].state == WorkInfo.State.RUNNING)

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
		override fun start() {
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