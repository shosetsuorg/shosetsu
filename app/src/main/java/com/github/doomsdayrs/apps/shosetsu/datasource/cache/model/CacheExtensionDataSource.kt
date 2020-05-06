package com.github.doomsdayrs.apps.shosetsu.datasource.cache.model

import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.emptyResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.datasource.cache.base.ICacheExtensionsDataSource

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
 * 04 / 05 / 2020
 */
class CacheExtensionDataSource : ICacheExtensionsDataSource {
	/**
	 * Map of Formatter ID to Formatter
	 */
	private val formatters: MutableMap<Int, Formatter> = mutableMapOf()

	override suspend fun loadFormatterFromMemory(formatterID: Int): HResult<Formatter> =
			formatters[formatterID]?.let { successResult(it) } ?: emptyResult()

	override suspend fun putFormatterInMemory(formatter: Formatter) {
		formatters[formatter.formatterID] = formatter
	}
}