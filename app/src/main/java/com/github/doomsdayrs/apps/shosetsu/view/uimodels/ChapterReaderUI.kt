package com.github.doomsdayrs.apps.shosetsu.view.uimodels

import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.domain.model.base.Convertible
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ChapterReaderSubEntity

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
data class ChapterReaderUI(
		val id: Int,
		val link: String,
		var title: String,
		var readingPosition: Int,
		var readingReadingStatus: ReadingStatus,
		var bookmarked: Boolean,
		var isSaved: Boolean,
		var savePath: String
) : Convertible<ChapterReaderSubEntity> {
	override fun convertTo(): ChapterReaderSubEntity {
		TODO("Not yet implemented")
	}

}