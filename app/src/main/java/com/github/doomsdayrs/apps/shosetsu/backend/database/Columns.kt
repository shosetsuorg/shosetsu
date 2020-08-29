package com.github.doomsdayrs.apps.shosetsu.backend.database

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
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
@Deprecated("SQL Database removed")
enum class Columns(private val key: String) {
	URL("url"),
	PARENT_ID("parent_id"),
	ID("id"),

	TITLE("title"),
	IMAGE_URL("image_url"),
	DESCRIPTION("description"),
	GENRES("genres"),
	AUTHORS("authors"),
	STATUS("status"),
	TAGS("tags"),
	ARTISTS("artists"),
	LANGUAGE("language"),
	MAX_CHAPTER_PAGE("max_chapter_page"),

	RELEASE_DATE("release_date"),
	ORDER("order_of"),

	FORMATTER_ID("formatterID"),
	READ_CHAPTER("read"),
	Y_POSITION("y"),
	BOOKMARKED("bookmarked"),
	;
	override fun toString(): String = key
}