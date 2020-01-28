package com.github.doomsdayrs.apps.shosetsu.ui.susScript.objects

import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.susScript.SusScriptDialog
import com.github.doomsdayrs.apps.shosetsu.ui.susScript.adapters.SusScriptAdapter

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

class DialogBody(inflater: LayoutInflater, builder: AlertDialog.Builder, private val susScriptDialog: SusScriptDialog) {
    val view = inflater.inflate(R.layout.alert_extensions_handle, null)!!
    lateinit var dialog: AlertDialog

    init {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = SusScriptAdapter(susScriptDialog)
        recyclerView.layoutManager = LinearLayoutManager(builder.context, LinearLayoutManager.VERTICAL, false)

        val acceptAll: Button = view.findViewById(R.id.acceptAll)
        acceptAll.setOnClickListener {
            susScriptDialog.setAll(0)
            susScriptDialog.processActions()
            dialog.hide()
        }
        val tempAll: Button = view.findViewById(R.id.tempAll)
        tempAll.setOnClickListener {
            susScriptDialog.setAll(1)
            susScriptDialog.processActions()
            dialog.hide()
        }
        val disableAll: Button = view.findViewById(R.id.disableAll)
        disableAll.setOnClickListener {
            susScriptDialog.setAll(2)
            susScriptDialog.processActions()
            dialog.hide()
        }
        val uninstallAll: Button = view.findViewById(R.id.uninstallAll)
        uninstallAll.setOnClickListener {
            susScriptDialog.setAll(3)
            susScriptDialog.processActions()
            dialog.hide()
        }
    }
}