package app.shosetsu.android.common.ext

import android.database.Cursor

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

fun Cursor.getShort(key: String): Short =
	getShort(getColumnIndex(key))

fun Cursor.getInt(key: String): Int =
	getInt(getColumnIndex(key))

fun Cursor.getLong(key: String): Long =
	getLong(getColumnIndex(key))

fun Cursor.getBlob(key: String): ByteArray =
	getBlob(getColumnIndex(key))

fun Cursor.getFloat(key: String): Float =
	getFloat(getColumnIndex(key))

fun Cursor.getDouble(key: String): Double =
	getDouble(getColumnIndex(key))

fun Cursor.getString(key: String): String =
	getString(getColumnIndex(key))

fun Cursor.getStringOrNull(key: String): String? =
	getString(getColumnIndex(key))