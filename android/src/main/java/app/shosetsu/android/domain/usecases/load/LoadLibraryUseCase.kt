package app.shosetsu.android.domain.usecases.load

import app.shosetsu.common.domain.repositories.base.INovelsRepository
import app.shosetsu.android.view.uimodels.model.library.ABookmarkedNovelUI
import app.shosetsu.android.view.uimodels.model.library.CompactBookmarkedNovelUI
import app.shosetsu.android.view.uimodels.model.library.FullBookmarkedNovelUI
import app.shosetsu.common.consts.settings.SettingKey.NovelCardType
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

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
	private val settings: ISettingsRepository,
) {
	operator fun invoke(): Flow<HResult<List<ABookmarkedNovelUI>>> =
		iNovelsRepository.getBookmarkedNovelsFlow()
			.combine(settings.getIntFlow(NovelCardType)) { origin, cardType ->
				origin.transform {
					val list = it
					val newList = list.map { (id, title, imageURL, bookmarked, unread,
						                         genres, authors, artists, tags) ->
						if (cardType == 0)
							FullBookmarkedNovelUI(
								id = id,
								title = title,
								imageURL = imageURL,
								bookmarked = bookmarked,
								unread = unread,
								genres = genres,
								authors = authors,
								artists = artists,
								tags = tags
							)
						else CompactBookmarkedNovelUI(
							id = id,
							title = title,
							imageURL = imageURL,
							bookmarked = bookmarked,
							unread = unread,
							genres = genres,
							authors = authors,
							artists = artists,
							tags = tags
						)
					}
					successResult(newList)
				}
			}
}