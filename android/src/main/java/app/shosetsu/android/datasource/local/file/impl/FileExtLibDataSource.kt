package app.shosetsu.android.datasource.local.file.impl

import app.shosetsu.android.common.FileNotFoundException
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.common.consts.FILE_LIBRARY_DIR
import app.shosetsu.android.common.consts.FILE_SOURCE_DIR
import app.shosetsu.android.common.enums.InternalFileDir.FILES
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.datasource.local.file.base.IFileExtLibDataSource
import app.shosetsu.android.providers.file.base.IFileSystemProvider
import java.io.IOException

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
	private val iFileSystemProvider: IFileSystemProvider,
) : IFileExtLibDataSource {
	init {
		logV("Creating required directories")
		try {
			iFileSystemProvider.createDirectory(FILES, "$FILE_SOURCE_DIR$FILE_LIBRARY_DIR")
			logV("Created required directories")
		} catch (e: Exception) {
			logE("Error on creation of directories", e)
		}
	}


	private fun makeLibraryFile(fileName: String): String =
		"$FILE_SOURCE_DIR$FILE_LIBRARY_DIR$fileName.lua"

	@Throws(FilePermissionException::class, IOException::class)
	override suspend fun writeExtLib(fileName: String, data: String) {
		iFileSystemProvider.writeFile(
			FILES,
			makeLibraryFile(fileName),
			data.encodeToByteArray()
		)
	}

	@Throws(FileNotFoundException::class, FilePermissionException::class)
	override suspend fun loadExtLib(fileName: String): String =
		iFileSystemProvider.readFile(FILES, makeLibraryFile(fileName)).decodeToString()

	@Throws(FileNotFoundException::class, FilePermissionException::class)
	override suspend fun deleteExtLib(fileName: String) {
		iFileSystemProvider.deleteFile(FILES, makeLibraryFile(fileName))
	}
}