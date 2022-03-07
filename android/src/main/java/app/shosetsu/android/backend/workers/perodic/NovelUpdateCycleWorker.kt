package app.shosetsu.android.backend.workers.perodic

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import androidx.work.*
import androidx.work.ExistingPeriodicWorkPolicy.REPLACE
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.onetime.NovelUpdateWorker
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.WorkerTags.UPDATE_CYCLE_WORK_ID
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logD
import app.shosetsu.android.common.ext.logI
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import org.kodein.di.instance
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
class NovelUpdateCycleWorker(
	appContext: Context,
	params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
	override suspend fun doWork(): Result {
		logI(LogConstants.SERVICE_EXECUTE)
		val manager = NovelUpdateWorker.Manager(applicationContext)

		when (manager.getWorkerState()) {
			WorkInfo.State.ENQUEUED -> {
				logI("NovelUpdater is waiting to update, ignoring")
			}
			WorkInfo.State.RUNNING -> {
				logI("NovelUpdater is running, ignoring")
			}
			WorkInfo.State.SUCCEEDED -> {
				logI("NovelUpdater has completed, starting again")
				manager.start()
			}
			WorkInfo.State.FAILED -> {
				logI("Previous NovelUpdater has failed, starting again")
				manager.start()
			}
			WorkInfo.State.BLOCKED -> {
				logI("Previous NovelUpdater is blocked, ignoring")
			}
			WorkInfo.State.CANCELLED -> {
				logI("Previous NovelUpdater was cancelled, starting again")
				manager.start()
			}
		}

		return Result.success()
	}

	/**
	 * Manager of [NovelUpdateCycleWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {
		private val iSettingsRepository by instance<ISettingsRepository>()


		private suspend fun updateCycle(): Long =
			iSettingsRepository.getInt(NovelUpdateCycle).toLong()

		private suspend fun updateOnMetered(): Boolean =
			iSettingsRepository.getBoolean(NovelUpdateOnMeteredConnection)

		private suspend fun updateOnLowStorage(): Boolean =
			iSettingsRepository.getBoolean(NovelUpdateOnLowStorage)

		private suspend fun updateOnLowBattery(): Boolean =
			iSettingsRepository.getBoolean(NovelUpdateOnLowBattery)

		private suspend fun updateOnlyIdle(): Boolean =
			iSettingsRepository.getBoolean(NovelUpdateOnlyWhenIdle)

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override suspend fun isRunning(): Boolean = try {
			getWorkerState() == WorkInfo.State.RUNNING
		} catch (e: Exception) {
			false
		}

		override suspend fun getWorkerState(index: Int): WorkInfo.State =
			getWorkerInfoList()[index].state

		override suspend fun getWorkerInfoList(): List<WorkInfo> =
			workerManager.getWorkInfosForUniqueWork(UPDATE_CYCLE_WORK_ID).await()


		override suspend fun getCount(): Int =
			getWorkerInfoList().size


		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 */
		override fun start(data: Data) {
			launchIO {
				logI(LogConstants.SERVICE_NEW)
				workerManager.enqueueUniquePeriodicWork(
					UPDATE_CYCLE_WORK_ID,
					REPLACE,
					PWRB<NovelUpdateCycleWorker>(
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
				workerManager.getWorkInfosForUniqueWork(UPDATE_CYCLE_WORK_ID).await()[0].let {
					logD("State ${it.state}")
				}
			}
		}

		/**
		 * Stops the service.
		 */
		override fun stop(): Operation = workerManager.cancelUniqueWork(UPDATE_CYCLE_WORK_ID)
	}
}