package app.shosetsu.android.domain.usecases

import android.database.sqlite.SQLiteException
import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.android.domain.usecases.get.GetRemoteNovelUseCase
import app.shosetsu.android.domain.usecases.update.UpdateNovelUseCase

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
 * Add a novel in the background
 * This will not load chapters of the novel
 * This will bookmark the novel after loading it up
 */
class NovelBackgroundAddUseCase(
	private val loadRemoteNovelUseCase: GetRemoteNovelUseCase,
	private val updateNovelEntityUseCase: UpdateNovelUseCase,
	private val novelsRepository: INovelsRepository
) {
	@Throws(SQLiteException::class)
	suspend operator fun invoke(novelID: Int) {
		return loadRemoteNovelUseCase(novelID, false).let {
			novelsRepository.getNovel(novelID)?.let { entity ->
				updateNovelEntityUseCase(entity.copy(bookmarked = true))
			}
		}
	}
}