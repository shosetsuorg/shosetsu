package com.github.doomsdayrs.apps.shosetsu.ui.settings

import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.ViewedController
import com.github.doomsdayrs.apps.shosetsu.ui.settings.adapter.SettingItemsAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem
import com.github.doomsdayrs.apps.shosetsu.variables.ext.context
import java.util.*

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
 * 29 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
abstract class SettingsSubController : ViewedController() {
    companion object {
        const val logID = "SettingsSubController"
    }

    override val idRes: Int = R.layout.settings
    abstract val settings: ArrayList<SettingsItem.SettingsItemData>
    var recyclerView: RecyclerView? = null

    val adapter: SettingItemsAdapter by lazy {
        Log.d(logID, "Creating adapter")
        SettingItemsAdapter(settings)
    }

    override fun onDestroyView(view: View) {
        recyclerView = null
    }

    override fun onViewCreated(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = adapter
    }

    fun findDataByID(@StringRes id: Int): Int {
        for ((index, data) in settings.withIndex()) {
            if (data.titleID == id)
                return index
        }
        return -1
    }
}
