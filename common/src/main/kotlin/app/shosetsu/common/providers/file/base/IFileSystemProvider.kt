package app.shosetsu.common.providers.file.base

import app.shosetsu.common.dto.HResult
import app.shosetsu.common.enums.ExternalFileDir
import app.shosetsu.common.enums.InternalFileDir

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * shosetsu
 * 23 / 10 / 2020
 *
 * This class interfaces the file system of the target
 *
 *
 */
interface IFileSystemProvider {

	fun doesInternalFileExist(internalFileDir: InternalFileDir, path: String): HResult<*>
	fun doesExternalFileExist(externalFileDir: ExternalFileDir, path: String): HResult<*>


	/**
	 * Loads a file from the internal file directory
	 * This means loading data from the APP specific directory
	 *
	 * @return file content
	 */
	fun readInternalFile(internalFileDir: InternalFileDir, path: String): HResult<String>

	/**
	 * Loads a file from the user directories (Pictures, Downloads, Etc)
	 *
	 * @return file content
	 */
	fun readExternalFile(externalFileDir: ExternalFileDir, path: String): HResult<String>


	fun deleteInternalFile(internalFileDir: InternalFileDir, path: String): HResult<*>

	fun deleteExternalFile(externalFileDir: ExternalFileDir, path: String): HResult<*>


	/**
	 * Writes a file to the internal file directory
	 */
	fun writeInternalFile(
		internalFileDir: InternalFileDir,
		path: String,
		content: String
	): HResult<*>

	/**
	 * Writes a file to the external file directory
	 */
	fun writeExternalFile(
		externalFileDir: ExternalFileDir,
		path: String,
		content: String
	): HResult<*>


	fun createInternalFile(internalFileDir: InternalFileDir, path: String): HResult<*>
	fun createExternalFile(externalFileDir: ExternalFileDir, path: String): HResult<*>

	/**
	 * Creates an internal directory, will avoid if not present
	 */
	fun createInternalDirectory(internalFileDir: InternalFileDir, path: String): HResult<*>

	/**
	 * Creates an external directory, will avoid if not present
	 */
	fun createExternalDirectory(externalFileDir: ExternalFileDir, path: String): HResult<*>

	/**
	 * Get filesystem path to a file
	 */
	fun retrieveInternalPath(internalFileDir: InternalFileDir, path: String): HResult<String>

	/**
	 * Get filesystem path to a file
	 */
	fun retrieveExternalPath(externalFileDir: ExternalFileDir, path: String): HResult<String>

}