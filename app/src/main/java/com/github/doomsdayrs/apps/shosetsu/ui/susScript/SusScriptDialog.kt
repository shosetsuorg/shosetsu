package com.github.doomsdayrs.apps.shosetsu.ui.susScript

import android.app.Activity
import android.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.susScript.adapters.SusScriptAdapter
import java.io.File

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
 * 19 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class SusScriptDialog(val activity: Activity, val files: ArrayList<File>) {

    fun execute() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(activity.getString(R.string.sus_script_title))
        builder.setMessage(R.string.sus_scripts)
        val recyclerView = RecyclerView(builder.context)
        recyclerView.adapter = SusScriptAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(builder.context, LinearLayoutManager.VERTICAL, false)
        builder.setView(recyclerView)
        builder.setPositiveButton(android.R.string.yes) { _, _ -> }
        builder.setNegativeButton(android.R.string.cancel) { _, _ -> }
        builder.show()
    }
}