package app.shosetsu.android.domain.usecases.load

import app.shosetsu.android.common.consts.ErrorKeys.ERROR_GENERAL
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.domain.model.local.NovelEntity
import app.shosetsu.android.domain.model.local.UpdateEntity
import app.shosetsu.android.domain.repository.base.IChaptersRepository
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
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
class LoadNovelUseCase(
		private val nR: INovelsRepository,
		private val eR: IExtensionsRepository,
		private val cR: IChaptersRepository,
		private val uR: IUpdatesRepository,
) {
	private suspend fun main(novel: NovelEntity, loadChapters: Boolean) =
			when (val fR = eR.loadFormatter(novel.formatterID)) {
				is HResult.Success -> {
					val ext = fR.data
					when (val pR = nR.retrieveNovelInfo(ext, novel, loadChapters)) {
						is HResult.Success -> {
							val page = pR.data
							val currentStatus = novel.loaded
							//Log.d(logID(), "Loaded novel info $page")
							nR.updateNovelData(novel, page)
							if (loadChapters)
								if (!currentStatus)
									cR.handleChapters(novel, page.chapters)
								else cR.handleChaptersReturn(novel, page.chapters).let { cLR ->
									when (cLR) {
										is HResult.Success -> {
											uR.addUpdates(cLR.data.map {
												UpdateEntity(it.id!!, novel.id!!, System.currentTimeMillis())
											})
											successResult(novel)
										}
										is HResult.Error -> cLR
										else -> errorResult(ERROR_GENERAL, "Unknown failure")
									}
								}
							successResult(true)
						}
						is HResult.Error -> pR
						else -> errorResult(ERROR_GENERAL, "Unknown failure")
					}
				}
				is HResult.Error -> fR
				else -> errorResult(ERROR_GENERAL, "Unknown failure")
			}

	suspend operator fun invoke(novel: NovelEntity, loadChapters: Boolean): HResult<Any> =
			main(novel, loadChapters)

	suspend operator fun invoke(novelID: Int, loadChapters: Boolean): HResult<Any> =
			when (val nResult = nR.loadNovel(novelID)) {
				is HResult.Success -> {
					val novel = nResult.data
					main(novel, loadChapters)
				}
				is HResult.Error -> nResult
				else -> errorResult(ERROR_GENERAL, "Unknown failure")
			}
}