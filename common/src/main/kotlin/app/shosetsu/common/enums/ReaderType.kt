package app.shosetsu.common.enums

import app.shosetsu.lib.Novel.ChapterType

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
 * 25 / 12 / 2020
 *
 * Defines which reader is loaded for a novel
 *
 * Must not conflict with [chapterType]
 *
 * @param key Identifier
 * @param chapterType What the chapter type being loaded must be
 */
@Deprecated("Just use a raw usage of ChapterType")
enum class ReaderType(val key: Int, val chapterType: ChapterType) {

	STRING(1, ChapterType.STRING),

	/**
	 * Loads the chapter content up as a website
	 *
	 * @see app.shosetsu.lib.Novel.ChapterType.HTML
	 */
	WEB_VIEW(2, ChapterType.HTML),

	/**
	 * A basic e_pub reader
	 *
	 * @see app.shosetsu.lib.Novel.ChapterType.EPUB
	 */
	E_PUB(3, ChapterType.EPUB),

	/**
	 * A basic PDF reader
	 *
	 * @see app.shosetsu.lib.Novel.ChapterType.PDF
	 */
	PDF(4, ChapterType.PDF),

	/**
	 * A basic markdown reader
	 *
	 * @see app.shosetsu.lib.Novel.ChapterType.MARKDOWN
	 */
	MARK_DOWN(5, ChapterType.MARKDOWN);

	companion object {
		fun valueOf(key: Int) =
			values().find { it.key == key } ?: STRING

		fun valueOf(chapterType: ChapterType) =
			values().find { it.chapterType == chapterType } ?: STRING
	}
}