package app.shosetsu.android.backend.workers.onetime

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.work.*
import androidx.work.ExistingWorkPolicy.REPLACE
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import app.shosetsu.android.R
import app.shosetsu.android.backend.receivers.NotificationBroadcastReceiver
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.SettingKey.*
import app.shosetsu.android.common.consts.BundleKeys
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.LogConstants.SERVICE_EXECUTE
import app.shosetsu.android.common.consts.Notifications.CHANNEL_UPDATE
import app.shosetsu.android.common.consts.Notifications.ID_CHAPTER_UPDATE
import app.shosetsu.android.common.consts.WorkerTags.UPDATE_WORK_ID
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.domain.model.local.LibraryNovelEntity
import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.domain.usecases.StartDownloadWorkerAfterUpdateUseCase
import app.shosetsu.android.domain.usecases.get.GetRemoteNovelUseCase
import app.shosetsu.android.ui.reader.ChapterReader
import app.shosetsu.lib.Novel
import app.shosetsu.lib.exceptions.HTTPException
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import org.luaj.vm2.LuaError
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

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

	private fun NotificationCompat.Builder.addCancelAction() {
		addAction(
			R.drawable.ic_baseline_cancel_24, getString(android.R.string.cancel),
			PendingIntent.getBroadcast(
				applicationContext,
				0,
				Intent(applicationContext, NotificationBroadcastReceiver::class.java).apply {
					action = ACTION_CANCEL_NOVEL_UPDATE
					putExtra(EXTRA_NOTIFICATION_ID, defaultNotificationID)
				},
				if (SDK_INT >= VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
			)
		)
	}


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
		iSettingsRepository.getBoolean(OnlyUpdateOngoingNovels)

	private suspend fun includedCategoriesInLibraryUpdate(): List<Int> =
		iSettingsRepository.getStringSet(IncludeCategoriesInUpdate).map(String::toInt)

	private suspend fun excludedCategoriesInLibraryUpdate(): List<Int> =
		iSettingsRepository.getStringSet(ExcludedCategoriesInUpdate).map(String::toInt)

	private suspend fun downloadOnUpdate(): Boolean =
		iSettingsRepository.getBoolean(DownloadNewNovelChapters)

	private suspend fun notificationStyle(): Boolean =
		iSettingsRepository.getBoolean(UpdateNotificationStyle)

	private suspend fun showProgress(): Boolean =
		iSettingsRepository.getBoolean(NovelUpdateShowProgress)

	private suspend fun classicFinale(): Boolean =
		iSettingsRepository.getBoolean(NovelUpdateClassicFinish)

	override suspend fun doWork(): Result {
		// Log that the worker is executing
		logI(SERVICE_EXECUTE)

		// Notify the user the worker is working
		notify(R.string.update) {
			setOngoing()
			addCancelAction()
		}

		/** Count of novels that have been updated */
		val updateNovels = arrayListOf<LibraryNovelEntity>()

		/** Collect updated chapters to be used */
		val updatedChapters = arrayListOf<ChapterEntity>()

		iNovelsRepository.loadLibraryNovelEntities().first().let { list ->
			val categoryID = inputData.getInt(KEY_CATEGORY, -1)
			if (categoryID >= 0) {
				list.filter { it.category == categoryID }
			} else list
		}.let { list ->
			if (onlyUpdateOngoing())
				list.filter { it.status != Novel.Status.COMPLETED }
			else list
		}.let { list ->
			val includedCategories = includedCategoriesInLibraryUpdate()
			val includedNovels = if (includedCategories.isNotEmpty()) {
				list.filter { it.category in includedCategories }
			} else {
				list
			}

			val excludedCategories = excludedCategoriesInLibraryUpdate()
			val excludedNovels = if (excludedCategories.isNotEmpty()) {
				list.filter { it.category in excludedCategories }.map { it.id }.toSet()
			} else {
				emptySet()
			}

			includedNovels.filterNot { it.id in excludedNovels }
		}.let { list ->
			list.distinctBy { it.id }
				.sortedBy { it.title }
		}.let { novels ->
			var progress = 0

			for (nE in novels) {
				if (isStopped) break

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
						addCancelAction()
					}
				} else notify(R.string.worker_novel_updating_silent) {
					setOngoing()
					setSilent(true)
					addCancelAction()
				}

				val it = try {
					loadRemoteNovelUseCase(nE.id, true)
				} catch (e: LuaError) {
					logE("Failed to load novel: $nE", e)
					notify(
						"${e.message}",
						10000 + nE.id
					) {
						setContentTitle(
							getString(
								R.string.worker_novel_update_load_failure,
								nE.title
							)
						)

						setNotOngoing()
						removeProgress()
						addCancelAction()
						this.priority = NotificationCompat.PRIORITY_HIGH
					}
					continue
				} catch (e: HTTPException) {
					logE("Failed to load novel: $nE", e)
					notify(
						"${e.message}",
						10000 + nE.id
					) {
						setContentTitle(
							getString(
								R.string.worker_novel_update_load_failure,
								nE.title
							)
						)

						setNotOngoing()
						removeProgress()
						addCancelAction()
						this.priority = NotificationCompat.PRIORITY_HIGH
					}
					continue
				} catch (e: IOException) {
					logE("Failed to load novel: $nE", e)
					notify(
						"${e.message}",
						10000 + nE.id
					) {
						setContentTitle(
							getString(
								R.string.worker_novel_update_load_failure,
								nE.title
							)
						)

						setNotOngoing()
						removeProgress()
						addCancelAction()
						this.priority = NotificationCompat.PRIORITY_HIGH
					}
					continue
				} catch (e: Exception) {
					if (e is CancellationException) {
						logE("Job was canceled", e)
						continue
					}
					logE("Failed to load novel: $nE", e)
					notify(
						"${e.message}",
						10000 + nE.id
					) {
						setContentTitle(
							getString(
								R.string.worker_novel_update_load_failure,
								nE.title
							)
						)

						setNotOngoing()
						removeProgress()
						addCancelAction()
						this.priority = NotificationCompat.PRIORITY_HIGH

						addReportErrorAction(
							applicationContext,
							10000 + nE.id,
							e
						)
					}
					continue
				}
				if (it != null)
					if (it.updatedChapters.isNotEmpty()) {
						updateNovels.add(nE)
						updatedChapters.addAll(it.updatedChapters)
					}
				//TODO Handle null
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

		// If not the classic finale, create a notification for each novel about its updated chaps
		if (!classicFinale())
			for (novel in updateNovels) {
				launchIO { // Run each novel notification on it's own seperate thread
					val uniqueChapters = updatedChapters.filter { it.novelID == novel.id }
					val chapterSize: Int = uniqueChapters.size
					val firstChapterId = uniqueChapters.minByOrNull { it.order }?.id
					val bitmap: Bitmap? =
						applicationContext.imageLoader.execute(
							ImageRequest.Builder(applicationContext).data(novel.imageURL)
								.build()
						).drawable?.toBitmap()

					notify(
						applicationContext.resources.getQuantityString(
							R.plurals.worker_novel_update_updated_novel_count,
							chapterSize,
							chapterSize
						),
						10000 + novel.id
					) {
						setContentTitle(
							getString(
								R.string.worker_novel_update_updated_novel,
								novel.title
							)
						)


						setLargeIcon(bitmap)

						setNotOngoing()
						removeProgress()

						if (firstChapterId != null) {
							addOpenReader(
								novel.id,
								firstChapterId
							)
							setAutoCancel(true)
						}

					}
				}
			}

		// Will update only if downloadOnUpdate is enabled and there have been chapters
		if (downloadOnUpdate() && updateNovels.size > 0 && updatedChapters.size > 0)
			startDownloadWorker(updatedChapters)

		return Result.success()
	}

	/**
	 * Set the content intent of the notification to open up the chapter reader.
	 */
	private fun NotificationCompat.Builder.addOpenReader(novelId: Int, chapterId: Int) {
		setContentIntent(
			PendingIntent.getActivity(
				applicationContext,
				0,
				intent(applicationContext, ChapterReader::class.java) {
					bundleOf(
						BundleKeys.BUNDLE_CHAPTER_ID to chapterId,
						BundleKeys.BUNDLE_NOVEL_ID to novelId
					)
				},
				(
						if (SDK_INT >= VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
						) or FLAG_UPDATE_CURRENT
			)
		)
	}

	/**
	 * Manager of [NovelUpdateWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {
		private val iSettingsRepository by instance<ISettingsRepository>()

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
			workerManager.getWorkInfosForUniqueWork(UPDATE_WORK_ID).await()

		override suspend fun getCount(): Int =
			getWorkerInfoList().size

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
					).setInputData(data).build()
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
		const val ACTION_CANCEL_NOVEL_UPDATE = "shosetsu_action_cancel_novel_update"
		const val KEY_TARGET: String = "Target"
		const val KEY_CHAPTERS: String = "Novels"

		const val KEY_NOVELS: Int = 0x00
		const val KEY_CATEGORY: String = "category"
	}
}