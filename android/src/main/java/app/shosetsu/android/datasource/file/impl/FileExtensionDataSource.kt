package app.shosetsu.android.datasource.file.impl

import app.shosetsu.android.common.consts.SCRIPT_DIR
import app.shosetsu.android.common.consts.SOURCE_DIR
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.providers.file.base.IFileSystemProvider
import app.shosetsu.common.consts.ErrorKeys.ERROR_LUA_GENERAL
import app.shosetsu.common.consts.ErrorKeys.ERROR_NOT_FOUND
import app.shosetsu.common.enums.InternalFileDir.FILES
import app.shosetsu.common.datasource.file.base.IFileExtensionDataSource
import app.shosetsu.common.dto.*
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.lua.LuaExtension
import org.luaj.vm2.LuaError
import java.io.FileNotFoundException

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
class FileExtensionDataSource(
		private val iFileSystemProvider: IFileSystemProvider
) : IFileExtensionDataSource {
	init {
		logV("Creating required directories")
		iFileSystemProvider.createInternalDirectory(FILES, "$SOURCE_DIR$SCRIPT_DIR").handle(
				onError = {
					logV("Error on creation of directories $it")
				},
				onSuccess = {
					logV("Created required directories")
				}
		)
	}

	private fun makeFormatterFile(fileName: String): String =
			"$SOURCE_DIR$SCRIPT_DIR$fileName.lua"


	override suspend fun loadFormatter(fileName: String): HResult<IExtension> = try {
		iFileSystemProvider.readInternalFile(FILES, makeFormatterFile(fileName)).withSuccess {
			try {
				successResult(LuaExtension(it, fileName))
			} catch (e: Exception) {
				e.toHError()
			}
		}
	} catch (e: LuaError) {
		errorResult(ERROR_LUA_GENERAL, e.message
				?: "Unknown Lua Error", e)
	} catch (e: FileNotFoundException) {
		errorResult(ERROR_NOT_FOUND, e.message
				?: "Unknown file not found", e)
	}

	override suspend fun writeFormatter(fileName: String, data: String): HResult<*> =
			iFileSystemProvider.writeInternalFile(FILES, makeFormatterFile(fileName), data)


	override suspend fun deleteFormatter(fileName: String): HResult<*> =
			iFileSystemProvider.deleteInternalFile(FILES, makeFormatterFile(fileName))

}