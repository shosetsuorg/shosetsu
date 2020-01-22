package com.github.doomsdayrs.apps.shosetsu.ui.extensions

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterController
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.adapter.ExtensionsAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers
import kotlinx.android.synthetic.main.fragment_catalogues.*
import org.json.JSONObject

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
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsFragment : Fragment(R.layout.fragment_catalogues) {

    val array: ArrayList<JSONObject> = ArrayList()

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_extensions, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                FormatterController.RefreshJSON(context as Activity, this).execute()
                true
            }
            R.id.reload -> {
                DefaultScrapers.formatters.clear()
                FormatterController.FormatterInit(context as Activity).execute()
                true
            }
            else -> false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utilities.setActivityTitle(activity, "Extensions")
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ExtensionsAdapter(this)
        setData()
    }

    private fun setData() {
        array.clear()
        val keys = ArrayList<String>()
        FormatterController.sourceJSON.keys().forEach { keys.add(it) }
        keys.remove("comments")
        for (key in keys) {
            val obj = FormatterController.sourceJSON.getJSONObject(key)
            obj.put("name", key)
            if (!array.contains(obj))
                array.add(obj)
        }
        recyclerView.adapter!!.notifyDataSetChanged()
    }
}