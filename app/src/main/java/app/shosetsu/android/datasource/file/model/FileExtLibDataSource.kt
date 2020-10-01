package app.shosetsu.android.datasource.file.model

import android.content.Context
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_IO
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_NOT_FOUND
import app.shosetsu.android.common.consts.LIBRARY_DIR
import app.shosetsu.android.common.consts.SOURCE_DIR
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.datasource.file.base.IFileExtLibDataSource
import okio.IOException
import java.io.File

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
class FileExtLibDataSource(
		private val context: Context,
) : IFileExtLibDataSource {

	private val ap: String by lazy {
		context.filesDir.absolutePath
	}

	private fun makeLibraryFile(fileName: String): File {
		val f = File("$ap$SOURCE_DIR$LIBRARY_DIR$fileName.lua")
		f.parentFile?.let { if (!it.exists()) it.mkdirs() }
		return f
	}

	override suspend fun writeExtLib(fileName: String, data: String): HResult<*> {
		try {
			makeLibraryFile(fileName).also {
				if (!it.exists()) it.parentFile?.mkdir()
			}.writeText(data)
		} catch (e: IOException) {
			return errorResult(ERROR_IO, e)
		}
		return successResult("")
	}

	override suspend fun loadExtLib(fileName: String): HResult<String> =
			blockingLoadLib(fileName)

	override fun blockingLoadLib(fileName: String): HResult<String> =
			makeLibraryFile(fileName).takeIf { it.exists() }?.let {
				successResult(it.readText())
			} ?: errorResult(ERROR_NOT_FOUND, "$fileName is not found in storage")


	override suspend fun deleteExtLib(fileName: String): HResult<*> {
		makeLibraryFile(fileName).takeIf { it.exists() }?.delete()
				?: errorResult(ERROR_IO, "Cannot delete nonexistent lib: $fileName")
		return successResult("")
	}
}