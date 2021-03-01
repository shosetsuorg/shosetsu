package app.shosetsu.android.domain.usecases.get

import app.shosetsu.android.common.ext.ifSo
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.domain.usecases.DownloadChapterPassageUseCase
import app.shosetsu.common.consts.settings.SettingKey.IsDownloadOnUpdate
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.domain.model.local.UpdateEntity
import app.shosetsu.common.domain.repositories.base.*
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform

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
 * 15 / 05 / 2020
 *
 * takes a novelID & parameters, then loads it's data to storage
 */
class GetNovelUseCase(
	private val nR: INovelsRepository,
	private val eR: IExtensionsRepository,
	private val cR: IChapterEntitiesRepository,
	private val uR: IUpdatesRepository,
	private val sR: ISettingsRepository,
	private val download: DownloadChapterPassageUseCase
) {
	private suspend fun main(
		novel: NovelEntity,
		loadChapters: Boolean,
		haveChaptersUpdate: () -> Unit = {}
	): HResult<Boolean> =
		eR.getIExtension(novel.extensionID).transform { ext ->
			nR.retrieveNovelInfo(ext, novel, loadChapters).transform { page ->
				val currentStatus: Boolean = novel.loaded

				// Fills the novel with new data
				nR.updateNovelData(novel, page)

				// If this novel has been loaded or not
				if (loadChapters) {
					if (!currentStatus)
						cR.handleChapters(
							novelID = novel.id!!,
							extensionID = novel.extensionID,
							list = page.chapters
						)
					else cR.handleChaptersReturn(
						novelID = novel.id!!,
						extensionID = novel.extensionID,
						list = page.chapters
					).handle { chapters ->
						if (chapters.isNotEmpty()) haveChaptersUpdate()
						uR.addUpdates(chapters.map {
							UpdateEntity(it.id!!, novel.id!!, System.currentTimeMillis())
						})
						sR.getBoolean(IsDownloadOnUpdate).handle { downloadUpdates ->
							downloadUpdates ifSo chapters.forEach { download(it) }
								?: logI("Not installing updates")
						}

					}
				}
				successResult(true)
			}
		}

	suspend operator fun invoke(
		novel: NovelEntity,
		loadChapters: Boolean,
		haveChaptersUpdate: () -> Unit = {}
	): HResult<Any> = main(novel, loadChapters, haveChaptersUpdate)

	suspend operator fun invoke(
		novelID: Int,
		loadChapters: Boolean
	): HResult<Any> = nR.getNovel(novelID).transform { novel ->
		main(novel, loadChapters)
	}

}