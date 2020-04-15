package com.github.doomsdayrs.apps.shosetsu.variables.ext

import android.content.ContentValues
import com.github.doomsdayrs.apps.shosetsu.backend.database.Columns

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
 * 15 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */

fun ContentValues.put(columns: Columns, value: String) = put(columns.toString(), value)
fun ContentValues.put(columns: Columns, value: Long) = put(columns.toString(), value)
fun ContentValues.put(columns: Columns, value: Int) = put(columns.toString(), value)
fun ContentValues.put(columns: Columns, value: Double) = put(columns.toString(), value)