package app.shosetsu.android.datasource.local.file.impl

import app.shosetsu.android.common.consts.FILE_SCRIPT_DIR
import app.shosetsu.android.common.consts.FILE_SOURCE_DIR
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.common.datasource.file.base.IFileExtensionDataSource
import app.shosetsu.common.domain.model.local.ExtensionEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform
import app.shosetsu.common.enums.InternalFileDir.FILES
import app.shosetsu.common.providers.file.base.IFileSystemProvider
import app.shosetsu.common.utils.asIEntity
import app.shosetsu.common.utils.fileExtension
import app.shosetsu.lib.IExtension

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
		iFileSystemProvider.createDirectory(FILES, "$FILE_SOURCE_DIR$FILE_SCRIPT_DIR").handle(
			onError = {
				logV("Error on creation of directories $it")
			},
			onSuccess = {
				logV("Created required directories")
			}
		)
	}

	private fun makeExtensionFileURL(entity: ExtensionEntity): String =
		"$FILE_SOURCE_DIR$FILE_SCRIPT_DIR${entity.fileName}.${entity.type.fileExtension}"


	override suspend fun loadExtension(entity: ExtensionEntity): HResult<IExtension> =
		iFileSystemProvider.readFile(FILES, makeExtensionFileURL(entity)).transform {
			try {
				successResult(entity.asIEntity(it))
			} catch (e: Exception) {
				e.toHError()
			}
		}

	override suspend fun writeExtension(entity: ExtensionEntity, data: ByteArray): HResult<*> =
		iFileSystemProvider.writeFile(
			FILES,
			makeExtensionFileURL(entity),
			data
		)


	override suspend fun deleteExtension(entity: ExtensionEntity): HResult<*> =
		iFileSystemProvider.deleteFile(FILES, makeExtensionFileURL(entity))

}