package com.github.doomsdayrs.apps.shosetsu.backend.async

import android.util.Log
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.api.shosetsu.services.core.Novel
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.wait
import com.github.doomsdayrs.apps.shosetsu.variables.ext.defaultListing
import org.luaj.vm2.LuaError


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
open class CatalogueLoader(val formatter: Formatter, val filters: Array<*>, val query: String? = null) {
    companion object {
        private const val logID = "CatalogueLoader"
    }

    private var listing = formatter.defaultListing

    constructor(formatter: Formatter, filters: Array<*>, selectedListing: Int) : this(formatter, filters) {
        if (listing != selectedListing) listing = selectedListing
    }


    /**
     * Loads up the category
     *
     * @param integers if length = 0, loads first page otherwise loads the page # correlated to the integer
     * @return if this was completed or not
     */
    @Throws(LuaError::class)
    fun execute(vararg integers: Int?): Array<Novel.Listing> {
        Log.d(logID, "Loading")
        if (formatter.hasCloudFlare) {
            Log.i(logID, "CLOUDFLARE DETECED")
            wait(5)
        }

        Log.d(logID, "Selected listing $listing")
        return if (query == null)
            formatter.listings[listing].getListing(filters,
                    if (integers.isEmpty()) 1 else integers[0]!!)
        else
            formatter.search((listOf(query)+filters).toTypedArray())
            { Log.i("Formatter", "${formatter.name}\t$it") }

    }
}