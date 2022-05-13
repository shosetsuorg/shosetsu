package app.shosetsu.android.view.uimodels.model

import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.dto.Convertible

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
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
data class ChapterUI(
	val id: Int,
	val novelID: Int,
	val link: String,
	val extensionID: Int,
	var title: String,
	var releaseDate: String,
	var order: Double,
	var readingPosition: Double,
	var readingStatus: ReadingStatus,
	var bookmarked: Boolean,
	var isSaved: Boolean,
	var isSelected: Boolean = false
) : Convertible<ChapterEntity> {

	override fun convertTo(): ChapterEntity =
		ChapterEntity(
			id,
			link,
			novelID,
			extensionID,
			title,
			releaseDate,
			order,
			readingPosition,
			readingStatus,
			bookmarked,
			isSaved
		)

}
