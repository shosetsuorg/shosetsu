package app.shosetsu.android.datasource.memory.model

import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.emptyResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.common.ext.get
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.common.ext.set
import app.shosetsu.android.datasource.memory.base.IMemExtLibDataSource
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
class GuavaMemExtLibDataSource : IMemExtLibDataSource {
    /** Library paring */
    private val libraries: Cache<String, String> = CacheBuilder.newBuilder()
            .maximumSize(200)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build()

    override suspend fun loadLibrary(name: String): HResult<String> =
            blockingLoadLibrary(name)

    override fun blockingLoadLibrary(name: String): HResult<String> {
        val result = libraries[name]
        logV("Loading $name from memory (success?: ${result != null})")
        return result?.let { successResult(it) } ?: emptyResult()
    }

    override suspend fun setLibrary(name: String, data: String): HResult<*> =
            blockingSetLibrary(name, data)

    override fun blockingSetLibrary(name: String, data: String): HResult<*> {
        logV("Putting $name into memory")
        libraries[name] = data
        return successResult("")
    }

    override suspend fun removeLibrary(name: String): HResult<*> =
            successResult(libraries.invalidate(name))
}