package app.shosetsu.android.domain.usecases.load

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.shosetsu.android.common.ShosetsuSettings
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.android.view.uimodels.model.library.ABookmarkedNovelUI
import app.shosetsu.android.view.uimodels.model.library.CompactBookmarkedNovelUI
import app.shosetsu.android.view.uimodels.model.library.FullBookmarkedNovelUI

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
 * 08 / 05 / 2020
 */
class LoadLibraryUseCase(
		private val iNovelsRepository: INovelsRepository,
		private val settings: ShosetsuSettings,
) : (() -> LiveData<HResult<List<ABookmarkedNovelUI>>>) {
	override fun invoke(): LiveData<HResult<List<ABookmarkedNovelUI>>> {
		return liveData {
			emitSource(iNovelsRepository.getLiveBookmarked().map { origin ->
				when (origin) {
					is HResult.Success -> {
						val list = origin.data
						val newList =
								list.map { (id, title, imageURL, bookmarked, unread) ->
									if (settings.novelCardType == 0)
										FullBookmarkedNovelUI(
												id,
												title,
												imageURL,
												bookmarked,
												unread
										)
									else CompactBookmarkedNovelUI(
											id,
											title,
											imageURL,
											bookmarked,
											unread
									)
								}
						successResult(newList)
					}
					is HResult.Error -> origin
					is HResult.Loading -> origin
					is HResult.Empty -> origin
				}
			})
		}
	}


}