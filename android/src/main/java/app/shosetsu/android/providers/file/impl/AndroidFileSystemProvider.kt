package app.shosetsu.android.providers.file.impl

import android.content.Context
import android.os.Environment.DIRECTORY_DOCUMENTS
import android.os.Environment.DIRECTORY_DOWNLOADS
import app.shosetsu.android.common.FileNotFoundException
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.common.FilePermissionException.PermissionType
import app.shosetsu.android.providers.file.base.IFileSystemProvider
import app.shosetsu.common.enums.ExternalFileDir
import app.shosetsu.common.enums.InternalFileDir
import java.io.File
import java.io.IOException

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
 */
class AndroidFileSystemProvider(
	private val context: Context
) : IFileSystemProvider {
	private val internalCacheDirPath by lazy { context.cacheDir.absolutePath }
	private val internalFilesDirPath by lazy { context.filesDir.absolutePath }

	private val internalGenericDirPath by lazy {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			context.dataDir.absolutePath
		} else {
			context.filesDir.absolutePath
		}
	}

	private val externalDirPath by lazy { context.getExternalFilesDir(null) }
	private val externalDownloadDirPath by lazy { context.getExternalFilesDir(DIRECTORY_DOWNLOADS) }
	private val externalDocumentDirPath by lazy { context.getExternalFilesDir(DIRECTORY_DOCUMENTS) }


	private fun InternalFileDir.path() = when (this) {
		InternalFileDir.CACHE -> "$internalCacheDirPath/"
		InternalFileDir.FILES -> "$internalFilesDirPath/"
		InternalFileDir.GENERIC -> "$internalGenericDirPath/"
	}

	private fun ExternalFileDir.path() = when (this) {
		ExternalFileDir.APP -> "$externalDirPath/"
		ExternalFileDir.DOWNLOADS -> "$externalDownloadDirPath/"
		ExternalFileDir.DOCUMENTS -> "$externalDocumentDirPath/"
	}

	override fun listFiles(
		internalFileDir: InternalFileDir,
		path: String
	): List<String> =
		File(internalFileDir.path() + path).list()?.toList().orEmpty()

	override fun listFiles(
		externalFileDir: ExternalFileDir,
		path: String
	): List<String> =
		File(externalFileDir.path() + path).list()?.toList().orEmpty()

	override fun doesFileExist(
		internalFileDir: InternalFileDir,
		path: String
	): Boolean = File(internalFileDir.path() + path).exists()

	override fun doesFileExist(
		externalFileDir: ExternalFileDir,
		path: String
	): Boolean = if (!File(externalFileDir.path() + path).exists())
		(false) else (true)

	@Throws(FileNotFoundException::class, FilePermissionException::class)
	override fun readFile(internalFileDir: InternalFileDir, path: String): ByteArray {
		val file = File(internalFileDir.path() + path)

		//logV("Reading $path in ${internalFileDir.path()} to $file")

		if (!file.exists()) throw FileNotFoundException("$path does not exist")
		if (!file.canRead()) throw FilePermissionException(file.path, PermissionType.READ)

		return file.readBytes()
	}

	@Throws(FileNotFoundException::class, FilePermissionException::class)
	override fun readFile(externalFileDir: ExternalFileDir, path: String): ByteArray {
		val file = File(externalFileDir.path() + path)

		//logV("Reading $path in ${externalFileDir.path()} to $file")

		if (!file.exists()) throw FileNotFoundException("$path does not exist")
		if (!file.canRead()) throw FilePermissionException(file.path, PermissionType.READ)
		return file.readBytes()
	}

	@Throws(FileNotFoundException::class, FilePermissionException::class)
	override fun readFile(path: String): ByteArray {
		val file = File(path)

		//	logV("Reading $path to $file")

		if (!file.exists()) throw FileNotFoundException("File not found: `$path`")
		if (!file.canRead()) throw FilePermissionException(path, PermissionType.READ)
		return file.readBytes()
	}

	@Throws(FilePermissionException::class)
	override fun deleteFile(internalFileDir: InternalFileDir, path: String): Boolean {
		val file = File(internalFileDir.path() + path)
//		logV("Deleting $path in ${internalFileDir.path()} to $file")

		if (!file.exists()) return false

		if (!file.canWrite())
			throw FilePermissionException(file.path, PermissionType.WRITE)

		return file.delete()
	}

	@Throws(FilePermissionException::class)
	override fun deleteFile(externalFileDir: ExternalFileDir, path: String): Boolean {
		val file = File(externalFileDir.path() + path)
		//	logV("Deleting $path in ${externalFileDir.path()} to $file")

		if (!file.exists()) return false

		if (!file.canWrite())
			throw FilePermissionException(file.path, PermissionType.WRITE)

		return file.delete()
	}

	@Throws(FilePermissionException::class, IOException::class)
	override fun writeFile(
		internalFileDir: InternalFileDir,
		path: String,
		content: ByteArray
	) {
		val file = File(internalFileDir.path() + path)

		//	logV("Writing $path in ${internalFileDir.path()} to $file")

		if (!file.exists()) file.createNewFile()

		if (!file.canWrite())
			throw FilePermissionException(file.path, PermissionType.WRITE)

		return file.writeBytes(content)
	}

	@Throws(FilePermissionException::class, IOException::class)
	override fun writeFile(
		externalFileDir: ExternalFileDir,
		path: String,
		content: ByteArray
	) {
		val file = File(externalFileDir.path() + path)

		//	logV("Writing $path in ${externalFileDir.path()} to $file")

		if (!file.exists()) file.createNewFile()

		if (!file.canWrite())
			throw FilePermissionException(file.path, PermissionType.WRITE)

		return file.writeBytes(content)
	}

	override fun createDirectory(
		internalFileDir: InternalFileDir,
		path: String
	): Boolean {
		val file = File(internalFileDir.path() + path)

//		logV("Creating $path in ${internalFileDir.path()}")

		// if (!file.canWrite()) return errorResult(ERROR_LACK_PERM, "Cannot write file: $file")
		return file.mkdirs()
	}

	override fun createDirectory(
		externalFileDir: ExternalFileDir,
		path: String
	): Boolean {
		val file = File(externalFileDir.path() + path)

		//	logV("Creating $path in ${externalFileDir.path()}")

		// if (!file.canWrite()) return errorResult(ERROR_LACK_PERM, "Cannot write file: $file")
		return file.mkdirs()
	}

	@Throws(FileNotFoundException::class)
	override fun retrievePath(
		internalFileDir: InternalFileDir,
		path: String
	): String {
		val file = File(internalFileDir.path() + path)

		if (!file.exists()) throw FileNotFoundException("$path does not exist")

		return file.absolutePath
	}

	@Throws(FileNotFoundException::class)
	override fun retrievePath(
		externalFileDir: ExternalFileDir,
		path: String
	): String {
		val file = File(externalFileDir.path() + path)

		if (!file.exists()) throw FileNotFoundException("$path does not exist")

		return file.absolutePath
	}

	@Throws(IOException::class)
	override fun createFile(internalFileDir: InternalFileDir, path: String): Boolean {
		val file = File(internalFileDir.path() + path)
		return file.createNewFile()
	}

	@Throws(IOException::class)
	override fun createFile(externalFileDir: ExternalFileDir, path: String): Boolean {
		val file = File(externalFileDir.path() + path)
		return file.createNewFile()
	}
}