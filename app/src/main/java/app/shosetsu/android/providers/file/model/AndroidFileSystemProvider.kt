package app.shosetsu.android.providers.file.model

import android.content.Context
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_LACK_PERM
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.emptyResult
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.common.enums.ExternalFileDir
import app.shosetsu.android.common.enums.InternalFileDir
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.providers.file.base.IFileSystemProvider
import java.io.File

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
    private fun InternalFileDir.path() = when (this) {
        InternalFileDir.CACHE -> context.cacheDir.absolutePath + "/"
        InternalFileDir.FILES -> context.filesDir.absolutePath + "/"
        InternalFileDir.GENERIC -> context.filesDir.absolutePath + "/"
    }

    override fun readInternalFile(internalFileDir: InternalFileDir, path: String): HResult<String> {
        val file = File(internalFileDir.path() + path)
        logV("Reading $path in ${internalFileDir.path()} to $file")

        if (!file.exists()) {
            logE("File does not exist")
            return emptyResult()
        }
        if (!file.canRead()) return errorResult(ERROR_LACK_PERM, "Cannot read file: $file")
        return successResult(file.readText())
    }

    override fun readExternalFile(externalFileDir: ExternalFileDir, path: String): HResult<String> {
        TODO("Not yet implemented")
    }

    override fun deleteInternalFile(internalFileDir: InternalFileDir, path: String): HResult<*> {
        val file = File(internalFileDir.path() + path)
        logV("Deleting $path in ${internalFileDir.path()} to $file")

        if (!file.canWrite()) return errorResult(ERROR_LACK_PERM, "Cannot delete file: $file")
        return successResult(file.delete())
    }

    override fun deleteExternalFile(externalFileDir: ExternalFileDir, path: String): HResult<*> {
        TODO("Not yet implemented")
    }

    override fun writeInternalFile(internalFileDir: InternalFileDir, path: String, content: String): HResult<*> {
        val file = File(internalFileDir.path() + path)
        file.parentFile?.let {
            if (!it.exists()) {
                logV("Creating parent dir $it")
                createInternalDirectory(internalFileDir, it.absolutePath)
            }
        }
        logV("Writing $path in ${internalFileDir.path()} to $file")
        if (!file.canWrite()) return errorResult(ERROR_LACK_PERM, "Cannot write file: $file")
        return successResult(file.writeText(content))
    }

    override fun writeExternalFile(externalFileDir: ExternalFileDir, path: String, content: String): HResult<*> {
        TODO("Not yet implemented")
    }

    override fun createInternalDirectory(internalFileDir: InternalFileDir, path: String): HResult<*> {
        val file = File(internalFileDir.path() + path)
        logV("Creating $path in ${internalFileDir.path()}")
        if (!file.canWrite()) return errorResult(ERROR_LACK_PERM, "Cannot write file: $file")
        val r = file.mkdirs()
        logV("Success? $r")
        return successResult(r)
    }

    override fun createExternalDirectory(externalFileDir: ExternalFileDir, path: String): HResult<*> {
        TODO("Not yet implemented")
    }
}