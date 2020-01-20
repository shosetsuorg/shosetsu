package com.github.doomsdayrs.apps.shosetsu.ui.susScript.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterController
import com.github.doomsdayrs.apps.shosetsu.ui.susScript.SusScriptDialog

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
class SusScriptAdapter(val susScriptDialog: SusScriptDialog) : RecyclerView.Adapter<SusScriptAdapter.SusScriptCard>() {

    class SusScriptCard(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title1: TextView = itemView.findViewById(R.id.title)
        val version1: TextView = itemView.findViewById(R.id.version)
        val hash1: TextView = itemView.findViewById(R.id.hash)

        val title2: TextView = itemView.findViewById(R.id.title2)
        val version2: TextView = itemView.findViewById(R.id.version2)
        val hash2: TextView = itemView.findViewById(R.id.hash2)

        val radioGroup: RadioGroup = itemView.findViewById(R.id.radioGroup)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SusScriptCard {
        return SusScriptCard(LayoutInflater.from(parent.context).inflate(R.layout.alert_extensions_handle_card, parent, false))
    }

    override fun getItemCount(): Int {
        return susScriptDialog.files.size
    }

    override fun onBindViewHolder(holder: SusScriptCard, position: Int) {
        val file = susScriptDialog.files[position]

        val json = FormatterController.getMetaData(file) ?: kotlin.run {
            Log.e("SusScriptAdapter", "Deleting file, Malformed URL")
            susScriptDialog.files.removeAt(position)
            this.notifyDataSetChanged()
            return
        }
        print(json.toString(2))
        holder.title1.text = file.name.substring(0, file.name.length - 4)
        //holder.version1.text = json.getString("version")
    }
}