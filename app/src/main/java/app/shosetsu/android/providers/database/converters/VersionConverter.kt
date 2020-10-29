package app.shosetsu.android.providers.database.converters

import androidx.room.TypeConverter
import app.shosetsu.lib.Version

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
 * 17 / 10 / 2020
 */
class VersionConverter {
	@TypeConverter
	fun toString(v: Version?): String {
		if (v == null) return ""
		return with(v) { "$major.$minor.$patch" }
	}

	@TypeConverter
	fun toVersion(v: String?): Version? {
		if (v.isNullOrEmpty()) return null
		return Version(v)
	}
}