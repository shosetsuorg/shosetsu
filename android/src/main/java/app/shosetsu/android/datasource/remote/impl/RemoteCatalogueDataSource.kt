package app.shosetsu.android.datasource.remote.impl

import app.shosetsu.android.common.ext.logD
import app.shosetsu.android.datasource.remote.base.IRemoteCatalogueDataSource
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
import app.shosetsu.lib.PAGE_INDEX
import app.shosetsu.lib.QUERY_INDEX
import app.shosetsu.lib.exceptions.HTTPException
import org.luaj.vm2.LuaError
import java.io.IOException

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
 * 10 / May / 2020
 */
class RemoteCatalogueDataSource : IRemoteCatalogueDataSource {

	@Throws(HTTPException::class, IOException::class, LuaError::class)
	override suspend fun search(
		ext: IExtension,
		query: String,
		data: Map<Int, Any>,
	): List<Novel.Listing> {
		return if (ext.hasSearch) {
			try {
				ext.search(HashMap(data).apply {
					this[QUERY_INDEX] = query
				}).toList()
			} catch (e: LuaError) {
				if (e.cause != null)
					throw e.cause!!
				else throw e
			}
		} else emptyList()
	}

	@Throws(HTTPException::class, LuaError::class, IOException::class)
	override suspend fun loadListing(
		ext: IExtension,
		listingIndex: Int,
		data: Map<Int, Any>,
	): List<Novel.Listing> {
		val listing = ext.listings[listingIndex]

		logD(data.toString())

		return if (!listing.isIncrementing && (data[PAGE_INDEX] as Int) > ext.startIndex) {
			emptyList()
		} else try {
			listing.getListing(data).toList()
		} catch (e: LuaError) {
			if (e.cause != null)
				throw e.cause!!
			else throw e
		}
	}
}

