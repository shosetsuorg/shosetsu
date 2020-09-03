package com.github.doomsdayrs.apps.shosetsu.datasource.remote.model

import app.shosetsu.lib.Formatter
import app.shosetsu.lib.HTTPException
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys.ERROR_GENERAL
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys.ERROR_LUA_GENERAL
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys.ERROR_NETWORK
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.errorResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.datasource.remote.base.IRemoteChaptersDataSource
import okio.IOException
import org.luaj.vm2.LuaError

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
 */

/**
 * shosetsu
 * 12 / 05 / 2020
 */
class RemoteChaptersDataSource : IRemoteChaptersDataSource {
	override suspend fun loadChapterPassage(
			formatter: Formatter,
			chapterURL: String
	): HResult<String> =
			try {
				successResult(formatter.getPassage(chapterURL))
			} catch (e: HTTPException) {
				errorResult(ErrorKeys.ERROR_HTTP_ERROR, e.message!!)
			} catch (e: IOException) {
				errorResult(ERROR_NETWORK, e.message ?: "Unknown Network Exception", e)
			} catch (e: LuaError) {
				errorResult(ERROR_LUA_GENERAL, e.message ?: "Unknown Lua Error", e)
			} catch (e: Exception) {
				errorResult(ERROR_GENERAL, e.message ?: "Unknown General Error", e)
			}
}