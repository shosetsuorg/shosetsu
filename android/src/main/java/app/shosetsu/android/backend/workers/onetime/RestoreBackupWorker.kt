package app.shosetsu.android.backend.workers.onetime

import android.content.Context
import android.util.Base64
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.shosetsu.android.common.ext.asEntity
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.utils.backupJSON
import app.shosetsu.android.domain.model.local.backup.FleshedBackupEntity
import app.shosetsu.android.domain.usecases.InitializeExtensionsUseCase
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.domain.repositories.base.*
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.unwrap
import app.shosetsu.lib.IExtension
import kotlinx.serialization.decodeFromString
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
			}
		) { backupEntity ->
			val decodedBytes: ByteArray = Base64.decode(backupEntity.content, Base64.DEFAULT)
			val unzippedString: String = unGZip(decodedBytes)
			val backup = backupJSON.decodeFromString<FleshedBackupEntity>(unzippedString)

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
			val presentNovels: List<NovelEntity> = novelsRepo.loadNovels().unwrap()!!

			backup.extensions.forEach { (extensionID, novels) ->
				extensionsRepo.getExtensionEntity(extensionID).handle { extensionEntity ->
					// Install the extension
					if (!extensionEntity.installed)
						extensionsRepo.installExtension(extensionEntity)
					val iExt = extensionsRepo.getIExtension(extensionEntity).unwrap()!!

					novels.forEach { (url, name, imageURL, chapters) ->
						// If none match the extension ID and URL, time to load it up
						if (presentNovels.none { it.extensionID == extensionEntity.id && it.url == url }) {
							val expandedURL = iExt.expandURL(url, IExtension.KEY_NOVEL_URL)
							val siteNovel = iExt.parseNovel(expandedURL, true)
							novelsRepo.insert(siteNovel.asEntity(url, extensionID)).handle {
							}
						}
					}
				}
			}
		}
		return Result.failure()
	}


	companion object {
		const val BACKUP_DATA_KEY = "BACKUP_NAME"
	}
}