package com.github.doomsdayrs.apps.shosetsu.domain.model.local

import androidx.annotation.NonNull
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.domain.model.base.Convertible
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ChapterReaderUI

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
 * 06 / 05 / 2020
 */
data class ChapterReaderSubEntity(
		val id: Int,
		@NonNull
		val url: String,

		@NonNull
		var title: String,

		var readingPosition: Int = 0,

		var readingStatus: ReadingStatus = ReadingStatus.UNREAD,

		var bookmarked: Boolean = false,

		var isSaved: Boolean = false,

		var savePath: String = ""
) : Convertible<ChapterReaderUI> {
	override fun convertTo(): ChapterReaderUI = ChapterReaderUI(
			id,
			url,
			title,
			readingPosition,
			readingStatus,
			bookmarked,
			isSaved,
			savePath
	)

}