package com.github.doomsdayrs.apps.shosetsu.backend.services

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.DownloadManager
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.async.ChapterLoader
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.chaptersDao
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.novelsDao
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.updatesDao
import com.github.doomsdayrs.apps.shosetsu.providers.database.entities.ChapterEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.entities.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.entities.UpdateEntity
import com.github.doomsdayrs.apps.shosetsu.variables.ext.entity
import com.github.doomsdayrs.apps.shosetsu.variables.ext.isServiceRunning
import com.github.doomsdayrs.apps.shosetsu.variables.ext.logID
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Formatters
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Notifications.CHANNEL_UPDATE
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Notifications.ID_CHAPTER_UPDATE
import needle.CancelableTask
import needle.Needle
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
class UpdateService : Service() {
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

	private val notificationManager by lazy {
		(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
	}

	private val progressNotification by lazy {
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
		job?.cancel()
		job = when (intent?.getIntExtra(KEY_TARGET, KEY_NOVELS) ?: KEY_NOVELS) {
			KEY_NOVELS ->
				UpdateManga(this, intent
						?: Intent().putIntegerArrayListExtra(KEY_CHAPTERS, Database.DatabaseNovels.intLibrary))

			KEY_CATEGORY ->
				UpdateCategory()

			else -> throw InvalidKeyException("How did you reach this point")
		}
		Needle.onBackgroundThread().execute(job)
		return super.onStartCommand(intent, flags, startId)
	}

	internal class UpdateCategory : CancelableTask() {
		override fun doWork() {
			TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
		}

	}

	internal class UpdateManga(private val updateService: UpdateService, val intent: Intent) : CancelableTask() {
		override fun doWork() {
			val updatedNovels = ArrayList<NovelEntity>()
			intent.getIntegerArrayListExtra(KEY_CHAPTERS)?.let { novelsIDs ->

				// Main process
				for (novelID in novelsIDs.indices) {
					val pr = updateService.progressNotification
					pr.setContentTitle(updateService.getString(R.string.updating))
					pr.setOngoing(true)

					val novelEntity = novelsDao.loadNovel(novelID)
					val formatter = novelEntity.formatter

					if (formatter != Formatters.unknown) {
						// Updates notification
						pr.setContentText(novelEntity.title)
						pr.setProgress(novelsIDs.size, novelID + 1, false)
						updateService.notificationManager.notify(ID_CHAPTER_UPDATE, pr.build())

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
									val tuple = chaptersDao.hasChapter(chapter.link) // One
									val chapterEntity: ChapterEntity
									if (!tuple.boolean) {
										Log.i(logID(), "add #$index\t: ${chapter.link} ")
										chapterEntity =
												chaptersDao.insertAndReturnChapterEntity( // two
														chapter.entity(novelEntity)
												)

										updatesDao.insertUpdate(UpdateEntity( // three
												chapterEntity.id,
												novelID,
												System.currentTimeMillis()
										))

										if (!updatedNovels.contains(novelEntity))
											updatedNovels.add(novelEntity)
									} else {
										chapterEntity = chaptersDao.loadChapter(tuple.id)
										chapterEntity.title = chapter.title
										chapterEntity.order = chapter.order
										chapterEntity.releaseDate = chapter.release
										chaptersDao.updateChapter(chapterEntity)
									}

									if (Settings.isDownloadOnUpdateEnabled)
										DownloadManager.addToDownload(
												updateService.applicationContext as Activity,
												chapterEntity.toDownload()
										)
								}
							}

							override fun onIncrementingProgress(page: Int, max: Int) {
							}

							override fun errorReceived(errorString: String) {
								Log.e(logID(), errorString)
							}
						}, formatter, novelEntity.novelURL).doInBackground()
						Utilities.wait(1000)
					} else {
						pr.setContentText("Unknown Formatter for ${novelEntity.novelURL}")
						pr.setProgress(novelsIDs.size, novelID + 1, false)
						updateService.notificationManager.notify(ID_CHAPTER_UPDATE, pr.build())
					}
				}

			}
			// Completion
			val stringBuilder = StringBuilder()
			val pr = updateService.progressNotification
			when {
				updatedNovels.size > 0 -> {
					pr.setContentTitle(updateService.getString(R.string.update_complete))
					for (novelCard in updatedNovels) stringBuilder.append(novelCard.title).append("\n")
					pr.style = Notification.BigTextStyle()
				}
				else -> {
					pr.setContentTitle(updateService.getString(R.string.update_complete))
					stringBuilder.append(updateService.getString(R.string.update_not_found))
				}
			}
			pr.setContentText(stringBuilder.toString())
			pr.setProgress(0, 0, false)
			pr.setOngoing(false)
			updateService.notificationManager.notify(ID_CHAPTER_UPDATE, pr.build())
			stop(updateService)
		}

	}


}