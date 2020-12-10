package app.shosetsu.android.datasource.memory.impl

import app.shosetsu.android.common.ext.get
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.common.ext.set
import app.shosetsu.common.consts.MEMORY_EXPIRE_EXT_LIB_TIME
import app.shosetsu.common.consts.MEMORY_MAX_EXTENSIONS
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.emptyResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.datasource.memory.base.IMemExtensionsDataSource
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
class GuavaMemExtensionDataSource : IMemExtensionsDataSource {
    /** Map of Formatter ID to Formatter */
    private val extensionsCache: Cache<Int, IExtension> = CacheBuilder.newBuilder()
		    .maximumSize(MEMORY_MAX_EXTENSIONS)
		    .expireAfterAccess(MEMORY_EXPIRE_EXT_LIB_TIME, MINUTES)
            .build()

    override suspend fun loadFormatterFromMemory(formatterID: Int): HResult<IExtension> {
	    logV("Loading formatter $formatterID from memory")
	    return extensionsCache[formatterID]?.let { successResult(it) } ?: emptyResult()
    }

    override suspend fun putFormatterInMemory(formatter: IExtension): HResult<*> {
	    logV("Putting formatter ${formatter.formatterID} into memory")
	    return successResult(extensionsCache.set(formatter.formatterID, formatter))
    }

    override suspend fun removeFormatterFromMemory(formatterID: Int): HResult<*> {
	    logV("Removing formatter $formatterID from memory")
	    return successResult(extensionsCache.invalidate(formatterID))
    }
}