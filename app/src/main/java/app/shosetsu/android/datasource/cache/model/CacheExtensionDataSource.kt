package app.shosetsu.android.datasource.cache.model

import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.emptyResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.common.ext.get
import app.shosetsu.android.common.ext.set
import app.shosetsu.android.datasource.cache.base.ICacheExtensionsDataSource
import app.shosetsu.lib.IExtension
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit.MINUTES

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
	/** Map of Formatter ID to Formatter */
	private val formatters: Cache<Int, IExtension> = CacheBuilder.newBuilder()
			.expireAfterAccess(20, MINUTES)
			.build()

	override suspend fun loadFormatterFromMemory(formatterID: Int): HResult<IExtension> =
			formatters[formatterID]?.let { successResult(it) } ?: emptyResult()

	override suspend fun putFormatterInMemory(formatter: IExtension): HResult<*> =
			successResult(formatters.set(formatter.formatterID, formatter))

	override suspend fun removeFormatterFromMemory(formatterID: Int): HResult<*> =
			successResult(formatters.invalidate(formatterID))
}