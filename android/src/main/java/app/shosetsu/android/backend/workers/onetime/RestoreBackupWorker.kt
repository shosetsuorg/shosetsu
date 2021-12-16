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
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.Notifications
import app.shosetsu.android.common.consts.Notifications.ID_RESTORE
import app.shosetsu.android.common.consts.VERSION_BACKUP
import app.shosetsu.android.common.consts.WorkerTags.RESTORE_WORK_ID
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.common.utils.backupJSON
import app.shosetsu.android.domain.model.local.backup.FleshedBackupEntity
import app.shosetsu.android.domain.repository.base.IBackupUriRepository
import app.shosetsu.android.domain.usecases.InstallExtensionUseCase
import app.shosetsu.android.domain.usecases.StartRepositoryUpdateManagerUseCase
import app.shosetsu.common.domain.model.local.BackupEntity
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.domain.model.local.NovelSettingEntity
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.domain.repositories.base.*
import app.shosetsu.common.dto.*
import app.shosetsu.common.enums.ReadingStatus
import app.shosetsu.lib.Version
import coil.imageLoader
import coil.request.ImageRequest
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.delay
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.acra.ACRA
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import java.io.*
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
	private fun unGZip(content: ByteArray): String =
		GZIPInputStream(content.inputStream()).bufferedReader().use { it.readText() }

	/**
	 * Loads a backup via the [Uri] provided by Androids file selection
	 */
	private fun loadBackupFromUri(uri: Uri): HResult<BackupEntity> {
		val contentResolver = applicationContext.contentResolver ?: return errorResult(
			NullPointerException("Null contentResolver")
		)
		val inputStream = contentResolver.openInputStream(uri)
		val bis = BufferedInputStream(inputStream)
		return successResult(BackupEntity(bis.readBytes()))
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
		(if (isExternal)
			backupUriRepo.take().transform { loadBackupFromUri(it) }
		else backupRepo.loadBackup(backupName!!)).handle(
			onEmpty = {
				logE("Received empty, impossible")
				notify(R.string.restore_notification_content_unexpected_empty) {
					setNotOngoing()
				}
				return Result.failure()
			},
			onLoading = {
				logE("Received loading, impossible")
				notify(R.string.restore_notification_content_impossible_loading) {
					setNotOngoing()
				}
				return Result.failure()
			},
			onError = { (code, message, exception) ->
				logE("$code $message", exception)
				ACRA.errorReporter.handleException(exception)
				notify("$code $message $exception") {
					setNotOngoing()
				}
				return Result.failure()
			}
		) { backupEntity ->
			// Decode encrypted string to bytes via Base64
			notify(R.string.restore_notification_content_decoding_string)
			val decodedBytes: ByteArray = Base64.decode(backupEntity.content, Base64.DEFAULT)

			// Unzip bytes to a string via gzip
			notify(R.string.restore_notification_content_unzipping_bytes)
			val unzippedString: String = unGZip(decodedBytes)


			// Verifies the version is compatible and then parses it to a backup object
			val backup = backupJSON.decodeFromJsonElement<FleshedBackupEntity>(
				backupJSON.parseToJsonElement(
					unzippedString
				).jsonObject.also { jsonObject ->
					// Reads the version line from the json, if it does not exist the process fails
					val value: String =
						jsonObject["version"]?.jsonPrimitive?.content ?: return Result.failure()
							.also {
								logE(MESSAGE_LOG_JSON_MISSING).also {
									notify(R.string.restore_notification_content_missing_key) { setNotOngoing() }
								}
							}

					// Checks if the version is compatible
					logV("Version in backup: $value")

					if (!Version(value).isCompatible(Version(VERSION_BACKUP))) {
						logE(MESSAGE_LOG_JSON_OUTDATED)
						notify(R.string.restore_notification_content_text_outdated) { setNotOngoing() }
						return Result.failure()
					}
				})

			notify("Adding repositories")
			// Adds the repositories
			backup.repos.forEach { (url, name) ->
				notify("") {
					setContentTitle(getString(R.string.restore_notification_title_adding_repos))
					setContentText("$name\n$url")
				}
				extensionsRepoRepo.addRepository(
					RepositoryEntity(
						url = url,
						name = name,
						isEnabled = true
					)
				)
			}

			notify("Loading repository data")
			// Load the data from the repositories
			initializeExtensionsUseCase()

			// Install the extensions
			val repoNovels: List<NovelEntity> = novelsRepo.loadNovels().unwrap()!!

			backup.extensions.forEach { (extensionID, backupNovels) ->
				extensionsRepo.getExtension(extensionID).handle { extensionEntity ->
					// Install the extension
					if (!extensionEntity.installed) {
						notify(getString(R.string.installing) + " ${extensionEntity.id} | ${extensionEntity.name}")
						installExtension(extensionEntity)
					}
					val iExt = extensionEntitiesRepo.get(extensionEntity).unwrap()!!

					// Use a single memory location for the bitmap
					var bitmap: Bitmap? = null

					fun clearBitmap() {
						bitmap = null
					}

					backupNovels.forEach novelLoop@{ (bNovelURL, name, imageURL, bChapters, bSettings) ->
						// If none match the extension ID and URL, time to load it up
						val loadImageJob = launchIO {
							try {
								bitmap = applicationContext.imageLoader.execute(
									ImageRequest.Builder(applicationContext).data(imageURL).build()
								).drawable?.toBitmap()
							} catch (e: IOException) {
								logE("Failed to download novel image", e)
								ACRA.errorReporter.handleException(e)
							}
						}

						var targetNovelID = -1
						if (repoNovels.none { it.extensionID == extensionEntity.id && it.url == bNovelURL }) {
							notify(R.string.restore_notification_content_novel_load) {
								setContentTitle(name)
								setLargeIcon(bitmap)
							}

							val siteNovel = try {
								iExt.parseNovel(bNovelURL, true)
							} catch (e: Exception) {
								logE("Failed to parse novel while loading backup", e)
								toast("Failed to parse `$name` in $iExt")
								ACRA.errorReporter.handleException(e)
								notify(
									R.string.restore_notification_content_novel_fail_parse,
									2000 + bNovelURL.hashCode()
								) {
									setContentTitle(name)
									setLargeIcon(bitmap)
									setNotOngoing()
								}

								clearBitmap()
								return@novelLoop
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
							).handle { (id) ->
								targetNovelID = id
							}

							notify(R.string.restore_notification_content_novel_chapters_save) {
								setContentTitle(name)
								setLargeIcon(bitmap)
							}
							chaptersRepo.handleChapters(
								novelID = targetNovelID,
								extensionID = extensionID,
								list = siteNovel.chapters.distinctBy { it.link }
							).handle(
								onError = {
									logE("Failed to handle chapters", it.exception)
									ACRA.errorReporter.handleException(it.exception)
								}
							) {
								logI("Inserted new chapters")
							}
						} else {
							// Get the novelID from the present novels, or just end this update
							repoNovels.find { it.extensionID == extensionEntity.id && it.url == bNovelURL }?.id?.let { it ->
								targetNovelID = it
							} ?: run {
								clearBitmap()
								return@novelLoop
							}
						}

						// if the ID is still -1, return
						if (targetNovelID == -1) {
							logE("Could not find novel, even after injecting, aborting")
							clearBitmap()
							return@novelLoop
						}

						notify(R.string.restore_notification_content_chapters_load) {
							setContentTitle(name)
							setLargeIcon(bitmap)
						}
						// get the chapters
						val repoChapters =
							chaptersRepo.getChapters(targetNovelID).unwrap() ?: listOf()

						// Go through the chapters updating them
						for (index in bChapters.indices) {
							bChapters[index].let { (chapterURL, chapterName, bookmarked, rS, rP) ->
								repoChapters.find { it.url == chapterURL }?.let { chapterEntity ->
									notify(getString(R.string.updating) + ": ${chapterEntity.title}") {
										setContentTitle(name)
										setLargeIcon(bitmap)
										setProgress(bChapters.size, index, false)
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
						}

						notify(R.string.restore_notification_content_settings_restore) {
							setContentTitle(name)
							setLargeIcon(bitmap)
							removeProgress()
						}
						novelsSettingsRepo.get(targetNovelID).handle(
							onEmpty = {
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
							},
							onError = {
								logE("Failed to load novel settings")
								ACRA.errorReporter.handleException(it.exception)
							}
						) {
							logI("Updating novel settings")
							novelsSettingsRepo.update(
								it.copy(
									sortType = bSettings.sortType,
									showOnlyReadingStatusOf = bSettings.showOnlyReadingStatusOf,
									showOnlyBookmarked = bSettings.showOnlyBookmarked,
									showOnlyDownloaded = bSettings.showOnlyDownloaded,
									reverseOrder = bSettings.reverseOrder,
								)
							)
						}


						loadImageJob.join() // Finish the image loading job

						clearBitmap()// Remove data from bitmap
					}
				}
			}
		}

		System.gc() // Politely ask for a garbage collection
		delay(1000) // Wait for gc to occur (maybe), also helps with the next notification

		notify(R.string.restore_notification_content_completed) {
			setNotOngoing()
		}
		logI("Completed restore")
		return Result.success()
	}

	/**
	 * Manager of [BackupWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {

		override val count: Int
			get() = workerManager.getWorkInfosForUniqueWork(RESTORE_WORK_ID).get().size

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override fun isRunning(): Boolean = try {
			// Is this running
			val a = (getWorkerState() == WorkInfo.State.RUNNING)

			// Don't run if update is being installed
			val b = !AppUpdateInstallWorker.Manager(context).isRunning()
			a && b
		} catch (e: Exception) {
			false
		}

		override fun getWorkerState(index: Int): WorkInfo.State =
			workerManager.getWorkInfosForUniqueWork(RESTORE_WORK_ID).get()[index].state

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