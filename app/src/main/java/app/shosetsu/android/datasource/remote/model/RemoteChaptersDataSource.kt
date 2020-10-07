package app.shosetsu.android.datasource.remote.model

import app.shosetsu.android.common.consts.ErrorKeys
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_GENERAL
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_LUA_GENERAL
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_NETWORK
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.datasource.remote.base.IRemoteChaptersDataSource
import app.shosetsu.lib.HTTPException
import app.shosetsu.lib.IExtension
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
			formatter: IExtension,
			chapterURL: String,
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