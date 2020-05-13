package com.github.doomsdayrs.apps.shosetsu.backend.services

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.activity.MainActivity
import com.github.doomsdayrs.apps.shosetsu.common.consts.LogConstants.SERVICE_CANCEL_PREVIOUS
import com.github.doomsdayrs.apps.shosetsu.common.consts.LogConstants.SERVICE_NEW
import com.github.doomsdayrs.apps.shosetsu.common.consts.LogConstants.SERVICE_NULLIFIED
import com.github.doomsdayrs.apps.shosetsu.common.consts.Notifications.CHANNEL_UPDATE
import com.github.doomsdayrs.apps.shosetsu.common.consts.Notifications.ID_CHAPTER_UPDATE
import com.github.doomsdayrs.apps.shosetsu.common.ext.isServiceRunning
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.toast
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IChaptersRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.INovelsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IUpdatesRepository
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.NovelsDao
import needle.CancelableTask
import needle.Needle
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.security.InvalidKeyException

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
 * ====================================================================
 */

/**
 * shosetsu
 * 07 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 * <p>
 *     Handles update requests for the entire application
 * </p>
 */
class UpdateService : Service(), KodeinAware {
	companion object {
		const val KEY_TARGET = "Target"
		const val KEY_CHAPTERS = "Novels"

		const val KEY_NOVELS = 0x00
		const val KEY_CATEGORY = 0x01


		fun init(
				context: Context,
				cards: ArrayList<Int> = ((context as MainActivity).kodein.instance<NovelsDao>()
						as NovelsDao).loadBookmarkedIDs()
						as ArrayList<Int>
		) =
				start(context, KEY_NOVELS, cards)

		/**
		 * Returns the status of the service.
		 *
		 * @param context the application context.
		 * @return true if the service is running, false otherwise.
		 */
		private fun isRunning(context: Context): Boolean {
			return context.isServiceRunning(UpdateService::class.java)
		}

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 *
		 * @param context the application context.
		 * @param category a specific category to update, or null for global update.
		 */
		fun start(context: Context, category: Int, novelIDs: ArrayList<Int>) {
			if (!isRunning(context)) {
				context.toast(R.string.library_update)
				val intent = Intent(context, UpdateService::class.java)
				intent.putExtra(KEY_TARGET, category)
				intent.putIntegerArrayListExtra(KEY_CHAPTERS, novelIDs)
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
					context.startService(intent)
				} else {
					context.startForegroundService(intent)
				}
			}
		}

		/**
		 * Stops the service.
		 *
		 * @param context the application context.
		 */
		fun stop(context: Context) {
			context.stopService(Intent(context, UpdateService::class.java))
		}
	}

	/**
	 * Wake lock that will be held until the service is destroyed.
	 */
	//  private lateinit var wakeLock: PowerManager.WakeLock

	internal val notificationManager by lazy { getSystemService<NotificationManager>()!! }

	internal val progressNotification by lazy {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Notification.Builder(this, CHANNEL_UPDATE)
		} else {
			// Suppressed due to lower API
			@Suppress("DEPRECATION")
			Notification.Builder(this)
		}
				.setSmallIcon(R.drawable.ic_system_update_alt_24dp)
				.setContentText("Update in progress")
				.setOnlyAlertOnce(true)
	}

	override val kodein: Kodein by closestKodein()
	internal val iChaptersRepository by instance<IChaptersRepository>()
	internal val iNovelsRepository by instance<INovelsRepository>()
	internal val iUpdatesRepository by instance<IUpdatesRepository>()

	private var job: CancelableTask? = null

	override fun onCreate() {
		super.onCreate()
		startForeground(ID_CHAPTER_UPDATE, progressNotification.build())
		//   wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LibraryUpdateService:WakeLock")
		//       wakeLock.acquire(60 * 60 * 1000L /*10 minutes*/)
	}

	override fun onDestroy() {
		job?.cancel()
		//     if (wakeLock.isHeld) wakeLock.release()
		super.onDestroy()
	}

	override fun onBind(intent: Intent?): IBinder? {
		return null
	}

	@Throws(InvalidKeyException::class)
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		Log.d(logID(), SERVICE_CANCEL_PREVIOUS)
		job?.cancel()
		Log.d(logID(), SERVICE_NEW)
		job = when (intent?.getIntExtra(KEY_TARGET, KEY_NOVELS) ?: KEY_NOVELS) {
			KEY_NOVELS ->
				UpdateManga(bundleOf())

			KEY_CATEGORY ->
				UpdateCategory()

			else -> throw InvalidKeyException("How did you reach this point")
		}
		job?.let { Needle.onBackgroundThread().execute(it) }
				?: Log.e(logID(), SERVICE_NULLIFIED)
		return super.onStartCommand(intent, flags, startId)
	}

	internal class UpdateCategory : CancelableTask() {
		override fun doWork() {
			TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
		}

	}

	inner class UpdateManga(val bundle: Bundle)
		: CancelableTask() {
		override fun doWork() {
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
					pr.setContentTitle(getString(R.string.update_complete))
					for (novelCard in updatedNovels) stringBuilder.append(novelCard.title).append("\n")
					pr.style = Notification.BigTextStyle()
				}
				else -> {
					pr.setContentTitle(getString(R.string.update_complete))
					stringBuilder.append(getString(R.string.update_not_found))
				}
			}
			pr.setContentText(stringBuilder.toString())
			pr.setProgress(0, 0, false)
			pr.setOngoing(false)
			notificationManager.notify(ID_CHAPTER_UPDATE, pr.build())
			stop(this@UpdateService)
		}

	}


}