package app.shosetsu.android.datasource.remote.model

import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.datasource.remote.base.IRemoteCatalogueDataSource
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.com.dto.emptyResult
import app.shosetsu.common.com.dto.successResult
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
import app.shosetsu.lib.PAGE_INDEX
import app.shosetsu.lib.QUERY_INDEX

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


	override suspend fun search(
			ext: IExtension,
			query: String,
			data: Map<Int, Any>,
	): HResult<List<Novel.Listing>> {
		return try {
			if (ext.hasSearch) {
				val l = ext.search(HashMap(data).apply {
					this[QUERY_INDEX] = query
				}).toList()

				if (l.isEmpty()) emptyResult() else successResult(l)
			} else emptyResult()
		} catch (e: Exception) {
			e.toHError()
		}
	}

	override suspend fun loadListing(
			ext: IExtension,
			listing: Int,
			data: Map<Int, Any>,
	): HResult<List<Novel.Listing>> {
		return try {
			logV("Data: $data")
			val l = ext.listings[listing]
			if (!l.isIncrementing && (data[PAGE_INDEX] as Int) > 1) {
				logV("### EMPTY RESULT ###")
				emptyResult()
			} else successResult(l.getListing(data).toList())
		} catch (e: Exception) {
			e.toHError()
		}
	}
}

