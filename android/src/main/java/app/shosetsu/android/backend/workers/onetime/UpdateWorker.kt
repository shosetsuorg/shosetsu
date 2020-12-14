package app.shosetsu.android.backend.workers.onetime

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.core.content.getSystemService
import androidx.work.*
import androidx.work.ExistingWorkPolicy.REPLACE
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.Notifications.CHANNEL_UPDATE
import app.shosetsu.android.common.consts.Notifications.ID_CHAPTER_UPDATE
import app.shosetsu.android.common.consts.WorkerTags.UPDATE_WORK_ID
import app.shosetsu.android.common.ext.combine
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.android.domain.usecases.StartDownloadWorkerUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUseCase
import app.shosetsu.android.domain.usecases.toast.ToastErrorUseCase
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle
import app.shosetsu.lib.Novel
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
 * 07 / 02 / 2020
 *
 * <p>
 *     Handles update requests for the entire application
 * </p>
 */
class UpdateWorker(
	appContext: Context,
	params: WorkerParameters,
) : CoroutineWorker(appContext, params), KodeinAware {
	companion object {
		const val KEY_TARGET: String = "Target"
		const val KEY_CHAPTERS: String = "Novels"

		const val KEY_NOVELS: Int = 0x00
		const val KEY_CATEGORY: Int = 0x01
	}

	/**
	 * Manager of [UpdateWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {
		private val iSettingsRepository by instance<ISettingsRepository>()

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
			workerManager.getWorkInfosForUniqueWork(UPDATE_WORK_ID)
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
				workerManager.enqueueUniqueWork(
					UPDATE_WORK_ID,
					REPLACE,
					OneTimeWorkRequestBuilder<UpdateWorker>().setConstraints(
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
					).build()
				)
				workerManager.getWorkInfosForUniqueWork(UPDATE_WORK_ID).await()[0].let {
					Log.d(logID(), "State ${it.state}")
				}
			}
		}

		/**
		 * Stops the service.
		 */
		override fun stop(): Operation = workerManager.cancelUniqueWork(UPDATE_WORK_ID)
	}

	private val notificationManager by lazy { appContext.getSystemService<NotificationManager>()!! }

	private val progressNotification by lazy {
		if (SDK_INT >= VERSION_CODES.O) {
			Notification.Builder(appContext, CHANNEL_UPDATE)
		} else {
			// Suppressed due to lower API
			@Suppress("DEPRECATION")
			Notification.Builder(appContext)
		}
			.setSmallIcon(R.drawable.refresh)
			.setContentText("Update in progress")
			.setOnlyAlertOnce(true)
	}

	override val kodein: Kodein by closestKodein(appContext)
	private val iNovelsRepository: INovelsRepository by instance()
	private val loadNovelUseCase: LoadNovelUseCase by instance()
	private val toastErrorUseCase: ToastErrorUseCase by instance()
	private val startDownloadWorker: StartDownloadWorkerUseCase by instance()
	private val iSettingsRepository: ISettingsRepository by instance()

	private suspend fun onlyUpdateOngoing(): Boolean =
		iSettingsRepository.getBoolean(OnlyUpdateOngoing).let {
			if (it is HResult.Success)
				it.data
			else OnlyUpdateOngoing.default
		}

	private suspend fun downloadOnUpdate(): Boolean =
		iSettingsRepository.getBoolean(IsDownloadOnUpdate).let {
			if (it is HResult.Success)
				it.data
			else IsDownloadOnUpdate.default
		}

	override suspend fun doWork(): Result {
		Log.i(logID(), LogConstants.SERVICE_EXECUTE)
		val pr = progressNotification
		pr.setContentTitle(applicationContext.getString(R.string.update))
		pr.setOngoing(true)
		notificationManager.notify(ID_CHAPTER_UPDATE, pr.build())

		val updateNovels = arrayListOf<NovelEntity>()

		iNovelsRepository.getBookmarkedNovels().let { hNovels ->
			when (hNovels) {
				is HResult.Success -> {
					val novels = hNovels.data.let { list: List<NovelEntity> ->
						if (onlyUpdateOngoing())
							list.filter { it.status == Novel.Status.PUBLISHING }
						else list
					}
					var progress = 0

					novels.forEach { nE ->
						pr.setContentText(applicationContext.getString(R.string.updating) + nE.title)
						pr.setProgress(novels.size, progress, false)
						notificationManager.notify(ID_CHAPTER_UPDATE, pr.build())
						loadNovelUseCase(nE, true) {
							updateNovels.add(nE)
						}.handle(onError = { toastErrorUseCase<UpdateWorker>(it) })
						progress++
					}

					pr.setContentTitle(applicationContext.getString(R.string.update))
					pr.setContentText(
						applicationContext.getString(R.string.update_complete) + "\n" +
								combine(",\n")
					)
					pr.setOngoing(false)
					pr.setProgress(0, 0, false)
					notificationManager.notify(ID_CHAPTER_UPDATE, pr.build())

					if (updateNovels.isEmpty()) notificationManager.cancel(ID_CHAPTER_UPDATE)
				}
				else -> {
					return Result.failure()
				}
			}
		}

		// Will update only if downloadOnUpdate is enabled and there have been chapters
		if (downloadOnUpdate() && updateNovels.isNotEmpty())
			startDownloadWorker()

		return Result.success()
	}
}