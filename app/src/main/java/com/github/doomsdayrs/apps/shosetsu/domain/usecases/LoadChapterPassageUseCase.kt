package com.github.doomsdayrs.apps.shosetsu.domain.usecases

import android.util.Log
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.errorResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IChaptersRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IExtensionsRepository
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ReaderChapterUI

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
 * 10 / 06 / 2020
 */
class LoadChapterPassageUseCase(
		private val iChaptersRepository: IChaptersRepository,
		private val iExtensionsRepository: IExtensionsRepository
) {
	suspend operator fun invoke(chapter: ReaderChapterUI): HResult<String> {
		Log.d(logID(), "Getting chapter entity #${chapter.id}}")
		val chapterResult = iChaptersRepository.loadChapter(chapter.id)
		return if (chapterResult is HResult.Success) {
			Log.d(logID(), "Success")
			val chapterEntity = chapterResult.data
			val formatterResult = iExtensionsRepository.loadFormatter(chapterEntity.formatterID)
			if (formatterResult is HResult.Success) {
				iChaptersRepository.loadChapterPassage(formatterResult.data, chapterEntity)
			} else errorResult(ErrorKeys.ERROR_NOT_FOUND, "Formatter not found")
		} else errorResult(ErrorKeys.ERROR_NOT_FOUND, "Chapter not found")
	}
}