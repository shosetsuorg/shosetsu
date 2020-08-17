package com.github.doomsdayrs.apps.shosetsu.datasource.file.model

import app.shosetsu.lib.Formatter
import app.shosetsu.lib.LuaFormatter
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.errorResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.utils.base.IFormatterUtils
import com.github.doomsdayrs.apps.shosetsu.datasource.file.base.IFileExtensionDataSource
import okio.IOException
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
		private val formatterUtils: IFormatterUtils
) : IFileExtensionDataSource {
	override suspend fun loadFormatter(fileName: String): HResult<Formatter> = try {
		successResult(LuaFormatter(formatterUtils.makeFormatterFile(fileName)))
	} catch (e: LuaError) {
		errorResult(ErrorKeys.ERROR_LUA_GENERAL, e.message ?: "Unknown Lua Error")
	} catch (e: FileNotFoundException) {
		errorResult(ErrorKeys.ERROR_NOT_FOUND, e.message ?: "Unknown file not found")
	}

	override suspend fun writeFormatter(fileName: String, data: String) {
		try {
			formatterUtils.makeFormatterFile(fileName).also {
				if (!it.exists()) {
					it.parentFile.mkdir()
					it.createNewFile()
				}
			}.writeText(data)
		} catch (e: IOException) {
			TODO("Implement Logging")
		}
	}

	override suspend fun deleteFormatter(fileName: String) {
		formatterUtils.makeFormatterFile(fileName).takeIf { it.exists() }?.delete()
	}
}