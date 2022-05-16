package app.shosetsu.android.domain.usecases.get

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.LuaException
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.domain.model.local.NovelEntity
import app.shosetsu.android.domain.model.local.UpdateEntity
import app.shosetsu.android.domain.repository.base.IChaptersRepository
import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.android.domain.repository.base.IUpdatesRepository

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
class GetRemoteNovelUseCase(
	private val nR: INovelsRepository,
	private val getExt: GetExtensionUseCase,
	private val cR: IChaptersRepository,
	private val uR: IUpdatesRepository,
) {
	/**
	 * Details regarding the state of an updated novel
	 */
	data class UpdatedNovelInfo(
		val updatedChapters: List<ChapterEntity> = listOf()
	)

	@Throws(SQLiteException::class, IndexOutOfBoundsException::class, LuaException::class)
	private suspend fun main(
		novel: NovelEntity,
		loadChapters: Boolean = true,
	): UpdatedNovelInfo? {
		logI("Loading novel data from internet for ${novel.id}")
		if (loadChapters) logI("And loading chapters for ${novel.id}")
		else logI("and not loading chapters for ${novel.id}")
		return getExt(novel.extensionID)?.let { ext ->
			nR.retrieveNovelInfo(ext, novel, loadChapters).let { page ->
				val hadNovelBeenLoaded: Boolean = novel.loaded

				// Fills the novel with new data
				nR.updateNovelData(novel, page)

				// If this novel has been loaded or not
				if (loadChapters) {
					if (!hadNovelBeenLoaded) {
						// If the novel has not been loaded, just handle the chapters
						logI("Novel has never been loaded, just inserting the chapters")
						cR.handleChapters(
							novelID = novel.id!!,
							extensionID = novel.extensionID,
							list = page.chapters
						).let {
							UpdatedNovelInfo()
						}
					} else {
						// If the novel has been loaded, handle the chapters and set them as updates
						logI("Novel has been loaded, sending update")
						cR.handleChaptersReturn(
							novelID = novel.id!!,
							extensionID = novel.extensionID,
							list = page.chapters
						).let { chapters ->
							uR.addUpdates(chapters.map {
								UpdateEntity(it.id!!, novel.id!!, System.currentTimeMillis())
							}).let {
								UpdatedNovelInfo(chapters)
							}
						}
					}
				} else {
					UpdatedNovelInfo()
				}
			}
		}
	}

	@Throws(SQLiteException::class, IndexOutOfBoundsException::class, LuaException::class)
	suspend operator fun invoke(
		novel: NovelEntity,
		loadChapters: Boolean = true,
	): UpdatedNovelInfo? = main(
		novel = novel,
		loadChapters = loadChapters
	)

	@Throws(SQLiteException::class, IndexOutOfBoundsException::class, LuaException::class)
	suspend operator fun invoke(
		novelID: Int,
		loadChapters: Boolean = true,
	): UpdatedNovelInfo? = nR.getNovel(novelID)?.let { novel ->
		main(
			novel = novel,
			loadChapters = loadChapters
		)
	}

}