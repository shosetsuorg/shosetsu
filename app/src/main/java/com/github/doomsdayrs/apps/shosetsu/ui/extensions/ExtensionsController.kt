package com.github.doomsdayrs.apps.shosetsu.ui.extensions

import android.app.Activity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterController
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.ViewedController
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.adapter.ExtensionsAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.ext.context
import com.github.doomsdayrs.apps.shosetsu.variables.ext.getString
import com.github.doomsdayrs.apps.shosetsu.variables.obj.DefaultScrapers
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
class ExtensionsController(override val idRes: Int = R.layout.fragment_catalogues) : ViewedController() {

    val array: ArrayList<JSONObject> = ArrayList()
    lateinit var adapter: ExtensionsAdapter
    lateinit var recyclerView: RecyclerView

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_extensions, menu)
    }

    override fun onViewCreated(view: View) {
        Utilities.setActivityTitle(activity, getString(R.string.extensions))
        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ExtensionsAdapter(this)
        recyclerView.adapter = adapter
        setData()
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


    fun setData() {
        array.clear()
        val keys = ArrayList<String>()
        FormatterController.sourceJSON.keys().forEach { keys.add(it) }
        keys.remove("comments")
        keys.remove("libraries")
        for (key in keys) {
            val obj = FormatterController.sourceJSON.getJSONObject(key)
            obj.put("name", key)
            if (!array.contains(obj))
                array.add(obj)
        }
        adapter.notifyDataSetChanged()
    }
}