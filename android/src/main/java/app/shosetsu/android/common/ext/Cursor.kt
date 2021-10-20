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
	getColumnIndex(key).takeIf { it >= 0 }?.let { getShort(it) }!!


fun Cursor.getInt(key: String): Int =
	getColumnIndex(key).takeIf { it >= 0 }?.let { getInt(it) }!!

fun Cursor.getLong(key: String): Long =
	getColumnIndex(key).takeIf { it >= 0 }?.let { getLong(it) }!!

fun Cursor.getBlob(key: String): ByteArray =
	getColumnIndex(key).takeIf { it >= 0 }?.let { getBlob(it) }!!

fun Cursor.getFloat(key: String): Float =
	getColumnIndex(key).takeIf { it >= 0 }?.let { getFloat(it) }!!

fun Cursor.getDouble(key: String): Double =
	getColumnIndex(key).takeIf { it >= 0 }?.let { getDouble(it) }!!

fun Cursor.getString(key: String): String =
	getColumnIndex(key).takeIf { it >= 0 }?.let { getString(it) }!!

fun Cursor.getStringOrNull(key: String): String? =
	getColumnIndex(key).takeIf { it >= 0 }?.let { getString(it) }