package app.shosetsu.android.backend.workers.perodic

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.work.*
import androidx.work.ExistingPeriodicWorkPolicy.REPLACE
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.onetime.UpdateWorker
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.WorkerTags.UPDATE_CYCLE_WORK_ID
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.common.com.consts.settings.SettingKey.*
import app.shosetsu.common.com.dto.HResult
import org.kodein.di.generic.instance
import java.util.concurrent.TimeUnit.HOURS
import androidx.work.PeriodicWorkRequestBuilder as PWRB

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
 * 07 / 02 / 2020
 *
 * <p>
 *     Handles update requests for the entire application
 * </p>
 */
class UpdateCycleWorker(
		appContext: Context,
		params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
	/**
	 * Manager of [UpdateCycleWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {
		private val iSettingsRepository by instance<ISettingsRepository>()


		private suspend fun updateCycle(): Long = iSettingsRepository.getInt(UpdateCycle).let {
			if (it is HResult.Success)
				it.data.toLong()
			else UpdateCycle.default.toLong()
		}

		private suspend fun updateOnMetered(): Boolean =
				iSettingsRepository.getBoolean(UpdateOnMeteredConnection).let {
					if (it is HResult.Success)
						it.data
					else UpdateOnMeteredConnection.default
				}

		private suspend fun updateOnLowStorage(): Boolean =
				iSettingsRepository.getBoolean(UpdateOnLowStorage).let {
					if (it is HResult.Success)
						it.data
					else UpdateOnLowStorage.default
				}

		private suspend fun updateOnLowBattery(): Boolean =
				iSettingsRepository.getBoolean(UpdateOnLowBattery).let {
					if (it is HResult.Success)
						it.data
					else UpdateOnLowBattery.default
				}

		private suspend fun updateOnlyIdle(): Boolean =
				iSettingsRepository.getBoolean(UpdateOnlyWhenIdle).let {
					if (it is HResult.Success)
						it.data
					else UpdateOnlyWhenIdle.default
				}

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override fun isRunning(): Boolean = try {
			workerManager.getWorkInfosForUniqueWork(UPDATE_CYCLE_WORK_ID)
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
				Log.i(logID(), LogConstants.SERVICE_NEW)
				workerManager.enqueueUniquePeriodicWork(
						UPDATE_CYCLE_WORK_ID,
						REPLACE,
						PWRB<UpdateCycleWorker>(
								updateCycle(),
								HOURS
						).setConstraints(
								Constraints.Builder().apply {
									setRequiredNetworkType(
											if (updateOnMetered()) {
												CONNECTED
											} else UNMETERED
									)
									setRequiresStorageNotLow(!updateOnLowStorage())
									setRequiresBatteryNotLow(!updateOnLowBattery())
									if (SDK_INT >= VERSION_CODES.M)
										setRequiresDeviceIdle(updateOnlyIdle())
								}.build()
						)
								.build()
				)
				workerManager.getWorkInfosForUniqueWork(UPDATE_CYCLE_WORK_ID).get()[0].let {
					Log.d(logID(), "State ${it.state}")
				}
			}
		}

		/**
		 * Stops the service.
		 */
		override fun stop(): Operation = workerManager.cancelUniqueWork(UPDATE_CYCLE_WORK_ID)
	}


	override suspend fun doWork(): Result {
		logI(LogConstants.SERVICE_EXECUTE)
		UpdateWorker.Manager(applicationContext).apply { if (!isRunning()) start() }
		return Result.success()
	}
}