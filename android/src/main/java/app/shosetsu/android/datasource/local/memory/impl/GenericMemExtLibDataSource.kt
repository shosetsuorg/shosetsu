package app.shosetsu.android.datasource.local.memory.impl

import app.shosetsu.android.common.consts.MEMORY_EXPIRE_EXT_LIB_TIME
import app.shosetsu.android.common.consts.MEMORY_MAX_EXT_LIBS
import app.shosetsu.android.datasource.local.memory.base.IMemExtLibDataSource

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
 * 19 / 11 / 2020
 */
class GenericMemExtLibDataSource : IMemExtLibDataSource,
	AbstractMemoryDataSource<String, String>() {

	override val maxSize = MEMORY_MAX_EXT_LIBS
	override val expireTime = MEMORY_EXPIRE_EXT_LIB_TIME * 1000 * 60

	override fun loadLibrary(name: String): String? =
		get(name)

	override fun setLibrary(name: String, data: String) =
		put(name, data)

	override fun removeLibrary(name: String) {
		remove(name)
	}
}