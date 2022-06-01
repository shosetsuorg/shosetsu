package app.shosetsu.android.view.uimodels.model.reader

import app.shosetsu.android.domain.model.local.ReaderChapterEntity
import app.shosetsu.android.dto.Convertible
import app.shosetsu.lib.Novel

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * shosetsu
 * 13 / 11 / 2020
 */
sealed class ReaderUIItem {
	/**
	 * Data class that holds each chapter and its data (not including text content)
	 *
	 * @param id Id of the chapter in shosetsu db
	 * @param link URL of the chapter
	 * @param readingPosition Where the user last left off while reading
	 * @param readingStatus What is the reading status of the chapter
	 * @param bookmarked Is the chapter bookmarked
	 * @param chapterType What type of view to use for loading,
	 * this is defined by the the extension first,
	 * otherwise the user choice will dictate what reader is used
	 *
	 * @param convertStringToHtml Convert a string chapter to an html chapter
	 */
	data class ReaderChapterUI(
		val id: Int,
		val title: String,
		val chapterType: Novel.ChapterType,
		val convertStringToHtml: Boolean = false
	) : Convertible<ReaderChapterEntity>, ReaderUIItem() {

		override fun convertTo(): ReaderChapterEntity = ReaderChapterEntity(
			id,
			title
		)
	}

	/**
	 * Divides each chapter, signalling what is from before and what is next
	 * Will always appear after the first chapter, never before the first.
	 * Will appear after the last chapter, but stating there are no more chapters
	 */
	data class ReaderDividerUI(
		val prev: String,
		val next: String? = null
	) : ReaderUIItem()
}