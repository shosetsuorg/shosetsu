package app.shosetsu.android.view.uimodels.model

import androidx.compose.runtime.Immutable
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
 * @param isSaved the chapters is downloaded
 */
@Immutable
data class ChapterUI(
	val id: Int,
	val novelID: Int,
	val link: String,
	val extensionID: Int,
	val title: String,
	val releaseDate: String,
	val order: Double,
	val readingPosition: Double,
	val readingStatus: ReadingStatus,
	val bookmarked: Boolean,
	val isSaved: Boolean,
	val isSelected: Boolean = false
) : Convertible<ChapterEntity> {

	val displayPosition = "%2.1f%%".format(readingPosition * 100)

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
