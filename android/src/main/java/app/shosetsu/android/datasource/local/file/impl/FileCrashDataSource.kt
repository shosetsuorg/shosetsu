package app.shosetsu.android.datasource.local.file.impl

import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.common.enums.ExternalFileDir.APP
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.datasource.local.file.base.IFileCrashDataSource
import app.shosetsu.android.providers.file.base.IFileSystemProvider
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

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
 * Shosetsu
 *
 * @since 19 / 07 / 2021
 * @author Doomsdayrs
 */
class FileCrashDataSource(
	private val fileSystem: IFileSystemProvider
) : IFileCrashDataSource {
	init {
		try {
			fileSystem.createDirectory(APP, DIRECTORY)
			logV("Directory created")
		} catch (e: Exception) {
			logE("Failed to create directory", e)
		}
	}

	val creationDate: String
		get() = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss", Locale.ROOT).format(Date())

	@Throws(FilePermissionException::class, IOException::class)
	override fun writeCrash(log: String): Unit =
		fileSystem.writeFile(APP, "$DIRECTORY/shosetsu-crash-$creationDate.txt", log.toByteArray())

	companion object {
		private const val DIRECTORY = "Crash"
	}
}