package app.shosetsu.android.datasource.remote.impl

import app.shosetsu.android.common.LuaException
import app.shosetsu.android.datasource.remote.base.IRemoteChaptersDataSource
import app.shosetsu.lib.IExtension
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

	@Throws(LuaException::class)
	override suspend fun loadChapterPassage(
		formatter: IExtension,
		chapterURL: String,
	): ByteArray = try {
		formatter.getPassage(chapterURL)
	} catch (e: LuaError) {
		if (e.cause != null)
			throw e.cause!!
		else throw LuaException(e)
	}
}