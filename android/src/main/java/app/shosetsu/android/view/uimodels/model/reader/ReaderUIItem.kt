package app.shosetsu.android.view.uimodels.model.reader

import androidx.compose.runtime.Immutable
import app.shosetsu.android.domain.model.local.ReaderChapterEntity
import app.shosetsu.android.dto.Convertible

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
	 */
	@Immutable
	data class ReaderChapterUI(
		val chapter: ReaderChapterEntity
	) : Convertible<ReaderChapterEntity>, ReaderUIItem() {
		val id: Int
			get() = chapter.id
		val title: String
			get() = chapter.title

		override fun convertTo(): ReaderChapterEntity = chapter
	}

	/**
	 * Divides each chapter, signalling what is from before and what is next
	 * Will always appear after the first chapter, never before the first.
	 * Will appear after the last chapter, but stating there are no more chapters
	 */
	@Immutable
	data class ReaderDividerUI(
		val prev: ReaderChapterUI,
		val next: ReaderChapterUI? = null
	) : ReaderUIItem()
}