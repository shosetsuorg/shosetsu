package app.shosetsu.android.providers.file.model

import android.content.Context
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.providers.file.base.IFileSystemProvider
import app.shosetsu.common.consts.ErrorKeys.ERROR_IO
import app.shosetsu.common.consts.ErrorKeys.ERROR_LACK_PERM
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.emptyResult
import app.shosetsu.common.dto.errorResult
import app.shosetsu.common.dto.successResult
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
	private val cacheDirPath by lazy { context.cacheDir.absolutePath }
	private val filesDirPath by lazy { context.filesDir.absolutePath }

	private fun InternalFileDir.path() = when (this) {
		InternalFileDir.CACHE -> "$cacheDirPath/"
		InternalFileDir.FILES -> "$filesDirPath/"
		InternalFileDir.GENERIC -> "$filesDirPath/"
	}

	override fun doesInternalFileExist(internalFileDir: InternalFileDir, path: String): HResult<*> {
		return if (!File(internalFileDir.path() + path).exists()) emptyResult() else successResult("")
	}

	override fun doesExternalFileExist(externalFileDir: ExternalFileDir, path: String): HResult<*> {
		TODO("Not yet implemented")
	}

	override fun readInternalFile(internalFileDir: InternalFileDir, path: String): HResult<String> {
		val file = File(internalFileDir.path() + path)
		logV("Reading $path in ${internalFileDir.path()} to $file")
		if (!file.exists()) return emptyResult()
		if (!file.canRead()) return errorResult(ERROR_LACK_PERM, "Cannot read file: $file")
		return successResult(file.readText())
	}

	override fun readExternalFile(externalFileDir: ExternalFileDir, path: String): HResult<String> {
		TODO("Not yet implemented")
	}

	override fun deleteInternalFile(internalFileDir: InternalFileDir, path: String): HResult<*> {
		val file = File(internalFileDir.path() + path)
		logV("Deleting $path in ${internalFileDir.path()} to $file")

		if (!file.canWrite() && file.exists()) return errorResult(ERROR_LACK_PERM, "Cannot delete file: $file")
		return successResult(file.delete())
	}

	override fun deleteExternalFile(externalFileDir: ExternalFileDir, path: String): HResult<*> {
		TODO("Not yet implemented")
	}

	override fun writeInternalFile(internalFileDir: InternalFileDir, path: String, content: String): HResult<*> {
		val file = File(internalFileDir.path() + path)

		logV("Writing $path in ${internalFileDir.path()} to $file")
		if (!file.canWrite() && file.exists())
			return errorResult(ERROR_LACK_PERM, "Cannot write file: $file")

		try {
			if (!file.exists()) file.createNewFile()
		} catch (e: IOException) {
			logE("IOException on attempt to create new file: $file", e)
			return errorResult(ERROR_IO, e)
		}

		return successResult(file.writeText(content))
	}

	override fun writeExternalFile(externalFileDir: ExternalFileDir, path: String, content: String): HResult<*> {
		TODO("Not yet implemented")
	}

	override fun createInternalDirectory(internalFileDir: InternalFileDir, path: String): HResult<*> {
		val file = File(internalFileDir.path() + path)
		logV("Creating $path in ${internalFileDir.path()}")
		// if (!file.canWrite()) return errorResult(ERROR_LACK_PERM, "Cannot write file: $file")
		return successResult(file.mkdirs())
	}

	override fun createExternalDirectory(externalFileDir: ExternalFileDir, path: String): HResult<*> {
		TODO("Not yet implemented")
	}
}