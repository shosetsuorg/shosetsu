package app.shosetsu.android.common.ext

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.common.com.consts.ErrorKeys
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.com.dto.errorResult
import app.shosetsu.lib.exceptions.HTTPException
import org.json.JSONException
import org.luaj.vm2.LuaError
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.security.InvalidParameterException

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
 * Converts Exceptions to their respective error result
 */
fun Exception.toHError(): HResult.Error = when (this) {
	is HTTPException -> {
		logD("HTTP Exception")
		errorResult(ErrorKeys.ERROR_HTTP_ERROR, message!!, this)
	}
	is IOException -> {
		logD("Network exception")
		errorResult(ErrorKeys.ERROR_NETWORK, message ?: "Unknown Network Exception", this)
	}
	is LuaError -> {
		if (cause != null && cause is HTTPException) {
			logD("HTTP exception")
			errorResult(ErrorKeys.ERROR_HTTP_ERROR, cause!!.message!!)
		} else errorResult(ErrorKeys.ERROR_LUA_GENERAL, message ?: "Unknown Lua Error", this)
	}
	is NullPointerException -> errorResult(this)
	is SQLiteException -> errorResult(this)
	is InvalidParameterException -> errorResult(this)
	is JSONException -> errorResult(this)
	is SocketTimeoutException -> errorResult(this)
	is UnknownHostException -> errorResult(this)
	else -> errorResult(ErrorKeys.ERROR_GENERAL, message ?: "Unknown General Error", this)
}
