package app.shosetsu.common.datasource.memory.base

import app.shosetsu.common.dto.HResult

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
 * 05 / 05 / 2020
 */
interface IMemExtLibDataSource {
    /** Load Library from memory */
    fun loadLibrary(name: String): HResult<String>


	/** Put Library in memory */
	fun setLibrary(name: String, data: String): HResult<*>


	/** Remove Library by ID from cache*/
	fun removeLibrary(name: String): HResult<*>
}