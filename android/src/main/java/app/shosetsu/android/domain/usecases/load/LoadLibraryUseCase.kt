package app.shosetsu.android.domain.usecases.load

import app.shosetsu.android.view.uimodels.model.library.ABookmarkedNovelUI
import app.shosetsu.android.view.uimodels.model.library.ComfyBookmarkedNovelUI
import app.shosetsu.android.view.uimodels.model.library.CompressedBookmarkedNovelUI
import app.shosetsu.android.view.uimodels.model.library.NormalBookmarkedNovelUI
import app.shosetsu.common.consts.settings.SettingKey.SelectedNovelCardType
import app.shosetsu.common.domain.repositories.base.INovelsRepository
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform
import app.shosetsu.common.enums.NovelCardType
import app.shosetsu.common.enums.NovelCardType.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
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
 * 08 / 05 / 2020
 */
class LoadLibraryUseCase(
	private val novelsRepo: INovelsRepository,
	private val settingsRepo: ISettingsRepository,
) {
	operator fun invoke(): Flow<HResult<List<ABookmarkedNovelUI>>> =
		novelsRepo.loadLibraryNovelEntities()
			.combine(settingsRepo.getIntFlow(SelectedNovelCardType).mapLatest {
				NovelCardType.valueOf(it)
			}) { origin, cardType ->
				origin.transform {
					val list = it
					val newList = list.map { (id, title, imageURL, bookmarked, unread,
						                         genres, authors, artists, tags) ->
						when (cardType) {
							NORMAL -> NormalBookmarkedNovelUI(
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
							COMPRESSED -> CompressedBookmarkedNovelUI(
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
							COZY -> ComfyBookmarkedNovelUI(
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
					}
					successResult(newList)
				}
			}
}