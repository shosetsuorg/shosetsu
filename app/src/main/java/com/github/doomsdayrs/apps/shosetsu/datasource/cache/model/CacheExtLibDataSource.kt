package com.github.doomsdayrs.apps.shosetsu.datasource.cache.model

import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.emptyResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.get
import com.github.doomsdayrs.apps.shosetsu.common.ext.set
import com.github.doomsdayrs.apps.shosetsu.datasource.cache.base.ICacheExtLibDataSource
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit

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
 * 13 / 05 / 2020
 */
class CacheExtLibDataSource : ICacheExtLibDataSource {
	/** Library paring */
	private val libraries: Cache<String, String> = CacheBuilder.newBuilder()
			.maximumSize(200)
			.expireAfterWrite(1, TimeUnit.HOURS)
			.build()

	override suspend fun loadLibrary(name: String): HResult<String> =
			blockingLoadLibrary(name)

	override fun blockingLoadLibrary(name: String): HResult<String> =
			libraries[name]?.let { successResult(it) } ?: emptyResult()

	override suspend fun setLibrary(name: String, data: String): HResult<*> {
		libraries[name] = data
		return successResult("")
	}

	override fun blockingSetLibrary(name: String, data: String): HResult<*> {
		libraries[name] = data
		return successResult("")
	}

	override suspend fun removeLibrary(name: String): HResult<*> =
			successResult(libraries.invalidate(name))
}