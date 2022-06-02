package app.shosetsu.android.datasource.local.file.impl

import app.shosetsu.android.common.FileNotFoundException
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.common.consts.FILE_SCRIPT_DIR
import app.shosetsu.android.common.consts.FILE_SOURCE_DIR
import app.shosetsu.android.common.enums.InternalFileDir.FILES
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.common.utils.asIEntity
import app.shosetsu.android.common.utils.fileExtension
import app.shosetsu.android.datasource.local.file.base.IFileExtensionDataSource
import app.shosetsu.android.domain.model.local.GenericExtensionEntity
import app.shosetsu.android.providers.file.base.IFileSystemProvider
import app.shosetsu.lib.IExtension
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
class FileExtensionDataSource(
	private val iFileSystemProvider: IFileSystemProvider
) : IFileExtensionDataSource {
	init {
		logV("Creating required directories")
		try {
			iFileSystemProvider.createDirectory(FILES, MERGED_DIR)
			logV("Created required directories")
		} catch (e: Exception) {
			logV("Error on creation of directories `$MERGED_DIR`", e)
		}
	}

	private fun makeExtensionFileURL(entity: GenericExtensionEntity): String =
		"$MERGED_DIR${entity.fileName}.${entity.type.fileExtension}"


	@Throws(FileNotFoundException::class, FilePermissionException::class)
	override suspend fun loadExtension(entity: GenericExtensionEntity): IExtension =
		entity.asIEntity(iFileSystemProvider.readFile(FILES, makeExtensionFileURL(entity)))

	@Throws(FilePermissionException::class, IOException::class)
	override suspend fun writeExtension(entity: GenericExtensionEntity, data: ByteArray) =
		iFileSystemProvider.writeFile(
			FILES,
			makeExtensionFileURL(entity),
			data
		)


	@Throws(FilePermissionException::class)
	override suspend fun deleteExtension(entity: GenericExtensionEntity) {
		iFileSystemProvider.deleteFile(FILES, makeExtensionFileURL(entity))
	}

	companion object {
		const val MERGED_DIR = "$FILE_SOURCE_DIR$FILE_SCRIPT_DIR"
	}
}