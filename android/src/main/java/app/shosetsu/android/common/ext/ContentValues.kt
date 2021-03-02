package app.shosetsu.android.common.ext

import android.content.ContentValues

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

operator fun ContentValues.set(key: String, value: String?) =
	put(key, value)

operator fun ContentValues.set(key: String, value: Int) =
	put(key, value)

operator fun ContentValues.set(key: String, value: Byte) =
	put(key, value)

operator fun ContentValues.set(key: String, value: Short) =
	put(key, value)

operator fun ContentValues.set(key: String, value: Long) =
	put(key, value)

operator fun ContentValues.set(key: String, value: Float) =
	put(key, value)

operator fun ContentValues.set(key: String, value: Double) =
	put(key, value)

operator fun ContentValues.set(key: String, value: ByteArray) =
	put(key, value)

