package app.shosetsu.android.backend.workers.onetime

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.work.*
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.NullContentResolverException
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.Notifications
import app.shosetsu.android.common.consts.Notifications.ID_RESTORE
import app.shosetsu.android.common.consts.VERSION_BACKUP
import app.shosetsu.android.common.consts.WorkerTags.RESTORE_WORK_ID
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.common.utils.backupJSON
import app.shosetsu.android.domain.model.local.backup.*
import app.shosetsu.android.domain.repository.base.*
import app.shosetsu.android.domain.usecases.InstallExtensionUseCase
import app.shosetsu.android.domain.usecases.StartRepositoryUpdateManagerUseCase
import app.shosetsu.android.common.GenericSQLiteException
import app.shosetsu.android.common.LuaException
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.model.local.BackupEntity
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.domain.model.local.NovelSettingEntity
import app.shosetsu.common.enums.ReadingStatus
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Version
import app.shosetsu.lib.exceptions.HTTPException
import coil.imageLoader
import coil.request.ImageRequest
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.decodeFromStream
import org.acra.ACRA
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import org.luaj.vm2.LuaError
import java.io.BufferedInputStream
import java.io.IOException
import java.net.UnknownHostException
import java.util.zip.GZIPInputStream

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
 * 21 / 01 / 2021
 */
class RestoreBackupWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(
	appContext,
	params
), DIAware, NotificationCapable {
	override val di: DI by closestDI(applicationContext)

	private val backupRepo by instance<IBackupRepository>()
	private val extensionsRepoRepo by instance<IExtensionRepoRepository>()
	private val initializeExtensionsUseCase by instance<StartRepositoryUpdateManagerUseCase>()
	private val extensionsRepo by instance<IExtensionsRepository>()
	private val extensionEntitiesRepo by instance<IExtensionEntitiesRepository>()
	private val installExtension: InstallExtensionUseCase by instance()
	private val novelsRepo by instance<INovelsRepository>()
	private val novelsSettingsRepo by instance<INovelSettingsRepository>()
	private val chaptersRepo by instance<IChaptersRepository>()
	private val backupUriRepo by instance<IBackupUriRepository>()
	override val baseNotificationBuilder: NotificationCompat.Builder
		get() = notificationBuilder(applicationContext, Notifications.CHANNEL_BACKUP)
			.setSubText(getString(R.string.restore_notification_subtitle))
			.setSmallIcon(R.drawable.restore)
			.setOnlyAlertOnce(true)
			.setOngoing(true)

	override val notificationManager: NotificationManagerCompat by notificationManager()
	override val notifyContext: Context = appContext
	override val defaultNotificationID: Int = ID_RESTORE

	@Throws(IOException::class)
	private fun unGZip(content: ByteArray) =
		GZIPInputStream(content.inputStream())

	/**
	 * Loads a backup via the [Uri] provided by Androids file selection
	 */
	@Throws(NullContentResolverException::class)
	private fun loadBackupFromUri(uri: Uri): BackupEntity {
		val contentResolver =
			applicationContext.contentResolver ?: throw NullContentResolverException()

		val inputStream = contentResolver.openInputStream(uri)
		val bis = BufferedInputStream(inputStream)
		return BackupEntity(bis.readBytes())
	}

	@Throws(IOException::class)
	override suspend fun doWork(): Result {
		logI("Starting restore")
		val backupName = inputData.getString(BACKUP_DATA_KEY)
		val isExternal = inputData.getBoolean(BACKUP_DIR_KEY, false)

		if (!isExternal && backupName == null) {
			logE("null backupName, Internal Restore requires backupName")
			return Result.failure()
		}

		notify(R.string.restore_notification_content_starting)
		val backupEntity = try {
			if (isExternal) {
				backupUriRepo.take()?.let { loadBackupFromUri(it) }
			} else {
				backupRepo.loadBackup(backupName!!)
			}
		} catch (e: Exception) {//TODO specify
			with(e) {
				logE(" $message", e)
				notify("$message $e") {
					setNotOngoing()
					addReportErrorAction(
						applicationContext,
						defaultNotificationID,
						e
					)
				}
				return Result.failure()
			}
		}
		if (backupEntity == null) {
			logE("Received empty, impossible")
			notify(R.string.restore_notification_content_unexpected_empty) {
				setNotOngoing()
			}
			return Result.failure()
		}


		// Decode encrypted string to bytes via Base64
		notify(R.string.restore_notification_content_decoding_string)
		val decodedBytes: ByteArray = Base64.decode(backupEntity.content, Base64.DEFAULT)

		// Unzip bytes to a string via gzip
		notify(R.string.restore_notification_content_unzipping_bytes)


		unGZip(decodedBytes).use { stream ->
			val metaInfo = backupJSON.decodeFromStream<MetaBackupEntity>(stream)

			// Reads the version line from the json, if it does not exist the process fails

			val metaVersion: String =
				metaInfo.version ?: return Result.failure()
					.also {
						logE(MESSAGE_LOG_JSON_MISSING)
						notify(R.string.restore_notification_content_missing_key) { setNotOngoing() }
					}


			// Checks if the version is compatible
			logV("Version in backup: $metaVersion")

			if (!Version(metaVersion).isCompatible(Version(VERSION_BACKUP))) {
				logE(MESSAGE_LOG_JSON_OUTDATED)
				notify(R.string.restore_notification_content_text_outdated) { setNotOngoing() }
				return Result.failure()
			}
		}

		unGZip(decodedBytes).use { stream ->
			val backup = backupJSON.decodeFromStream<FleshedBackupEntity>(stream)

			notify("Adding repositories")
			// Adds the repositories
			backup.repos.forEach { (url, name) ->
				notify("") {
					setContentTitle(getString(R.string.restore_notification_title_adding_repos))
					setContentText("$name\n$url")
				}
				try {
					extensionsRepoRepo.addRepository(
						url,
						name,
					)
				} catch (e: GenericSQLiteException) {
					logE("Failed to add repo", e)
					// its likely constraint, we can ignore it
				}
			}

			notify("Loading repository data")
			// Load the data from the repositories
			initializeExtensionsUseCase()

			// Install the extensions
			val repoNovels: List<NovelEntity> = novelsRepo.loadNovels()

			backup.extensions.forEach { restoreExtension(repoNovels, it) }
		}


		System.gc() // Politely ask for a garbage collection
		delay(5000) // Wait for gc to occur (maybe), also helps with the next notification

		notify(R.string.restore_notification_content_completed)
		{
			setNotOngoing()
		}
		logI("Completed restore")
		return Result.success()
	}

	private suspend fun restoreExtension(
		repoNovels: List<NovelEntity>,
		backupExtensionEntity: BackupExtensionEntity
	) {
		val extensionID = backupExtensionEntity.id
		val backupNovels = backupExtensionEntity.novels
		logI("$extensionID")
		// TODO Critical, Rework extensions to enable the below to work properly. DO NOT RELEASE.
		extensionsRepo.getExtension(2, extensionID)?.let { extensionEntity ->
			// Install the extension
			if (!extensionsRepo.isExtensionInstalled(extensionEntity)) {
				notify(getString(R.string.installing) + " ${extensionEntity.id} | ${extensionEntity.name}")
				installExtension(extensionEntity)
			}
			val iExt = extensionEntitiesRepo.get(extensionEntity)

			backupNovels.forEach novelLoop@{ novelEntity ->
				try {
					restoreNovel(
						iExt,
						extensionID,
						novelEntity,
						repoNovels
					)
				} catch (e: Exception) {
					e.printStackTrace()
				}
			}
		}
	}

	private suspend fun restoreNovel(
		iExt: IExtension,
		extensionID: Int,
		backupNovelEntity: BackupNovelEntity,
		repoNovels: List<NovelEntity>
	) {
		// Use a single memory location for the bitmap
		var bitmap: Bitmap? = null

		fun clearBitmap() {
			bitmap = null
		}

		val bNovelURL = backupNovelEntity.url
		val name = backupNovelEntity.name
		val imageURL = backupNovelEntity.imageURL
		val bChapters = backupNovelEntity.chapters
		val bSettings = backupNovelEntity.settings

		logI(name)

		// If none match the extension ID and URL, time to load it up
		val loadImageJob = launchIO {
			try {
				bitmap =
					applicationContext.imageLoader.execute(
						ImageRequest.Builder(applicationContext).data(imageURL).build()
					).drawable?.toBitmap()

			} catch (e: IOException) {
				logE("Failed to download novel image", e)
				ACRA.errorReporter.handleSilentException(e)
			}
		}

		var targetNovelID = -1
		if (repoNovels.none { it.extensionID == extensionID && it.url == bNovelURL }) {
			notify(R.string.restore_notification_content_novel_load) {
				setContentTitle(name)
				setLargeIcon(bitmap)
			}

			val siteNovel = try {
				try {
					iExt.parseNovel(bNovelURL, true)
				} catch (e: LuaError) {
					if (e.cause != null)
						throw e.cause!!
					else throw LuaException(e)
				}
			} catch (e: Exception) {
				when (e) {
					is HTTPException -> {
						logE("Failed to load novel from website", e)

						notify(
							getString(
								R.string.restore_notification_content_novel_fail_parse_http,
								"${e.code}"
							),
							2000 + bNovelURL.hashCode()
						) {
							setContentTitle(name)
							setLargeIcon(bitmap)
							setNotOngoing()
						}

					}
					is UnknownHostException -> {
						logE("Failed to locate website", e)

						notify(
							getString(
								R.string.restore_notification_content_novel_fail_parse_host,
								"${e.message}"
							),
							2000 + bNovelURL.hashCode()
						) {
							setContentTitle(name)
							setLargeIcon(bitmap)
							setNotOngoing()
						}
					}
					is LuaError -> {
						logE("Lua error occurred", e)

						notify(
							getString(
								R.string.restore_notification_content_novel_fail_lua,
								"${e.message}"
							),
							2000 + bNovelURL.hashCode()
						) {
							setContentTitle(name)
							setLargeIcon(bitmap)
							setNotOngoing()
						}
					}
					else -> {
						logE("Failed to parse novel while loading backup", e)

						ACRA.errorReporter.handleException(e, false)

						notify(
							R.string.restore_notification_content_novel_fail_parse,
							2000 + bNovelURL.hashCode()
						) {
							setContentTitle(name)
							setLargeIcon(bitmap)
							setNotOngoing()
						}
					}
				}
				delay(5000)
				return
			}

			notify(R.string.restore_notification_content_novel_save) {
				setContentTitle(name)
				setLargeIcon(bitmap)
			}
			novelsRepo.insertReturnStripped(
				siteNovel.asEntity(
					link = bNovelURL,
					extensionID = extensionID,
				).copy(
					bookmarked = true
				)
			)?.let { (id) ->
				targetNovelID = id
			}

			notify(R.string.restore_notification_content_novel_chapters_save) {
				setContentTitle(name)
				setLargeIcon(bitmap)
			}
			try {
				chaptersRepo.handleChapters(
					novelID = targetNovelID,
					extensionID = extensionID,
					list = siteNovel.chapters.distinctBy { it.link }
				)
			} catch (e: Exception) {//TODO Specify
				logE("Failed to handle chapters", e)
				ACRA.errorReporter.handleSilentException(e)
			}
			logI("Inserted new chapters")
		} else {
			// Get the novelID from the present novels, or just end this update
			repoNovels.find { it.extensionID == extensionID && it.url == bNovelURL }?.id?.let { it ->
				targetNovelID = it
			} ?: run {
				clearBitmap()
				return
			}
		}

		// if the ID is still -1, return
		if (targetNovelID == -1) {
			logE("Could not find novel, even after injecting, aborting")
			clearBitmap()
			return
		}

		notify(R.string.restore_notification_content_chapters_load) {
			setContentTitle(name)
			setLargeIcon(bitmap)
		}
		// get the chapters
		val repoChapters = chaptersRepo.getChapters(targetNovelID)

		// Go through the chapters updating them
		for (index in bChapters.indices) {
			restoreChapter(
				name,
				repoChapters,
				bChapters[index],
				{ bitmap },
				bChapters.size,
				index
			)
		}

		notify(R.string.restore_notification_content_settings_restore) {
			setContentTitle(name)
			setLargeIcon(bitmap)
			removeProgress()
		}

		val settingEntity = try {
			novelsSettingsRepo.get(targetNovelID)
		} catch (e: Exception) {// TODO specify
			logE("Failed to load novel settings")
			ACRA.errorReporter.handleSilentException(e)
			return
		}

		if (settingEntity == null) {
			logI("Inserting novel settings")
			novelsSettingsRepo.insert(
				NovelSettingEntity(
					targetNovelID,
					sortType = bSettings.sortType,
					showOnlyReadingStatusOf = bSettings.showOnlyReadingStatusOf,
					showOnlyBookmarked = bSettings.showOnlyBookmarked,
					showOnlyDownloaded = bSettings.showOnlyDownloaded,
					reverseOrder = bSettings.reverseOrder,
				)
			)
		} else {
			novelsSettingsRepo.update(
				settingEntity.copy(
					sortType = bSettings.sortType,
					showOnlyReadingStatusOf = bSettings.showOnlyReadingStatusOf,
					showOnlyBookmarked = bSettings.showOnlyBookmarked,
					showOnlyDownloaded = bSettings.showOnlyDownloaded,
					reverseOrder = bSettings.reverseOrder,
				)
			)
		}


		loadImageJob.join() // Finish the image loading job

		clearBitmap() // Remove data from bitmap

		delay(500) // Delay things a bit
	}

	private val settings: ISettingsRepository by instance()
	private val printChapter: Boolean
		get() = runBlocking {
			settings.getBoolean(SettingKey.RestorePrintChapters)
		}

	private suspend fun restoreChapter(
		novelName: String,
		repoChapters: List<ChapterEntity>,
		backupChapterEntity: BackupChapterEntity,
		getBitmap: () -> Bitmap?,
		size: Int,
		index: Int
	) {
		val chapterURL = backupChapterEntity.url
		val chapterName = backupChapterEntity.name
		val bookmarked = backupChapterEntity.bookmarked
		val rS = backupChapterEntity.rS
		val rP = backupChapterEntity.rP

		repoChapters.find { it.url == chapterURL }?.let { chapterEntity ->

			if (printChapter)
				logI(chapterName)

			notify(getString(R.string.updating) + ": ${chapterEntity.title}") {
				setContentTitle(novelName)
				setLargeIcon(getBitmap())
				setProgress(size, index, false)
				setSilent(true)
			}

			chaptersRepo.updateChapter(
				chapterEntity.copy(
					title = chapterName,
					bookmarked = bookmarked,
				).let {
					when (it.readingStatus) {
						ReadingStatus.READING -> it
						ReadingStatus.READ -> it
						else -> it.copy(
							readingStatus = rS,
							readingPosition = rP
						)
					}
				}
			)
		}
	}

	/**
	 * Manager of [BackupWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {

		override suspend fun getCount(): Int =
			getWorkerInfoList().size

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override suspend fun isRunning(): Boolean = try {
			// Is this running
			val a = (getWorkerState() == WorkInfo.State.RUNNING)

			// Don't run if update is being installed
			val b = !AppUpdateInstallWorker.Manager(context).isRunning()
			a && b
		} catch (e: Exception) {
			false
		}

		override suspend fun getWorkerState(index: Int): WorkInfo.State =
			getWorkerInfoList()[index].state

		override suspend fun getWorkerInfoList(): List<WorkInfo> =
			workerManager.getWorkInfosForUniqueWork(RESTORE_WORK_ID).await()

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 */
		override fun start(data: Data) {
			launchIO {
				logI(LogConstants.SERVICE_NEW)
				workerManager.enqueueUniqueWork(
					RESTORE_WORK_ID,
					ExistingWorkPolicy.REPLACE,
					OneTimeWorkRequestBuilder<RestoreBackupWorker>(
					).setInputData(data).build()
				)
				logI(
					"Worker State ${
						workerManager.getWorkInfosForUniqueWork(RESTORE_WORK_ID)
							.await()[0].state
					}"
				)
			}
		}

		/**
		 * Stops the service.
		 */
		override fun stop(): Operation =
			workerManager.cancelUniqueWork(RESTORE_WORK_ID)
	}

	companion object {
		private const val MESSAGE_LOG_JSON_MISSING = "BACKUP JSON DOES NOT CONTAIN KEY 'version'"


		private const val MESSAGE_LOG_JSON_OUTDATED = "BACKUP JSON MISMATCH"


		/**
		 * Path / name of file
		 */
		const val BACKUP_DATA_KEY = "BACKUP_NAME"

		/**
		 * If true, the [BACKUP_DATA_KEY] is a full path pointing to a specific file, other wise
		 * it is an internal path
		 */
		const val BACKUP_DIR_KEY = "BACKUP_DIR"
	}
}