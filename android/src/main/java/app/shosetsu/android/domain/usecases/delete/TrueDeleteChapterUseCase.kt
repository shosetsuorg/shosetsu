package app.shosetsu.android.domain.usecases.delete

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.common.NoSuchExtensionException
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.domain.repository.base.IChaptersRepository
import app.shosetsu.android.view.uimodels.model.ChapterUI

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
 * Shosetsu
 *
 * @since 18 / 11 / 2021
 * @author Doomsdayrs
 */
class TrueDeleteChapterUseCase(
	private val repo: IChaptersRepository,
	private val deleteChapter: DeleteChapterPassageUseCase
) {
	suspend operator fun invoke(chapterUI: ChapterUI) {
		this(chapterUI.convertTo())
	}

	@Throws(
		SQLiteException::class,
		NoSuchExtensionException::class,
		FilePermissionException::class
	)
	suspend operator fun invoke(chapter: ChapterEntity) {
		deleteChapter(chapter)
		repo.delete(chapter)
	}
}