package app.shosetsu.android.providers.file.impl

import android.content.Context
import android.os.Environment.DIRECTORY_DOCUMENTS
import android.os.Environment.DIRECTORY_DOWNLOADS
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.common.consts.ErrorKeys.ERROR_IO
import app.shosetsu.common.consts.ErrorKeys.ERROR_LACK_PERM
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.emptyResult
import app.shosetsu.common.dto.errorResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.enums.ExternalFileDir
import app.shosetsu.common.enums.InternalFileDir
import app.shosetsu.common.providers.file.base.IFileSystemProvider
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
	): HResult<List<String>> =
		successResult(File(internalFileDir.path() + path).list()?.toList().orEmpty())

	override fun listFiles(
		externalFileDir: ExternalFileDir,
		path: String
	): HResult<List<String>> =
		successResult(File(externalFileDir.path() + path).list()?.toList().orEmpty())

	override fun doesFileExist(
		internalFileDir: InternalFileDir,
		path: String
	): HResult<Boolean> = if (!File(internalFileDir.path() + path).exists())
		successResult(false) else successResult(true)

	override fun doesFileExist(
		externalFileDir: ExternalFileDir,
		path: String
	): HResult<Boolean> = if (!File(externalFileDir.path() + path).exists())
		successResult(false) else successResult(true)

	override fun readFile(internalFileDir: InternalFileDir, path: String): HResult<ByteArray> {
		val file = File(internalFileDir.path() + path)

		//logV("Reading $path in ${internalFileDir.path()} to $file")

		if (!file.exists()) return emptyResult()
		if (!file.canRead()) return errorResult(ERROR_LACK_PERM, "Cannot read file: $file")
		return successResult(file.readBytes())
	}

	override fun readFile(externalFileDir: ExternalFileDir, path: String): HResult<ByteArray> {
		val file = File(externalFileDir.path() + path)

		//logV("Reading $path in ${externalFileDir.path()} to $file")

		if (!file.exists()) return emptyResult()
		if (!file.canRead()) return errorResult(ERROR_LACK_PERM, "Cannot read file: $file")
		return successResult(file.readBytes())
	}

	override fun readFile(path: String): HResult<ByteArray> {
		val file = File(path)

		//	logV("Reading $path to $file")

		if (!file.exists()) return emptyResult()
		if (!file.canRead()) return errorResult(ERROR_LACK_PERM, "Cannot read file: $file")
		return successResult(file.readBytes())
	}

	override fun deleteFile(internalFileDir: InternalFileDir, path: String): HResult<*> {
		val file = File(internalFileDir.path() + path)
//		logV("Deleting $path in ${internalFileDir.path()} to $file")

		if (!file.canWrite() && file.exists()) return errorResult(
			ERROR_LACK_PERM,
			"Cannot delete file: $file"
		)
		return successResult(file.delete())
	}

	override fun deleteFile(externalFileDir: ExternalFileDir, path: String): HResult<*> {
		val file = File(externalFileDir.path() + path)
		//	logV("Deleting $path in ${externalFileDir.path()} to $file")

		if (!file.canWrite() && file.exists()) return errorResult(
			ERROR_LACK_PERM,
			"Cannot delete file: $file"
		)
		return successResult(file.delete())
	}

	override fun writeFile(
		internalFileDir: InternalFileDir,
		path: String,
		content: ByteArray
	): HResult<*> {
		val file = File(internalFileDir.path() + path)

		//	logV("Writing $path in ${internalFileDir.path()} to $file")
		if (!file.canWrite() && file.exists())
			return errorResult(ERROR_LACK_PERM, "Cannot write file: $file")

		try {
			if (!file.exists()) file.createNewFile()
		} catch (e: IOException) {
			logE("IOException on attempt to create new file: $file", e)
			return errorResult(ERROR_IO, e)
		}
		return successResult(file.writeBytes(content))
	}

	override fun writeFile(
		externalFileDir: ExternalFileDir,
		path: String,
		content: ByteArray
	): HResult<*> {
		val file = File(externalFileDir.path() + path)

		//	logV("Writing $path in ${externalFileDir.path()} to $file")

		if (!file.canWrite() && file.exists())
			return errorResult(ERROR_LACK_PERM, "Cannot write file: $file")

		try {
			if (!file.exists()) file.createNewFile()
		} catch (e: IOException) {

			logE("IOException on attempt to create new file: $file", e)

			return errorResult(ERROR_IO, e)
		}

		return successResult(file.writeBytes(content))
	}

	override fun createDirectory(
		internalFileDir: InternalFileDir,
		path: String
	): HResult<*> {
		val file = File(internalFileDir.path() + path)

//		logV("Creating $path in ${internalFileDir.path()}")

		// if (!file.canWrite()) return errorResult(ERROR_LACK_PERM, "Cannot write file: $file")
		return successResult(file.mkdirs())
	}

	override fun createDirectory(
		externalFileDir: ExternalFileDir,
		path: String
	): HResult<*> {
		val file = File(externalFileDir.path() + path)

		//	logV("Creating $path in ${externalFileDir.path()}")

		// if (!file.canWrite()) return errorResult(ERROR_LACK_PERM, "Cannot write file: $file")
		return successResult(file.mkdirs())
	}

	override fun retrievePath(
		internalFileDir: InternalFileDir,
		path: String
	): HResult<String> {
		val file = File(internalFileDir.path() + path)

		if (!file.exists()) return emptyResult()

		return successResult(file.absolutePath)
	}

	override fun retrievePath(
		externalFileDir: ExternalFileDir,
		path: String
	): HResult<String> {
		val file = File(externalFileDir.path() + path)

		if (!file.exists()) return emptyResult()

		return successResult(file.absolutePath)
	}

	override fun createFile(internalFileDir: InternalFileDir, path: String): HResult<*> {
		val file = File(internalFileDir.path() + path)
		return try {
			val t = file.createNewFile()
			if (t) successResult(t) else emptyResult()
		} catch (e: IOException) {
			e.toHError()
		}
	}

	override fun createFile(externalFileDir: ExternalFileDir, path: String): HResult<*> {
		val file = File(externalFileDir.path() + path)
		return try {
			val t = file.createNewFile()
			if (t) successResult(t) else emptyResult()
		} catch (e: IOException) {
			e.toHError()
		}
	}
}