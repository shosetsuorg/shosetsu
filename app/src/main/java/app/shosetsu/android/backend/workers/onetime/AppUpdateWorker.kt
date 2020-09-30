package app.shosetsu.android.backend.workers.onetime

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import androidx.work.*
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.Notifications
import app.shosetsu.android.common.consts.Notifications.ID_APP_UPDATE
import app.shosetsu.android.common.consts.WorkerTags
import app.shosetsu.android.common.consts.WorkerTags.APP_UPDATE_WORK_ID
import app.shosetsu.android.common.consts.settings.SettingKey.*
import app.shosetsu.android.common.dto.handle
import app.shosetsu.android.common.dto.handledReturnAny
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.domain.usecases.load.LoadAppUpdateUseCase
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
 */
class AppUpdateWorker(
		appContext: Context,
		params: WorkerParameters
) : CoroutineWorker(appContext, params), KodeinAware {
	override val kodein: Kodein by closestKodein(applicationContext)
	private val appUpdateUseCase by instance<LoadAppUpdateUseCase>()
	private val notificationManager: NotificationManager by lazy { appContext.getSystemService()!! }
	private val progressNotification by lazy {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Notification.Builder(appContext, Notifications.CHANNEL_APP_UPDATE)
		} else {
			// Suppressed due to lower API
			@Suppress("DEPRECATION")
			Notification.Builder(appContext)
		}
				.setContentTitle(applicationContext.getString(R.string.app_update_check))
				.setSmallIcon(R.drawable.ic_system_update_alt_24dp)
				.setOnlyAlertOnce(true)
	}

	override suspend fun doWork(): Result {
		val pr = progressNotification
		pr.setOngoing(true)
		notificationManager.notify(ID_APP_UPDATE, pr.build())

		val result = appUpdateUseCase()
		pr.setOngoing(false)
		result.handle(onEmpty = {
			pr.setContentText(applicationContext.getString(R.string.app_update_unavaliable))
			notificationManager.notify(ID_APP_UPDATE, pr.build())
		}, onError = {
			Log.e(logID(), "Error!", it.error)
			pr.setContentText("Error! ${it.code} | ${it.message}")
			notificationManager.notify(ID_APP_UPDATE, pr.build())
		}) {
			pr.setContentText(
					applicationContext.getString(R.string.app_update_available)
							+ " " + it.version
			)
			notificationManager.notify(ID_APP_UPDATE, pr.build())
		}
		return Result.success()
	}


	/**
	 * Manager of [AppUpdateWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {
		private val iSettingsRepository: ISettingsRepository by instance()

		private suspend fun appUpdateOnMetered(): Boolean =
				iSettingsRepository.getBoolean(AppUpdateOnMeteredConnection).handledReturnAny {
					it
				} ?: AppUpdateOnMeteredConnection.default


		private suspend fun appUpdateOnlyIdle(): Boolean =
				iSettingsRepository.getBoolean(AppUpdateOnlyWhenIdle).handledReturnAny {
					it
				} ?: AppUpdateOnlyWhenIdle.default

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override fun isRunning(): Boolean = try {
			workerManager.getWorkInfosForUniqueWork(WorkerTags.UPDATE_CYCLE_WORK_ID)
					.get()[0].state == WorkInfo.State.RUNNING
		} catch (e: Exception) {
			false
		}

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 */
		override fun start() {
			launchIO {
				this@Manager.logI(LogConstants.SERVICE_NEW)
				workerManager.enqueueUniqueWork(
						APP_UPDATE_WORK_ID,
						ExistingWorkPolicy.REPLACE,
						OneTimeWorkRequestBuilder<AppUpdateWorker>(
						).setConstraints(Constraints.Builder().apply {
							setRequiredNetworkType(
									if (appUpdateOnMetered()) CONNECTED else UNMETERED
							)
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
								setRequiresDeviceIdle(appUpdateOnlyIdle())
						}.build()
						).build()
				)
				workerManager.getWorkInfosForUniqueWork(APP_UPDATE_WORK_ID).get()[0].let {
					this@Manager.logI("Worker State ${it.state}")
				}
			}
		}

		/**
		 * Stops the service.
		 */
		override fun stop(): Operation = workerManager.cancelUniqueWork(APP_UPDATE_WORK_ID)
	}

}