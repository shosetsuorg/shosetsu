package app.shosetsu.android.domain.usecases.update

import android.database.sqlite.SQLiteException
import app.shosetsu.android.domain.model.local.NovelEntity
import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.android.view.uimodels.model.NovelUI

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
 * 06 / 06 / 2020
 */
class UpdateNovelUseCase(
	private val chaptersRepository: INovelsRepository,
) {
	suspend operator fun invoke(novelUI: NovelUI) =
		this(novelUI.convertTo())

	@Throws(SQLiteException::class)
	suspend operator fun invoke(novelEntity: NovelEntity) =
		chaptersRepository.update(novelEntity)
}