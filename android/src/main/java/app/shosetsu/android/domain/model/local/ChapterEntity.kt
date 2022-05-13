package app.shosetsu.android.domain.model.local

import app.shosetsu.android.common.enums.ReadingStatus

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
 * ====================================================================
 */

/**
 * shosetsu
 * 23 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 * @param readingPosition The progress through an chapter. Will be interpreted as 0 - 99%
 */
data class ChapterEntity(
	var id: Int? = null,

	var url: String,

	val novelID: Int,

	val extensionID: Int,

	var title: String,

	var releaseDate: String,

	var order: Double,

	var readingPosition: Double = 0.0,

	var readingStatus: ReadingStatus = ReadingStatus.UNREAD,

	var bookmarked: Boolean = false,

	var isSaved: Boolean = false,
)