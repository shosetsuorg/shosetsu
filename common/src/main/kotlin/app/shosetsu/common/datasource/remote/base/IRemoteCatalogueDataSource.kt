package app.shosetsu.common.datasource.remote.base

import app.shosetsu.common.dto.HResult
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel

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
interface IRemoteCatalogueDataSource {

	/**
	 * Runs a search on an extension
	 */
	suspend fun search(
			ext: IExtension,
			query: String,
			data: Map<Int, Any>,
	): HResult<List<Novel.Listing>>


	/**
	 * Loads a listings data from an extension
	 */
	suspend fun loadListing(
		ext: IExtension,
		listingIndex: Int,
		data: Map<Int, Any>,
	): HResult<List<Novel.Listing>>
}