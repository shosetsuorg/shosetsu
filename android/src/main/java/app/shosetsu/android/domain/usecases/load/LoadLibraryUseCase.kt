package app.shosetsu.android.domain.usecases.load

import android.database.sqlite.SQLiteException
import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.android.view.uimodels.model.LibraryNovelUI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
) {
	@Throws(SQLiteException::class)
	@OptIn(ExperimentalCoroutinesApi::class)
	operator fun invoke(): Flow<List<LibraryNovelUI>> =
		novelsRepo.loadLibraryNovelEntities().mapLatest { origin ->
			origin.map { (id,
							 title,
							 imageURL,
							 bookmarked,
							 unread,
							 genres,
							 authors,
							 artists,
							 tags) ->
				LibraryNovelUI(
					id, title, imageURL, bookmarked, unread, genres, authors, artists, tags
				)
			}
		}
}