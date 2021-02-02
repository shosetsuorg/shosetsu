package app.shosetsu.android.backend.workers.onetime

import android.content.Context
import android.util.Base64
import androidx.work.*
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.WorkerTags.RESTORE_WORK_ID
import app.shosetsu.android.common.ext.asEntity
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.utils.backupJSON
import app.shosetsu.android.domain.model.local.backup.FleshedBackupEntity
import app.shosetsu.android.domain.usecases.InitializeExtensionsUseCase
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.domain.repositories.base.*
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.unwrap
import app.shosetsu.common.enums.ReadingStatus
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.io.IOException
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
), KodeinAware {
	override val kodein: Kodein by closestKodein(appContext)

	private val backupRepo by instance<IBackupRepository>()
	private val extensionsRepoRepo by instance<IExtensionRepoRepository>()
	private val initializeExtensionsUseCase by instance<InitializeExtensionsUseCase>()
	private val extensionsRepo by instance<IExtensionsRepository>()
	private val novelsRepo by instance<INovelsRepository>()
	private val chaptersRepo by instance<IChaptersRepository>()

	@Throws(IOException::class)
	private fun unGZip(content: ByteArray): String =
		GZIPInputStream(content.inputStream()).bufferedReader().use { it.readText() }

	@Throws(IOException::class)
	override suspend fun doWork(): Result {
		val backupName = inputData.getString(BACKUP_DATA_KEY) ?: return Result.failure()
		backupRepo.loadBackup(backupName).handle(
			onEmpty = {
				return Result.failure()
			},
			onLoading = {
				return Result.failure()
			},
			onError = { (code, message, exception) ->
				logE("$code $message", exception)
				return Result.failure()
			}
		) { backupEntity ->
			// Decode
			val decodedBytes: ByteArray = Base64.decode(backupEntity.content, Base64.DEFAULT)

			// Unzip
			val unzippedString: String = unGZip(decodedBytes)


			// Verifies the version is compatible and then parses it to a backup object
			val backup = backupJSON.decodeFromJsonElement<FleshedBackupEntity>(
				backupJSON.parseToJsonElement(
					unzippedString
				).jsonObject.also { jsonObject ->
					if (jsonObject.containsKey("version")) {
						if (jsonObject["version"]!!.jsonPrimitive.toString() != SUPPORTED_VERSION)
							return Result.failure()
					} else return Result.failure()
				})

			// Adds the repositories
			backup.repos.forEach { (url, name) ->
				extensionsRepoRepo.addRepository(
					RepositoryEntity(
						url = url,
						name = name
					)
				)
			}

			// Load the data from the repositories
			initializeExtensionsUseCase.invoke { }

			// Install the extensions
			val repoNovels: List<NovelEntity> = novelsRepo.loadNovels().unwrap()!!

			backup.extensions.forEach { (extensionID, novels) ->
				extensionsRepo.getExtensionEntity(extensionID).handle { extensionEntity ->
					// Install the extension
					if (!extensionEntity.installed)
						extensionsRepo.installExtension(extensionEntity)
					val iExt = extensionsRepo.getIExtension(extensionEntity).unwrap()!!

					novels.forEach novelLoop@{ (novelURL, _, _, chapters) ->
						// If none match the extension ID and URL, time to load it up
						var targetNovelID = -1
						if (repoNovels.none { it.extensionID == extensionEntity.id && it.url == novelURL }) {
							val siteNovel = iExt.parseNovel(novelURL, true)
							novelsRepo.insertReturnStripped(
								siteNovel.asEntity(
									novelURL,
									extensionID
								)
							).handle { (id) ->
								targetNovelID = id
							}
						} else {
							// Get the novelID from the present novels, or just end this update
							repoNovels.find { it.extensionID == extensionEntity.id && it.url == novelURL }?.id?.let { it ->
								targetNovelID = it
							} ?: return@novelLoop
						}

						// if the ID is still -1, return
						if (targetNovelID == -1) return@novelLoop

						// get the chapters
						val repoChapters =
							chaptersRepo.getChapters(targetNovelID).unwrap() ?: listOf()

						// Go through the chapters updating them
						chapters.forEach { (chapterURL, chapterName, bookmarked, rS, rP) ->
							repoChapters.find { it.url == chapterURL }?.let { chapterEntity ->
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
				}
			}
		}
		return Result.success()
	}

	/**
	 * Manager of [BackupWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override fun isRunning(): Boolean = try {
			// Is this running
			val a = (workerManager.getWorkInfosForUniqueWork(RESTORE_WORK_ID)
				.get()[0].state == WorkInfo.State.RUNNING)

			// Don't run if update is being installed
			val b = !AppUpdateInstallWorker.Manager(context).isRunning()
			a && b
		} catch (e: Exception) {
			false
		}

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
					OneTimeWorkRequestBuilder<AppUpdateCheckWorker>(
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
		const val SUPPORTED_VERSION = "1.0.0"
		const val BACKUP_DATA_KEY = "BACKUP_NAME"
	}
}