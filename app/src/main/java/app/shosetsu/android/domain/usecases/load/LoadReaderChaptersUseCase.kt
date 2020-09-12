package app.shosetsu.android.domain.usecases.load

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.loading
import app.shosetsu.android.common.dto.mapListTo
import app.shosetsu.android.domain.repository.base.IChaptersRepository
import app.shosetsu.android.view.uimodels.model.ReaderChapterUI

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
) : ((@kotlin.ParameterName("novelID") Int) -> LiveData<HResult<List<ReaderChapterUI>>>) {
	override fun invoke(novelID: Int): LiveData<HResult<List<ReaderChapterUI>>> =
			liveData<HResult<List<ReaderChapterUI>>> {
				emit(loading())
				emitSource(iChaptersRepository.loadReaderChapters(novelID).map { it.mapListTo() })
			}
}