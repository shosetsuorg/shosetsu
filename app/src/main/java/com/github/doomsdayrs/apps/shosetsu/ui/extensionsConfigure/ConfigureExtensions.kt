package com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure

import android.os.Bundle
import android.util.Log
import android.view.View
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.controllers.RecyclerController
import com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.adapters.ConfigExtAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.ext.getString
import org.json.JSONArray

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
 * 21 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ConfigureExtensions : RecyclerController<ConfigExtAdapter>() {
    companion object {
        const val logID = "ConfigureExtensions"
    }

    lateinit var jsonArray: JSONArray

    override fun onSaveInstanceState(outState: Bundle) = outState.putString("array", jsonArray.toString())

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d(logID, "Restoring")
        jsonArray = JSONArray(savedInstanceState.getString("array", "[]"))
    }

    override fun onViewCreated(view: View) {
        Utilities.setActivityTitle(activity, getString(R.string.configure_extensions))
        Log.d(logID, "Array received:\t$jsonArray")
        adapter = ConfigExtAdapter(this)
    }

}