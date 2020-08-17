package com.github.doomsdayrs.apps.shosetsu.datasource.remote.model

import android.util.Log
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.Novel
import app.shosetsu.lib.QUERY_INDEX
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys.ERROR_GENERAL
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys.ERROR_LUA_GENERAL
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys.ERROR_NETWORK
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys.ERROR_NO_SEARCH
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.errorResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.datasource.remote.base.IRemoteCatalogueDataSource
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
			data: Map<Int, Any>
	): HResult<List<Novel.Listing>> {
		return try {
			if (formatter.hasSearch)
				successResult(
						formatter.search(HashMap(data).apply { this[QUERY_INDEX] = query }) {
							Log.i(logID(), it)
						}.toList()
				)
			else errorResult(ERROR_NO_SEARCH, "This extension has no search functionality")
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
			data: Map<Int, Any>
	): HResult<List<Novel.Listing>> {
		return try {
			successResult(
					formatter.listings[listing].getListing(data, page).toList()
			)
		} catch (e: IOException) {
			errorResult(ERROR_NETWORK, e.message ?: "Unknown Network Exception")
		} catch (e: LuaError) {
			errorResult(ERROR_LUA_GENERAL, e.message ?: "Unknown Lua Error")
		} catch (e: Exception) {
			errorResult(ERROR_GENERAL, e.message ?: "Unknown General Error")
		}
	}
}

