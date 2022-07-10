package app.shosetsu.android.datasource.local.file.base

import app.shosetsu.android.common.FileNotFoundException
import app.shosetsu.android.common.FilePermissionException
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
interface IFileExtLibDataSource {

	@Throws(FilePermissionException::class, IOException::class)
	suspend fun writeExtLib(fileName: String, data: String)

	@Throws(FileNotFoundException::class, FilePermissionException::class)
	suspend fun loadExtLib(fileName: String): String

	@Throws(FileNotFoundException::class, FilePermissionException::class)
	suspend fun deleteExtLib(fileName: String)
}