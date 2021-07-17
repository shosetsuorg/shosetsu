package app.shosetsu.android.backend.workers.onetime

import android.content.Context
import android.graphics.Bitmap
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import androidx.work.ExistingWorkPolicy.REPLACE
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.LogConstants.SERVICE_EXECUTE
import app.shosetsu.android.common.consts.Notifications.CHANNEL_UPDATE
import app.shosetsu.android.common.consts.Notifications.ID_CHAPTER_UPDATE
import app.shosetsu.android.common.consts.WorkerTags.UPDATE_WORK_ID
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.usecases.StartDownloadWorkerAfterUpdateUseCase
import app.shosetsu.android.domain.usecases.get.GetRemoteNovelUseCase
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.domain.repositories.base.INovelsRepository
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.domain.repositories.base.getBooleanOrDefault
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.transformToSuccess
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
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
 * 07 / 02 / 2020
 *
 * <p>
 *     Handles update requests for the entire application
 * </p>
 */
class NovelUpdateWorker(
	appContext: Context,
	params: WorkerParameters,
) : CoroutineWorker(appContext, params), DIAware, NotificationCapable {
	override val notifyContext: Context
		get() = applicationContext

	override val defaultNotificationID: Int = ID_CHAPTER_UPDATE

	override val notificationManager: NotificationManagerCompat by notificationManager()
	override val baseNotificationBuilder: NotificationCompat.Builder
		get() = notificationBuilder(applicationContext, CHANNEL_UPDATE)
			.setSmallIcon(R.drawable.refresh)
			.setSubText(applicationContext.getString(R.string.update_novel))
			.setContentText("Update in progress")
			.setOnlyAlertOnce(true)


	override val di: DI by closestDI(appContext)

	private val iNovelsRepository: INovelsRepository by instance()
	private val loadRemoteNovelUseCase: GetRemoteNovelUseCase by instance()
	private val startDownloadWorker: StartDownloadWorkerAfterUpdateUseCase by instance()
	private val iSettingsRepository: ISettingsRepository by instance()

	private suspend fun onlyUpdateOngoing(): Boolean =
		iSettingsRepository.getBooleanOrDefault(OnlyUpdateOngoing)

	private suspend fun downloadOnUpdate(): Boolean =
		iSettingsRepository.getBooleanOrDefault(IsDownloadOnUpdate)

	private suspend fun notificationStyle(): Boolean =
		iSettingsRepository.getBooleanOrDefault(UpdateNotificationStyle)

	private suspend fun showProgress(): Boolean =
		iSettingsRepository.getBooleanOrDefault(NovelUpdateShowProgress)

	private suspend fun classicFinale(): Boolean =
		iSettingsRepository.getBooleanOrDefault(NovelUpdateClassicFinish)

	override suspend fun doWork(): Result {
		// Log that the worker is executing
		logI(SERVICE_EXECUTE)

		// Notify the user the worker is working
		notify(R.string.update) {
			setOngoing()
		}

		/** Count of novels that have been updated */
		val updateNovels = arrayListOf<NovelEntity>()

		/** Collect updated chapters to be used */
		val updatedChapters = arrayListOf<ChapterEntity>()

		iNovelsRepository.loadBookmarkedNovelEntities().transformToSuccess { list ->
			if (onlyUpdateOngoing())
				list.filter { it.status == Novel.Status.PUBLISHING }
			else list
		}.handle(
			onError = { return Result.failure() },
			onLoading = { return Result.failure() },
			onEmpty = { return Result.failure() },
		) { novels ->
			var progress = 0

			novels.forEach { nE ->
				val style = notificationStyle()
				val title: String =
					if (style) nE.title else applicationContext.getString(R.string.updating)
				val content: String = if (style) "" else nE.title

				if (showProgress()) {
					notify(content) {
						setContentTitle(title)
						setProgress(novels.size, progress, false)
						setOngoing()
						setSilent(true)
					}
				} else notify(R.string.worker_novel_updating_silent) {
					setOngoing()
					setSilent(true)
				}

				loadRemoteNovelUseCase(nE, true).handle(
					onError = {
						logE("Failed to load novel: $nE", it.exception)
						notify(
							"${it.code} : ${it.message}",
							10000 + nE.id!!
						) {
							setContentTitle(
								getString(
									R.string.worker_novel_update_load_failure,
									nE.title
								)
							)

							setNotOngoing()
							removeProgress()
							this.priority = NotificationCompat.PRIORITY_HIGH
						}
					}
				) {
					if (it.updatedChapters.isNotEmpty()) {
						updateNovels.add(nE)
						updatedChapters.addAll(it.updatedChapters)
					}
				}
				progress++
			}

			notify(R.string.update_complete) {
				setNotOngoing()
				removeProgress()
			}

			// Get rid of the complete notification after 5 seconds
			if (!classicFinale())
				launchIO {
					delay(5000)
					notificationManager.cancel(defaultNotificationID)
				}
		}

		if (!classicFinale())
			for (novel in updateNovels) {
				launchIO { // Run each novel notification on it's own seperate thread
					val chapterSize: Int = updatedChapters.filter { it.novelID == novel.id }.size
					notify(
						applicationContext.resources.getQuantityString(
							R.plurals.worker_novel_update_updated_novel_count,
							chapterSize,
							chapterSize
						),
						10000 + novel.id!!
					) {
						setContentTitle(
							getString(
								R.string.worker_novel_update_updated_novel,
								novel.title
							)
						)
						val bitmap: Bitmap? = try {
							Picasso.get().load(novel.imageURL).get()
						} catch (e: Exception) {
							null
						}

						setLargeIcon(bitmap)

						setNotOngoing()
						removeProgress()
					}
				}
			}

		// Will update only if downloadOnUpdate is enabled and there have been chapters
		if (downloadOnUpdate() && updateNovels.size > 0 && updatedChapters.size > 0)
			startDownloadWorker(updatedChapters)


		return Result.success()
	}

	/**
	 * Manager of [NovelUpdateWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {
		private val iSettingsRepository by instance<ISettingsRepository>()

		private suspend fun updateOnMetered(): Boolean =
			iSettingsRepository.getBooleanOrDefault(UpdateOnMeteredConnection)

		private suspend fun updateOnLowStorage(): Boolean =
			iSettingsRepository.getBooleanOrDefault(UpdateOnLowStorage)

		private suspend fun updateOnLowBattery(): Boolean =
			iSettingsRepository.getBooleanOrDefault(UpdateOnLowBattery)

		private suspend fun updateOnlyIdle(): Boolean =
			iSettingsRepository.getBooleanOrDefault(UpdateOnlyWhenIdle)

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override fun isRunning(): Boolean = try {
			getWorkerState() == WorkInfo.State.RUNNING
		} catch (e: Exception) {
			false
		}

		override fun getWorkerState(index: Int): WorkInfo.State =
			workerManager.getWorkInfosForUniqueWork(UPDATE_WORK_ID).get()[index].state

		override val count: Int
			get() = workerManager.getWorkInfosForUniqueWork(UPDATE_WORK_ID).get().size

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 */
		override fun start(data: Data) {
			launchIO {
				logI(LogConstants.SERVICE_NEW)
				workerManager.enqueueUniqueWork(
					UPDATE_WORK_ID,
					REPLACE,
					OneTimeWorkRequestBuilder<NovelUpdateWorker>().setConstraints(
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

	companion object {
		const val KEY_TARGET: String = "Target"
		const val KEY_CHAPTERS: String = "Novels"

		const val KEY_NOVELS: Int = 0x00
		const val KEY_CATEGORY: Int = 0x01
	}
}