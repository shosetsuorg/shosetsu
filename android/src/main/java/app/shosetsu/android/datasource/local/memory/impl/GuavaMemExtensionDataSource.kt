package app.shosetsu.android.datasource.local.memory.impl

import app.shosetsu.android.common.ext.get
import app.shosetsu.android.common.ext.set
import app.shosetsu.common.consts.MEMORY_EXPIRE_EXT_LIB_TIME
import app.shosetsu.common.consts.MEMORY_MAX_EXTENSIONS
import app.shosetsu.common.datasource.memory.base.IMemExtensionsDataSource
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.emptyResult
import app.shosetsu.common.dto.successResult
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

	override fun loadExtensionFromMemory(extensionID: Int): HResult<IExtension> {
		//	logV("Loading formatter $extensionID from memory")
		return extensionsCache[extensionID]?.let { successResult(it) } ?: emptyResult()
	}

	override fun putExtensionInMemory(iExtension: IExtension): HResult<*> {
		//	logV("Putting formatter ${iExtension.formatterID} into memory")
		return successResult(extensionsCache.set(iExtension.formatterID, iExtension))
	}

	override fun removeExtensionFromMemory(extensionID: Int): HResult<*> {
		//	logV("Removing formatter $extensionID from memory")
		return successResult(extensionsCache.invalidate(extensionID))
	}
}