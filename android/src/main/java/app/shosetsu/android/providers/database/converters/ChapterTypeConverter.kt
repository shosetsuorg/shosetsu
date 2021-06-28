package app.shosetsu.android.providers.database.converters

import androidx.room.TypeConverter
import app.shosetsu.lib.Novel.ChapterType
import app.shosetsu.lib.Novel.ChapterType.Companion.valueOf

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
 *
 * Converts a [ChapterType] from it's enum form to integer form and back
 */
class ChapterTypeConverter {
	@TypeConverter
	fun toString(readerType: ChapterType): Int = readerType.key

	@TypeConverter
	fun toSortType(key: Int): ChapterType = valueOf(key)
}