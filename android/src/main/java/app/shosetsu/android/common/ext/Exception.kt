package app.shosetsu.android.common.ext

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.common.consts.ErrorKeys
import app.shosetsu.common.consts.ErrorKeys.ERROR_GENERAL
import app.shosetsu.common.consts.ErrorKeys.ERROR_IMPOSSIBLE
import app.shosetsu.common.consts.ErrorKeys.ERROR_JSON
import app.shosetsu.common.consts.ErrorKeys.ERROR_LUA_BROKEN
import app.shosetsu.common.consts.ErrorKeys.ERROR_LUA_GENERAL
import app.shosetsu.common.consts.ErrorKeys.ERROR_NETWORK
import app.shosetsu.common.consts.ErrorKeys.ERROR_NOT_FOUND
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.errorResult
import app.shosetsu.lib.exceptions.*
import kotlinx.serialization.SerializationException
import org.json.JSONException
import org.luaj.vm2.LuaError
import java.io.FileNotFoundException
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
	is JsonMissingKeyException -> {
		errorResult(ERROR_JSON, this)
	}
	is MissingExtensionLibrary -> {
		errorResult(ERROR_LUA_GENERAL, this)
	}
	is MissingOrInvalidKeysException -> {
		errorResult(ERROR_LUA_BROKEN, this)
	}
	is InvalidFilterIDException -> {
		errorResult(ERROR_LUA_BROKEN, this)
	}
	is HTTPException -> errorResult(ErrorKeys.ERROR_HTTP_ERROR, message!!, this)
	is SocketTimeoutException -> errorResult(this)
	is IOException -> {
		errorResult(ERROR_NETWORK, message ?: "Unknown Network Exception", this)
	}
	is LuaError -> {
		if (cause != null)
			(cause as Exception).toHError()
		else errorResult(ERROR_LUA_GENERAL, message ?: "Unknown Lua Error", this)
	}
	is NullPointerException -> errorResult(this)
	is SQLiteException -> errorResult(this)
	is InvalidParameterException -> errorResult(this)
	is JSONException -> errorResult(this)
	is SerializationException -> errorResult(this)
	is UnknownHostException -> errorResult(this)
	is android.content.res.Resources.NotFoundException -> errorResult(ERROR_IMPOSSIBLE, this)
	is FileNotFoundException -> errorResult(
		ERROR_NOT_FOUND,
		this.message ?: "Unknown file not found",
		this
	)
	else -> errorResult(ERROR_GENERAL, message ?: "Unknown General Error", this)
}
