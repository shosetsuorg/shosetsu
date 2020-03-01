package com.github.doomsdayrs.apps.shosetsu.backend.async

import android.util.Log
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.api.shosetsu.services.core.Novel
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.wait
import com.github.doomsdayrs.apps.shosetsu.variables.ext.getListing
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaTable


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
open class CatalogueLoader(val formatter: Formatter) {

    var query: String = ""

    constructor(query: String, formatter: Formatter) : this(formatter) {
        this.query = query
    }

    /**
     * Loads up the category
     *
     * @param integers if length = 0, loads first page otherwise loads the page # correlated to the integer
     * @return if this was completed or not
     */
    @Throws(LuaError::class)
    fun execute(vararg integers: Int?): Array<Novel.Listing> {
        Log.d("CatalogueLoader", "Loading")
        if (formatter.hasCloudFlare) {
            Log.i("CatalogueLoader", "CLOUDFLARE DETECED")
            wait(5)
        }
        // Loads novel list
        return if (integers.isEmpty())
            if (query.isEmpty())
                formatter.getListing().getListing(1)
            else {
                val table = LuaTable()
                table["query"] = query
                formatter.search(table) { Log.i("Formatter", "${formatter.name}\t$it") }
            }
        else
            formatter.getListing().getListing(integers[0]!!)
    }
}