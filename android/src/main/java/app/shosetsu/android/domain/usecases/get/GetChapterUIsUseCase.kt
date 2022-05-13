package app.shosetsu.android.domain.usecases.get

import app.shosetsu.android.common.utils.uifactory.mapLatestToResultFlowWithFactory
import app.shosetsu.android.domain.repository.base.IChaptersRepository
import app.shosetsu.android.dto.convertList
import app.shosetsu.android.view.uimodels.model.ChapterUI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest

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
 * 18 / 05 / 2020
 */
class GetChapterUIsUseCase(
	private val chapters: IChaptersRepository,
) {
	@OptIn(ExperimentalCoroutinesApi::class)
	operator fun invoke(novelID: Int): Flow<List<ChapterUI>> = flow {
		if (novelID != -1)
			emitAll(
				chapters.getChaptersLive(novelID).mapLatestToResultFlowWithFactory()
					.mapLatest { it.convertList() }
			)
	}
}