package app.shosetsu.android.domain.usecases.delete

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.common.NoSuchExtensionException
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.domain.repository.base.IChaptersRepository
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.android.dto.convertList
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
 * shosetsu
 * 26 / 06 / 2020
 */
class DeleteChapterPassageUseCase(
	private val iChaptersRepository: IChaptersRepository,
	private val iExtensionsRepository: IExtensionsRepository
) {
	suspend operator fun invoke(chapterUI: ChapterUI) {
		this(arrayOf(chapterUI.convertTo()))
	}

	suspend operator fun invoke(chapter: ChapterEntity) {
		this(arrayOf(chapter))
	}

	@Throws(
		SQLiteException::class,
		NoSuchExtensionException::class,
		FilePermissionException::class
	)
	suspend operator fun invoke(chapters: List<ChapterUI>) {
		invoke(chapters.convertList().toTypedArray())
	}

	@Throws(
		SQLiteException::class,
		NoSuchExtensionException::class,
		FilePermissionException::class
	)
	suspend operator fun invoke(chapters: Array<ChapterEntity>) {
		val ext = iExtensionsRepository.getInstalledExtension(chapters.first().extensionID)
			?: throw NoSuchExtensionException(chapters.first().extensionID)

		iChaptersRepository.deleteChapterPassage(
			chapters,
			ext.chapterType
		)
	}
}