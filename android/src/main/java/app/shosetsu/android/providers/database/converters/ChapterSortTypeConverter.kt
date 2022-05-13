package app.shosetsu.android.providers.database.converters

import androidx.room.TypeConverter
import app.shosetsu.android.common.enums.ChapterSortType

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
 * 02 / 01 / 2021
 */
class ChapterSortTypeConverter {
	@TypeConverter
	fun toString(chapterSortType: ChapterSortType?): String? =
		chapterSortType?.name

	@TypeConverter
	fun toSortType(key: String?): ChapterSortType? =
		key?.let { ChapterSortType.valueOf(it) }
}