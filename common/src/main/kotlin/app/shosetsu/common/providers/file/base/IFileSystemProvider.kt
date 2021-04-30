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

	fun listFiles(internalFileDir: InternalFileDir, path: String): HResult<List<String>>
	fun listFiles(externalFileDir: ExternalFileDir, path: String): HResult<List<String>>

	fun doesFileExist(internalFileDir: InternalFileDir, path: String): HResult<Boolean>

	fun doesFileExist(externalFileDir: ExternalFileDir, path: String): HResult<Boolean>


	/**
	 * Loads a file from the internal file directory
	 * This means loading data from the APP specific directory
	 *
	 * @return file content
	 */
	fun readFile(internalFileDir: InternalFileDir, path: String): HResult<ByteArray>

	/**
	 * Loads a file from the user directories (Pictures, Downloads, Etc)
	 *
	 * @return file content
	 */
	fun readFile(externalFileDir: ExternalFileDir, path: String): HResult<ByteArray>


	/**
	 * Reads a file directly
	 *
	 * @param path Absolute path to a file
	 * @return file content
	 */
	fun readFile(path: String): HResult<ByteArray>

	fun deleteFile(internalFileDir: InternalFileDir, path: String): HResult<*>

	fun deleteFile(externalFileDir: ExternalFileDir, path: String): HResult<*>


	/**
	 * Writes a file to the internal file directory
	 */
	fun writeFile(
		internalFileDir: InternalFileDir,
		path: String,
		content: ByteArray
	): HResult<*>

	/**
	 * Writes a file to the external file directory
	 */
	fun writeFile(
		externalFileDir: ExternalFileDir,
		path: String,
		content: ByteArray
	): HResult<*>


	fun createFile(internalFileDir: InternalFileDir, path: String): HResult<*>
	fun createFile(externalFileDir: ExternalFileDir, path: String): HResult<*>

	/**
	 * Creates an internal directory, will avoid if not present
	 */
	fun createDirectory(internalFileDir: InternalFileDir, path: String): HResult<*>

	/**
	 * Creates an external directory, will avoid if not present
	 */
	fun createDirectory(externalFileDir: ExternalFileDir, path: String): HResult<*>

	/**
	 * Get filesystem path to a file
	 */
	fun retrievePath(internalFileDir: InternalFileDir, path: String): HResult<String>

	/**
	 * Get filesystem path to a file
	 */
	fun retrievePath(externalFileDir: ExternalFileDir, path: String): HResult<String>

}