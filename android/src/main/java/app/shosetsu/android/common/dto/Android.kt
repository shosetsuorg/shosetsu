package app.shosetsu.android.common.dto

import android.database.sqlite.SQLiteException
import app.shosetsu.common.consts.ErrorKeys
import app.shosetsu.common.dto.HResult
import org.json.JSONException

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


/** An exception occurred in SQL*/
fun errorResult(e: SQLiteException): HResult.Error =
		HResult.Error(ErrorKeys.ERROR_HTTP_SQL, e.message ?: "UnknownSQLException", e)

fun errorResult(e: JSONException): HResult.Error =
		HResult.Error(ErrorKeys.ERROR_JSON, e.message ?: "UnknownJSONException", e)
