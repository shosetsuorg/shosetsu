package app.shosetsu.android.backend.workers.onetime

import android.content.Context
import android.os.Build
import android.util.Base64
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.Notifications
import app.shosetsu.android.common.consts.Notifications.CHANNEL_BACKUP
import app.shosetsu.android.common.consts.WorkerTags.BACKUP_WORK_ID
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.common.utils.backupJSON
import app.shosetsu.android.domain.model.local.backup.*
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.model.local.BackupEntity
import app.shosetsu.common.domain.repositories.base.*
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.unwrap
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.serialization.encodeToString
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPOutputStream

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * 18 / 01 / 2021
 */
class BackupWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(
	appContext,
	params,
), DIAware, NotificationCapable {

	override val di: DI by closestDI(appContext)
	private val novelRepository by instance<INovelsRepository>()
	private val iSettingsRepository by instance<ISettingsRepository>()

	/**
	 * TODO add settings backup
	 */
	private val novelSettingsRepository by instance<INovelSettingsRepository>()
	private val extensionsRepository by instance<IExtensionsRepository>()
	private val chaptersRepository by instance<IChaptersRepository>()
	private val extensionRepoRepository by instance<IExtensionRepoRepository>()
	private val backupRepository by instance<IBackupRepository>()

	override val notificationManager: NotificationManagerCompat by notificationManager()

	override val baseNotificationBuilder: NotificationCompat.Builder
		get() = notificationBuilder(applicationContext, CHANNEL_BACKUP)
			.setSmallIcon(R.drawable.backup_icon)
			.setSubText("Backup")
			.setOnlyAlertOnce(true)
			.setOngoing(true)

	override val notifyContext: Context
		get() = applicationContext
	override val defaultNotificationID: Int = Notifications.ID_BACKUP

	private suspend fun backupChapters() =
		iSettingsRepository.getBooleanOrDefault(BackupChapters)

	private suspend fun backupSettings() =
		iSettingsRepository.getBooleanOrDefault(BackupSettings)


	@Throws(IOException::class)
	fun gzip(content: String): ByteArray {
		val bos = ByteArrayOutputStream()
		GZIPOutputStream(bos).bufferedWriter().use { it.write(content) }
		return bos.toByteArray()
	}

	private suspend fun getBackupChapters(novelID: Int): List<BackupChapterEntity> {
		if (backupChapters())
			chaptersRepository.getChapters(novelID).handle {
				return it.map { chapterEntity ->
					BackupChapterEntity(
						chapterEntity.url,
						chapterEntity.title,
						chapterEntity.bookmarked,
						chapterEntity.readingStatus,
						chapterEntity.readingPosition
					)
				}
			}
		return listOf()
	}


	@Throws(IOException::class)
	override suspend fun doWork(): Result {
		// Load novels
		logV(LogConstants.SERVICE_EXECUTE)
		notify("Starting...")
		val backupSettings = backupSettings()

		novelRepository.loadBookmarkedNovelEntities().handle { novels ->
			notify("Loaded ${novels.size} novel(s)")

			notify("Retrieving and mapping chapters")
			// Novels to their chapters
			val novelsToChapters = novels.map { it to getBackupChapters(it.id!!) }


			notify("Loading extensions required")
			// Extensions each novel requires
			// Distinct, with no duplicates
			val extensions = novels.map {
				extensionsRepository.getExtensionEntity(it.extensionID).unwrap()!!
			}.distinct()

			notify("Loading repositories required")
			// All the repos required for backup
			// Contains only the repos that are used
			val repositoriesRequired =
				extensionRepoRepository.loadRepositories().unwrap()!!
					.filter { repositoryEntity ->
						extensions.any { extensionEntity ->
							extensionEntity.repoID == repositoryEntity.id
						}
					}.map { (_, url, name) ->
						BackupRepositoryEntity(url, name)
					}

			notify("Creating backup entity")
			val backup = FleshedBackupEntity(
				repos = repositoriesRequired,
				// Creates the trees
				extensions = extensions.map { extensionEntity ->
					BackupExtensionEntity(
						extensionEntity.id,
						novelsToChapters.filter { (novel, _) ->
							novel.extensionID == extensionEntity.id
						}.map { (novel, chapters) ->
							val settings =
								if (backupSettings)
									novelSettingsRepository.get(novel.id!!).unwrap()
								else null

							val bSettings = settings?.let {
								BackupNovelSettingEntity(
									it.sortType,
									it.showOnlyReadingStatusOf,
									it.showOnlyBookmarked,
									it.showOnlyDownloaded
								)
							} ?: BackupNovelSettingEntity()

							BackupNovelEntity(
								novel.url,
								novel.title,
								novel.imageURL,
								chapters,
								settings = bSettings
							)
						}
					)
				}
			)

			notify("Encoding to json")
			val stringBackup = backupJSON.encodeToString(backup)

			notify("Zipping bytes")
			val zippedBytes = gzip(stringBackup)

			notify("Encoding via bas64")
			val base64Bytes = Base64.encode(zippedBytes, Base64.DEFAULT)

			notify("Saving to file")
			backupRepository.saveBackup(
				BackupEntity(
					base64Bytes
				)
			)

			notify("Completed") {
				setOngoing(false)
			}

			// Call GC to clean up the bulky resources
			System.gc()
			return Result.success()
		}

		return Result.failure()
	}

	/**
	 * Manager of [BackupWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {
		private val iSettingsRepository: ISettingsRepository by instance()

		private suspend fun requiresBackupOnIdle(): Boolean =
			iSettingsRepository.getBooleanOrDefault(BackupOnlyWhenIdle)

		private suspend fun allowsBackupOnLowStorage(): Boolean =
			iSettingsRepository.getBooleanOrDefault(BackupOnLowStorage)

		private suspend fun allowsBackupOnLowBattery(): Boolean =
			iSettingsRepository.getBooleanOrDefault(BackupOnLowBattery)

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
			workerManager.getWorkInfosForUniqueWork(BACKUP_WORK_ID).get()[index].state

		override val count: Int
			get() = workerManager.getWorkInfosForUniqueWork(BACKUP_WORK_ID).get().size

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 */
		override fun start(data: Data) {
			launchIO {
				logI(LogConstants.SERVICE_NEW)
				workerManager.enqueueUniqueWork(
					BACKUP_WORK_ID,
					ExistingWorkPolicy.REPLACE,
					OneTimeWorkRequestBuilder<BackupWorker>(
					).setConstraints(
						Constraints.Builder().apply {
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
								setRequiresDeviceIdle(requiresBackupOnIdle())

							setRequiresStorageNotLow(!allowsBackupOnLowStorage())
							setRequiresBatteryNotLow(!allowsBackupOnLowBattery())
						}.build()
					).build()
				)
				logI(
					"Worker State ${
						workerManager.getWorkInfosForUniqueWork(BACKUP_WORK_ID)
							.await()[0].state
					}"
				)
			}
		}

		/**
		 * Stops the service.
		 */
		override fun stop(): Operation =
			workerManager.cancelUniqueWork(BACKUP_WORK_ID)
	}
}