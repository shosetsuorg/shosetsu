package app.shosetsu.android.domain.usecases

import app.shosetsu.android.domain.usecases.load.LoadNovelUseCase
import app.shosetsu.android.domain.usecases.update.UpdateNovelUseCase
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.domain.model.local.NovelEntity

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
 */
class NovelBackgroundAddUseCase(
		private val loadNovelUseCase: LoadNovelUseCase,
		private val updateNovelEntityUseCase: UpdateNovelUseCase,
) {
	suspend operator fun invoke(novelID: Int): HResult<*> =
			loadNovelUseCase(novelID, false).also {
				if (it is HResult.Success<*>) {
					if (it.data is NovelEntity) {
						updateNovelEntityUseCase((it.data as NovelEntity).copy(
								bookmarked = true
						))
					}
				}
			}
}