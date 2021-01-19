package app.shosetsu.android.backend.workers.onetime

import android.content.Context
import android.util.Base64
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.shosetsu.android.common.utils.backupJSON
import app.shosetsu.android.domain.model.local.backup.*
import app.shosetsu.common.domain.model.local.BackupEntity
import app.shosetsu.common.domain.repositories.base.*
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.unwrap
import kotlinx.serialization.encodeToString
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream
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
), KodeinAware {

	override val kodein: Kodein by closestKodein(appContext)
	private val novelRepository by instance<INovelsRepository>()

	/**
	 * TODO add settings backup
	 */
	private val novelSettingsRepository by instance<INovelSettingsRepository>()
	private val extensionsRepository by instance<IExtensionsRepository>()
	private val chaptersRepository by instance<IChaptersRepository>()
	private val extensionRepoRepository by instance<IExtensionRepoRepository>()
	private val backupRepository by instance<IBackupRepository>()


	@Throws(IOException::class)
	fun gzip(content: String): ByteArray {
		val bos = ByteArrayOutputStream()
		GZIPOutputStream(bos).bufferedWriter().use { it.write(content) }
		return bos.toByteArray()
	}

	@Throws(IOException::class)
	fun ungzip(content: ByteArray): String =
		GZIPInputStream(content.inputStream()).bufferedReader().use { it.readText() }

	@Throws(IOException::class)
	override suspend fun doWork(): Result {
		// Load novels
		novelRepository.getBookmarkedNovels().handle { novels ->
			// Novels to their chapters
			val novelsToChapters = novels.map {
				it to (chaptersRepository.getChapters(it.id!!).unwrap()?.map { chapterEntity ->
					BackupChapterEntity(
						chapterEntity.url,
						chapterEntity.title,
						chapterEntity.bookmarked,
						chapterEntity.readingStatus,
						chapterEntity.readingPosition
					)
				} ?: listOf())
			}

			// Extensions each novel requires
			// Distinct, with no duplicates
			val extensions = novels.map {
				extensionsRepository.getExtensionEntity(it.extensionID).unwrap()!!
			}.distinct()

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

			val backup = FleshedBackupEntity(
				repos = repositoriesRequired,
				// Creates the trees
				extensions = extensions.map { extensionEntity ->
					BackupExtensionEntity(
						extensionEntity.id,
						novelsToChapters.filter { (novel, _) ->
							novel.extensionID == extensionEntity.id
						}.map { (novel, chapters) ->
							BackupNovelEntity(
								novel.url,
								novel.title,
								novel.imageURL,
								chapters
							)
						}
					)
				}
			)

			val stringBackup = backupJSON.encodeToString(backup)

			val zippedBytes = gzip(stringBackup)

			val base64Bytes = Base64.encodeToString(zippedBytes, Base64.DEFAULT)

			backupRepository.saveBackup(
				BackupEntity(
					base64Bytes
				)
			)
			return Result.success()
		}
		return Result.failure()
	}


}