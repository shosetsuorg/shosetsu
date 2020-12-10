package app.shosetsu.common.datasource.memory.impl

import app.shosetsu.common.consts.MEMORY_EXPIRE_EXTENSION_TIME
import app.shosetsu.common.consts.MEMORY_MAX_EXTENSIONS
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.emptyResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.datasource.memory.base.IMemExtensionsDataSource
import app.shosetsu.lib.IExtension

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
class GenericMemExtensionDataSource : IMemExtensionsDataSource {
	private val extensions = HashMap<Int, Pair<Long, IExtension>>()
		get() {
			recycle(field)
			return field
		}

	private fun recycle(hashMap: HashMap<Int, Pair<Long, IExtension>>) {
		val keys = hashMap.keys
		for (i in keys) {
			val (time) = hashMap[i] ?: continue
			if (time + ((MEMORY_EXPIRE_EXTENSION_TIME * 1000) * 60) <= System.currentTimeMillis())
				hashMap.remove(i)
		}
	}

	override suspend fun loadFormatterFromMemory(formatterID: Int): HResult<IExtension> {
		val extension = extensions
		return if (extension.containsKey(formatterID))
			extension[formatterID]?.let { successResult(it.second) }
					?: emptyResult() else emptyResult()
	}

	override suspend fun putFormatterInMemory(formatter: IExtension): HResult<*> {
		val extension = extensions
		if (extension.size > MEMORY_MAX_EXTENSIONS) extension.remove(extension.keys.first())
		extension[formatter.formatterID] = System.currentTimeMillis() to formatter
		return successResult("")
	}

	override suspend fun removeFormatterFromMemory(formatterID: Int): HResult<*> =
			if (!extensions.containsKey(formatterID)) emptyResult()
			else {
				extensions.remove(formatterID)
				successResult("")
			}

}