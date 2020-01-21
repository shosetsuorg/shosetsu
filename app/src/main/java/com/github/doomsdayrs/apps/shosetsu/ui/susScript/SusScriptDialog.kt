package com.github.doomsdayrs.apps.shosetsu.ui.susScript

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.dep.LuaFormatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterController
import com.github.doomsdayrs.apps.shosetsu.ui.susScript.adapters.SusScriptAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers
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
class SusScriptDialog(val activity: Activity, fileList: ArrayList<File>) {

    class Dialog(inflater: LayoutInflater, private val builder: AlertDialog.Builder, private val susScriptDialog: SusScriptDialog) {
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

    class FileObject(val file: File) {
        /**
         * 0: Accept, 1: Temp Accept, 2: Disable, 3: Remove
         */
        var action: Int = 3

        fun delete() {
            file.delete()
        }
    }

    val files = ArrayList<FileObject>()

    init {
        for (file in fileList) {
            files.add(FileObject(file))
        }
    }

    fun execute() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(activity.getString(R.string.sus_script_title))
        builder.setMessage(R.string.sus_scripts)

        val dialog = Dialog(activity.layoutInflater, builder, this)
        builder.setView(dialog.view)

        builder.setPositiveButton(android.R.string.yes) { _, _ ->
            processActions()
        }

        builder.setNegativeButton(android.R.string.cancel) { _, _ ->
            for (file in files) {
                Log.i("SusScriptDialog", "Deleting\t${file.file.name}")
                file.delete()
            }
        }
        dialog.dialog = builder.show()
    }

    fun processActions() {
        for (file in files) {
            Log.i("SusScriptDialog", "File confirmed Action\t${file.file.name}\t${file.action}")
            when (file.action) {
                0 -> {
                    FormatterController.trustScript(file.file)
                    DefaultScrapers.formatters.add(LuaFormatter(file.file))
                }
                1 -> {
                    DefaultScrapers.formatters.add(LuaFormatter(file.file))
                }
                2 -> {
                    //TODO ignore list
                }
                3 -> {
                    file.delete()
                }
            }
        }
    }

    fun setAll(int: Int) {
        for (file in files)
            file.action = int
    }
}