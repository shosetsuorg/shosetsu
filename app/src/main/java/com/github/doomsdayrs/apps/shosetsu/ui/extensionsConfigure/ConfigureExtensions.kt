package com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.adapters.ConfigExtAdapter
import kotlinx.android.synthetic.main.settings.*
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
 * @param jsonArray Array of disabled formatters, Includes . . . imageURL, Name, ID
 */
class ConfigureExtensions : Fragment(R.layout.alert_extensions_configure) {
    lateinit var jsonArray: JSONArray

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utilities.setActivityTitle(activity,getString(R.string.configure_extensions))
        if (savedInstanceState != null) {
            jsonArray = JSONArray(savedInstanceState.getString("array", "[]"))
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ConfigExtAdapter(this)
    }

}