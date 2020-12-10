package app.shosetsu.android.providers.database.converters

import androidx.room.TypeConverter
import app.shosetsu.lib.Novel.Status
import app.shosetsu.lib.Novel.Status.Companion.fromInt

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
 */
class NovelStatusConverter {
	@TypeConverter
	fun toInt(status: Status): Int = status.key

	@TypeConverter
	fun toStatus(key: Int): Status = fromInt(key)
}