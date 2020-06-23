package com.github.doomsdayrs.apps.shosetsu.backend.services

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import androidx.core.content.getSystemService
import androidx.work.*
import androidx.work.ExistingPeriodicWorkPolicy.REPLACE
import androidx.work.NetworkType.METERED
import androidx.work.NetworkType.UNMETERED
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.Settings
import com.github.doomsdayrs.apps.shosetsu.common.Settings.updateOnLowBattery
import com.github.doomsdayrs.apps.shosetsu.common.Settings.updateOnLowStorage
import com.github.doomsdayrs.apps.shosetsu.common.Settings.updateOnlyIdle
import com.github.doomsdayrs.apps.shosetsu.common.consts.LogConstants
import com.github.doomsdayrs.apps.shosetsu.common.consts.Notifications.CHANNEL_UPDATE
import com.github.doomsdayrs.apps.shosetsu.common.consts.Notifications.ID_CHAPTER_UPDATE
import com.github.doomsdayrs.apps.shosetsu.common.consts.WorkerTags.UPDATE_WORK_ID
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.INovelsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.LoadNovelUseCase
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.util.concurrent.TimeUnit.HOURS
import java.util.concurrent.TimeUnit.MINUTES
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
class UpdateWorker(
		appContext: Context,
		params: WorkerParameters
) : CoroutineWorker(appContext, params), KodeinAware {
	companion object {
		const val KEY_TARGET = "Target"
		const val KEY_CHAPTERS = "Novels"

		const val KEY_NOVELS = 0x00
		const val KEY_CATEGORY = 0x01


		/**
		 * Returns the status of the service.
		 *
		 * @param context the application context.
		 * @return true if the service is running, false otherwise.
		 */
		private fun isRunning(
				context: Context,
				workerManager: WorkManager = WorkManager.getInstance(context)
		): Boolean = try {
			workerManager.getWorkInfosForUniqueWork(UPDATE_WORK_ID)
					.get()[0].state == WorkInfo.State.RUNNING
		} catch (e: Exception) {
			false
		}

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 *
		 * @param context the application context.
		 */
		fun start(context: Context,
		          workerManager: WorkManager = WorkManager.getInstance(context)
		) {
			Log.i(logID(), LogConstants.SERVICE_NEW)
			workerManager.enqueueUniquePeriodicWork(
					UPDATE_WORK_ID,
					REPLACE,
					PWRB<DownloadWorker>(
							Settings.updateCycle.toLong(),
							HOURS,
							15,
							MINUTES
					).setConstraints(
							Constraints.Builder().apply {
								setRequiredNetworkType(
										if (Settings.updateOnMetered) {
											METERED
										} else UNMETERED
								)
								setRequiresStorageNotLow(!updateOnLowStorage)
								setRequiresBatteryNotLow(!updateOnLowBattery)
								if (SDK_INT >= VERSION_CODES.M)
									setRequiresDeviceIdle(updateOnlyIdle)
							}.build()
					)
							.build()
			)
		}

		/**
		 * Stops the service.
		 *
		 * @param context the application context.
		 */
		fun stop(context: Context,
		         workerManager: WorkManager = WorkManager.getInstance(context)
		): Any = workerManager.cancelUniqueWork(UPDATE_WORK_ID)
	}

	/**
	 * Wake lock that will be held until the service is destroyed.
	 */
//  private lateinit var wakeLock: PowerManager.WakeLock

	internal val notificationManager by lazy { appContext.getSystemService<NotificationManager>()!! }

	internal val progressNotification by lazy {
		if (SDK_INT >= VERSION_CODES.O) {
			Notification.Builder(appContext, CHANNEL_UPDATE)
		} else {
			// Suppressed due to lower API
			@Suppress("DEPRECATION")
			Notification.Builder(appContext)
		}
				.setSmallIcon(R.drawable.ic_system_update_alt_24dp)
				.setContentText("Update in progress")
				.setOnlyAlertOnce(true)
	}

	override val kodein: Kodein by closestKodein(appContext)
	internal val iNovelsRepository by instance<INovelsRepository>()
	internal val loadNovelUseCase by instance<LoadNovelUseCase>()

	override suspend fun doWork(): Result {
		Log.i(logID(), LogConstants.SERVICE_EXECUTE)
		val pr = progressNotification
		pr.setContentTitle(applicationContext.getString(R.string.update))
		pr.setOngoing(true)
		notificationManager.notify(ID_CHAPTER_UPDATE, pr.build())

		iNovelsRepository.suspendedGetBookmarkedNovels().let { hNovels ->
			when (hNovels) {
				is HResult.Success -> {
					val novels = hNovels.data.let {
						if (Settings.onlyUpdateOngoing)
							it.filter { it.status == Novel.Status.PUBLISHING }
						else it
					}
					var progress = 0
					novels.forEach {
						pr.setContentText(applicationContext.getString(R.string.updating) + it.title)
						pr.setProgress(novels.size, progress, false)
						notificationManager.notify(ID_CHAPTER_UPDATE, pr.build())
						loadNovelUseCase(it, true).let {

						}
						progress++
					}
					pr.setContentTitle(applicationContext.getString(R.string.update))
					pr.setContentText(applicationContext.getString(R.string.update_complete))
					pr.setOngoing(false)
					notificationManager.notify(ID_CHAPTER_UPDATE, pr.build())

				}
				else -> {
					return Result.failure()
				}
			}
		}
		return Result.success()
	}


	inner class UpdateNovels(val bundle: Bundle) {
		fun doWork() {
			val updatedNovels = ArrayList<NovelEntity>()
			iNovelsRepository.blockingGetBookmarkedNovels().let { novelEntities ->
				// Main process
				/*
				novelEntities.forEachIndexed { index, novelEntity ->
					val pr = progressNotification
					pr.setContentTitle(getString(R.string.updating))
					pr.setOngoing(true)

					val formatter = novelEntity.formatter

					if (formatter != FormatterUtils.unknown) {
						// Updates notification
						pr.setContentText(novelEntity.title)
						pr.setProgress(novelEntities.size, index + 1, false)
						notificationManager.notify(ID_CHAPTER_UPDATE, pr.build())

						// Runs process
						ChapterLoader(object : ChapterLoader.ChapterLoaderAction {
							override fun onPreExecute() {
							}

							override fun onPostExecute(
									result: Boolean,
									finalChapters: ArrayList<Novel.Chapter>
							) {
							}

							override fun onJustBeforePost(finalChapters: ArrayList<Novel.Chapter>) {
								for ((index, chapter) in finalChapters.withIndex()) {
									val tuple = iChaptersRepository.hasChapter(chapter.link) // One
									val chapterEntity: ChapterEntity
									if (!tuple.boolean) {
										Log.i(logID(), "add #$index\t: ${chapter.link} ")
										chapterEntity =
												iChaptersRepository.insertAndReturnChapterEntity( // two
														chapter.entity(novelEntity)
												)

										iUpdatesRepository.insertUpdate(UpdateEntity( // three
												chapterEntity.id,
												novelEntity.id,
												System.currentTimeMillis()
										))

										if (!updatedNovels.contains(novelEntity))
											updatedNovels.add(novelEntity)
									} else {
										chapterEntity = iChaptersRepository.loadChapter(tuple.id)
										chapterEntity.title = chapter.title
										chapterEntity.order = chapter.order
										chapterEntity.releaseDate = chapter.release
										iChaptersRepository.updateChapter(chapterEntity)
									}

									if (Settings.isDownloadOnUpdateEnabled)
										DownloadManager.addToDownload(
												applicationContext as Activity,
												chapterEntity.toDownload()
										)
								}
							}

							override fun onIncrementingProgress(page: Int, max: Int) {
							}

							override fun errorReceived(errorString: String) {
								Log.e(logID(), errorString)
							}
						}, formatter, novelEntity.url).doInBackground()
						wait(1000)
					} else {
						pr.setContentText("Unknown Formatter for ${novelEntity.url}")
						pr.setProgress(novelEntities.size, index + 1, false)
						notificationManager.notify(ID_CHAPTER_UPDATE, pr.build())
					}
				}
				*/
			}

			// Completion
			val stringBuilder = StringBuilder()
			val pr = progressNotification
			when {
				updatedNovels.size > 0 -> {
					//	pr.setContentTitle(getString(R.string.update_complete))
					for (novelCard in updatedNovels) stringBuilder.append(novelCard.title).append("\n")
					pr.style = Notification.BigTextStyle()
				}
				else -> {
					//		pr.setContentTitle(getString(R.string.update_complete))
					//		stringBuilder.append(getString(R.string.update_not_found))
				}
			}
			pr.setContentText(stringBuilder.toString())
			pr.setProgress(0, 0, false)
			pr.setOngoing(false)
			notificationManager.notify(ID_CHAPTER_UPDATE, pr.build())
		}

	}


}