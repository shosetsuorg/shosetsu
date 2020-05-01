package com.github.doomsdayrs.apps.shosetsu.common.ext

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.github.doomsdayrs.apps.shosetsu.backend.database.Tables

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

fun SQLiteDatabase.insert(
		table: Tables,
		contentValues: ContentValues,
		nullColumnHack: String? = null
) =
		insert(
				table.toString(),
				nullColumnHack,
				contentValues
		)

fun SQLiteDatabase.update(
		table: Tables,
		values: ContentValues,
		whereClause: String? = null,
		whereArgs: Array<String>? = null
) =
		update(table.toString(), values, whereClause, whereArgs)

fun SQLiteDatabase.delete(table: Tables, whereClause: String, whereArgs: Array<String>) =
		delete(table.toString(), whereClause, whereArgs)

fun SQLiteDatabase.query(
		table: Tables,
		columns: Array<String>? = null,
		selection: String? = null,
		selectionArgs: Array<String?>? = null,
		groupBy: String? = null,
		having: String? = null,
		orderBy: String? = null,
		limit: String? = null
): Cursor {
	return query(
			table.toString(),
			columns,
			selection,
			selectionArgs,
			groupBy,
			having,
			orderBy,
			limit
	)
}