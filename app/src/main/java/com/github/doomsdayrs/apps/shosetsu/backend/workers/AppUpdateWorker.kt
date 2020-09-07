package com.github.doomsdayrs.apps.shosetsu.backend.workers

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import androidx.work.*
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.common.consts.LogConstants
import com.github.doomsdayrs.apps.shosetsu.common.consts.Notifications
import com.github.doomsdayrs.apps.shosetsu.common.consts.Notifications.ID_APP_UPDATE
import com.github.doomsdayrs.apps.shosetsu.common.consts.WorkerTags
import com.github.doomsdayrs.apps.shosetsu.common.consts.WorkerTags.APP_UPDATE_WORK_ID
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.LoadAppUpdateUseCase
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.util.concurrent.TimeUnit

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

		when (result) {
			is HResult.Success -> {
				pr.setContentText(
						applicationContext.getString(R.string.app_update_available)
								+ " " + result.data.version
				)
				notificationManager.notify(ID_APP_UPDATE, pr.build())
			}
			is HResult.Empty -> {
				pr.setContentText(applicationContext.getString(R.string.app_update_unavaliable))
				notificationManager.notify(ID_APP_UPDATE, pr.build())
			}
			is HResult.Error -> {
				Log.e(logID(), "Error!", result.error)
				pr.setContentText("Error! ${result.code} | ${result.message}")
				notificationManager.notify(ID_APP_UPDATE, pr.build())
			}
		}
		return Result.success()
	}


	/**
	 * Manager of [AppUpdateWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {
		private val settings: ShosetsuSettings by instance()


		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override fun isRunning(): Boolean = try {
			workerManager.getWorkInfosForUniqueWork(WorkerTags.UPDATE_WORK_ID)
					.get()[0].state == WorkInfo.State.RUNNING
		} catch (e: Exception) {
			false
		}

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 */
		override fun start() {
			Log.i(logID(), LogConstants.SERVICE_NEW)
			workerManager.enqueueUniquePeriodicWork(
					APP_UPDATE_WORK_ID,
					ExistingPeriodicWorkPolicy.REPLACE,
					PeriodicWorkRequestBuilder<AppUpdateWorker>(
							settings.appUpdateCycle.toLong(),
							TimeUnit.HOURS
					).setConstraints(Constraints.Builder().apply {
						setRequiredNetworkType(
								if (settings.appUpdateOnMetered) CONNECTED else UNMETERED
						)
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
							setRequiresDeviceIdle(settings.appUpdateOnlyIdle)
					}.build()
					).build()
			)
			workerManager.getWorkInfosForUniqueWork(APP_UPDATE_WORK_ID).get()[0].let {
				Log.d(logID(), "Worker State ${it.state}")
			}
		}

		/**
		 * Stops the service.
		 */
		override fun stop(): Operation = workerManager.cancelUniqueWork(APP_UPDATE_WORK_ID)
	}

}