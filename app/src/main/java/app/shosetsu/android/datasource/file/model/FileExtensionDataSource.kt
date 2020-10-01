package app.shosetsu.android.datasource.file.model

import android.content.Context
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_LUA_GENERAL
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_NOT_FOUND
import app.shosetsu.android.common.consts.SCRIPT_DIR
import app.shosetsu.android.common.consts.SOURCE_DIR
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.datasource.file.base.IFileExtensionDataSource
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.LuaFormatter
import org.luaj.vm2.LuaError
import java.io.File
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
		private val context: Context,
) : IFileExtensionDataSource {
	private val ap: String by lazy {
		context.filesDir.absolutePath
	}

	private fun makeFormatterFile(fileName: String): File {
		val f = File("$ap$SOURCE_DIR$SCRIPT_DIR$fileName.lua")
		f.parentFile?.let { if (!it.exists()) it.mkdirs() }
		return f
	}

	override suspend fun loadFormatter(fileName: String): HResult<Formatter> = try {
		successResult(LuaFormatter(makeFormatterFile(fileName)))
	} catch (e: LuaError) {
		errorResult(ERROR_LUA_GENERAL, e.message ?: "Unknown Lua Error", e)
	} catch (e: FileNotFoundException) {
		errorResult(ERROR_NOT_FOUND, e.message ?: "Unknown file not found", e)
	}

	override suspend fun writeFormatter(fileName: String, data: String): HResult<*> {
		makeFormatterFile(fileName).also {
			if (!it.exists()) {
				it.parentFile?.mkdir()
			}
		}.writeText(data)
		return successResult("")
	}

	override suspend fun deleteFormatter(fileName: String): HResult<*> {
		makeFormatterFile(fileName).takeIf { it.exists() }?.delete()
				?: errorResult(ERROR_NOT_FOUND, "Cannot delete unknown file: $fileName")
		return successResult("")
	}
}