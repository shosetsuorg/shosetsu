package com.github.doomsdayrs.apps.shosetsu.backend.async

import app.shosetsu.lib.Formatter


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
 * ====================================================================
 */
/**
 * shosetsu
 * 20 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
@Deprecated("Nobel idea but bad")
open class CatalogueLoader(
		val formatter: Formatter,
		val filters: Array<*>,
		val query: String? = null,
) {
	/*
	/**
	 * Loads up the category
	 *
	 * @param integers if length = 0, loads first page otherwise
	 * loads the page # correlated to the integer
	 * @return if this was completed or not
	 */
	@Throws(LuaError::class)
	fun execute(vararg integers: Int?): Array<Novel.Listing> {
		Log.d(logID(), "Loading")
		if (formatter.hasCloudFlare) {
			Log.i(logID(), "CLOUDFLARE DETECED")
			wait(5)
		}

		Log.d(logID(), "Selected listing $listing")
		return if (query == null)
			formatter.listings[listing].getListing(filters,
					if (integers.isEmpty()) 1 else integers[0]!!)
		else
			formatter.search((listOf(query) + filters).toTypedArray())
			{ Log.i("Formatter", "${formatter.name}\t$it") }
	}

	 */
}