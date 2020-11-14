package app.shosetsu.android.domain.usecases.load

import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.loading
import app.shosetsu.android.common.dto.mapLatestResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.domain.repository.base.IChaptersRepository
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI
import app.shosetsu.lib.Novel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

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
 * 07 / 06 / 2020
 */
class LoadReaderChaptersUseCase(
		private val iChaptersRepository: IChaptersRepository,
) {
	operator fun invoke(novelID: Int): Flow<HResult<List<ReaderChapterUI>>> =
			flow {
				emit(loading())
				emitAll(iChaptersRepository.loadReaderChapters(novelID).mapLatestResult {
					successResult(it.map { (id, url, title, readingPosition, readingStatus, bookmarked) ->
						ReaderChapterUI(id, url, title, readingPosition, readingStatus, bookmarked, Novel.ChapterType.STRING)
					})
				})
			}
}