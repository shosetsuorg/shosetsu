package app.shosetsu.android.backend.workers.perodic

import android.content.Context
import android.os.Build
import androidx.work.*
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.onetime.AppUpdateWorker
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.WorkerTags
import app.shosetsu.android.common.consts.WorkerTags.APP_UPDATE_CYCLE_WORK_ID
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.HResult
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
class AppUpdateCycleWorker(
		appContext: Context,
		params: WorkerParameters
) : CoroutineWorker(appContext, params) {

	override suspend fun doWork(): Result {
		logI(LogConstants.SERVICE_EXECUTE)
		AppUpdateWorker.Manager(applicationContext).apply { if (!isRunning()) start() }
		return Result.success()
	}


	/**
	 * Manager of [AppUpdateCycleWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {
		private val iSettingsRepository: ISettingsRepository by instance()

		private suspend fun appUpdateCycle(): Long = iSettingsRepository.getInt(AppUpdateCycle).let {
			if (it is HResult.Success)
				it.data.toLong()
			else AppUpdateCycle.default.toLong()
		}

		private suspend fun appUpdateOnMetered(): Boolean = iSettingsRepository.getBoolean(AppUpdateOnMeteredConnection).let {
			if (it is HResult.Success)
				it.data
			else AppUpdateOnMeteredConnection.default
		}

		private suspend fun appUpdateOnlyIdle(): Boolean = iSettingsRepository.getBoolean(AppUpdateOnlyWhenIdle).let {
			if (it is HResult.Success)
				it.data
			else AppUpdateOnlyWhenIdle.default
		}

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
				logI(LogConstants.SERVICE_NEW)
				workerManager.enqueueUniquePeriodicWork(
						APP_UPDATE_CYCLE_WORK_ID,
						ExistingPeriodicWorkPolicy.REPLACE,
						PeriodicWorkRequestBuilder<AppUpdateCycleWorker>(
								appUpdateCycle(),
								TimeUnit.HOURS
						).setConstraints(Constraints.Builder().apply {
							setRequiredNetworkType(
									if (appUpdateOnMetered()) CONNECTED else UNMETERED
							)
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
								setRequiresDeviceIdle(appUpdateOnlyIdle())
						}.build()
						).build()
				)
				workerManager.getWorkInfosForUniqueWork(APP_UPDATE_CYCLE_WORK_ID).await()[0].let {
					logI("Worker State ${it.state}")
				}
			}
		}

		/**
		 * Stops the service.
		 */
		override fun stop(): Operation = workerManager.cancelUniqueWork(APP_UPDATE_CYCLE_WORK_ID)
	}

}