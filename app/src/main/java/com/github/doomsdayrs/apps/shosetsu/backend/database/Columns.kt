package com.github.doomsdayrs.apps.shosetsu.backend.database

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
 * ====================================================================
 */ /**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
internal enum class Columns(private val COLUMN: String) {
    URL("url"), PARENT_ID("parent_id"), ID("id"), READER_TYPE("reader_type"), TITLE("title"), IMAGE_URL("image_url"), DESCRIPTION("description"), GENRES("genres"), AUTHORS("authors"), STATUS("status"), TAGS("tags"), ARTISTS("artists"), LANGUAGE("language"), MAX_CHAPTER_PAGE("max_chapter_page"), RELEASE_DATE("release_date"), ORDER("order_of"), FORMATTER_ID("formatterID"), READ_CHAPTER("read"), Y("y"), BOOKMARKED("bookmarked"), IS_SAVED("isSaved"), SAVE_PATH("savePath"), NOVEL_NAME("novelName"), CHAPTER_NAME("chapterName"), PAUSED("paused"), READING_STATUS("reading_status"), TIME("time");

    override fun toString(): String {
        return COLUMN
    }

}