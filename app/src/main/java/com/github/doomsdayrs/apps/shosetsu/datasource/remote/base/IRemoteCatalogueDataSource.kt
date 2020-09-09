package com.github.doomsdayrs.apps.shosetsu.datasource.remote.base

import app.shosetsu.lib.Formatter
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult

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
			formatter: Formatter,
			query: String,
			page: Int,
			data: Map<Int, Any>,
	): HResult<List<Novel.Listing>>


	/**
	 * Loads a listings data from an extension
	 */
	suspend fun loadListing(
			formatter: Formatter,
			listing: Int,
			page: Int,
			data: Map<Int, Any>,
	): HResult<List<Novel.Listing>>
}