package app.shosetsu.android.providers.file.base

import app.shosetsu.android.common.FileNotFoundException
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.common.enums.ExternalFileDir
import app.shosetsu.android.common.enums.InternalFileDir
import java.io.IOException
import java.io.InputStream

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

	fun listFiles(internalFileDir: InternalFileDir, path: String): List<String>
	fun listFiles(externalFileDir: ExternalFileDir, path: String): List<String>

	fun doesFileExist(internalFileDir: InternalFileDir, path: String): Boolean

	fun doesFileExist(externalFileDir: ExternalFileDir, path: String): Boolean


	/**
	 * Loads a file from the internal file directory
	 * This means loading data from the APP specific directory
	 *
	 * @return file content
	 */
	@Throws(FileNotFoundException::class, FilePermissionException::class)
	fun readFile(internalFileDir: InternalFileDir, path: String): ByteArray

	/**
	 * Loads a file from the user directories (Pictures, Downloads, Etc)
	 *
	 * @return file content
	 */
	@Throws(FilePermissionException::class, FileNotFoundException::class)
	fun readFile(externalFileDir: ExternalFileDir, path: String): ByteArray


	/**
	 * Reads a file directly
	 *
	 * @param path Absolute path to a file
	 * @return file content
	 */
	@Throws(FileNotFoundException::class, FilePermissionException::class)
	fun readFile(path: String): ByteArray

	@Throws(FilePermissionException::class)
	fun deleteFile(internalFileDir: InternalFileDir, path: String): Boolean

	@Throws(FilePermissionException::class)
	fun deleteFile(externalFileDir: ExternalFileDir, path: String): Boolean


	/**
	 * Writes a file to the internal file directory
	 */
	@Throws(FilePermissionException::class, IOException::class)
	fun writeFile(
		internalFileDir: InternalFileDir,
		path: String,
		content: ByteArray
	)

	/**
	 * Writes a file to the internal file directory
	 */
	@Throws(FilePermissionException::class, IOException::class)
	fun writeFile(
		internalFileDir: InternalFileDir,
		path: String,
		content: InputStream
	)

	/**
	 * Writes a file to the external file directory
	 */
	@Throws(FilePermissionException::class, IOException::class)
	fun writeFile(
		externalFileDir: ExternalFileDir,
		path: String,
		content: ByteArray
	)


	@Throws(IOException::class)
	fun createFile(internalFileDir: InternalFileDir, path: String): Boolean

	@Throws(IOException::class)
	fun createFile(externalFileDir: ExternalFileDir, path: String): Boolean

	/**
	 * Creates an internal directory, will avoid if not present
	 */
	fun createDirectory(internalFileDir: InternalFileDir, path: String): Boolean

	/**
	 * Creates an external directory, will avoid if not present
	 */
	fun createDirectory(externalFileDir: ExternalFileDir, path: String): Boolean

	/**
	 * Get filesystem path to a file
	 */
	@Throws(FileNotFoundException::class)
	fun retrievePath(internalFileDir: InternalFileDir, path: String): String

	/**
	 * Get filesystem path to a file
	 */
	@Throws(FileNotFoundException::class)
	fun retrievePath(externalFileDir: ExternalFileDir, path: String): String

}