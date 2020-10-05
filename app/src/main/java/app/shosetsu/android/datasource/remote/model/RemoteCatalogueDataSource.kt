package app.shosetsu.android.datasource.remote.model

import android.util.Log
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_GENERAL
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_HTTP_ERROR
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_LUA_GENERAL
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_NETWORK
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_NO_SEARCH
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.emptyResult
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.datasource.remote.base.IRemoteCatalogueDataSource
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.HTTPException
import app.shosetsu.lib.Novel
import app.shosetsu.lib.QUERY_INDEX
import okio.IOException
import org.luaj.vm2.LuaError

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
 * 10 / May / 2020
 */
class RemoteCatalogueDataSource : IRemoteCatalogueDataSource {
	override suspend fun search(
			formatter: Formatter,
			query: String,
			page: Int,
			data: Map<Int, Any>,
	): HResult<List<Novel.Listing>> {
		return try {
			if (formatter.hasSearch) {
				val l = formatter.search(HashMap(data).apply {
					this[QUERY_INDEX] = query
				}) {
					Log.i(logID(), it)
				}.toList()

				if (l.isEmpty()) emptyResult() else successResult(l)
			} else errorResult(ERROR_NO_SEARCH, "This extension has no search functionality")
		} catch (e: IOException) {
			errorResult(ERROR_NETWORK, e.message ?: "Unknown Network Exception")
		} catch (e: LuaError) {
			errorResult(ERROR_LUA_GENERAL, e.message ?: "Unknown Lua Error")
		} catch (e: Exception) {
			errorResult(ERROR_GENERAL, e.message ?: "Unknown General Error")
		}
	}

	override suspend fun loadListing(
			formatter: Formatter,
			listing: Int,
			page: Int,
			data: Map<Int, Any>,
	): HResult<List<Novel.Listing>> {
		return try {
			logV("Data: $data")
			val l = formatter.listings[listing]
			if (!l.isIncrementing && page > 0) emptyResult()
			else successResult(l.getListing(data, page).toList())
		} catch (e: HTTPException) {
			Log.d(logID(), "HTTP Exception")
			errorResult(ERROR_HTTP_ERROR, e.message!!, e)
		} catch (e: IOException) {
			Log.d(logID(), "Network exception")
			errorResult(ERROR_NETWORK, e.message ?: "Unknown Network Exception", e)
		} catch (e: LuaError) {
			if (e.cause != null && e.cause is HTTPException) {
				Log.d(logID(), "HTTP exception")
				errorResult(ERROR_HTTP_ERROR, e.cause!!.message!!)
			} else
				errorResult(ERROR_LUA_GENERAL, e.message ?: "Unknown Lua Error", e)
		} catch (e: Exception) {
			Log.d(logID(), "General exception")
			errorResult(ERROR_GENERAL, e.message ?: "Unknown General Error", e)
		}
	}
}

