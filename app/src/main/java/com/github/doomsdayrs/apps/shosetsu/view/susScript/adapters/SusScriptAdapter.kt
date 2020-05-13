package com.github.doomsdayrs.apps.shosetsu.view.susScript.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.view.susScript.SusScriptDialog
import com.github.doomsdayrs.apps.shosetsu.view.susScript.viewHolders.SusScriptCard
import com.github.doomsdayrs.apps.shosetsu.common.ext.md5

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
class SusScriptAdapter(private val susScriptDialog: SusScriptDialog) : RecyclerView.Adapter<SusScriptCard>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SusScriptCard {
		return SusScriptCard(LayoutInflater.from(parent.context).inflate(R.layout.alert_extensions_handle_card, parent, false))
	}

	override fun getItemCount(): Int {
		return susScriptDialog.files.size
	}

	override fun onBindViewHolder(holder: SusScriptCard, position: Int) {
		val fileObj = susScriptDialog.files[position]
		val file = fileObj.file

		//val json = FormatterUtils.getMetaData(file) ?: kotlin.run {
		//	Log.e("SusScriptAdapter", "Deleting file, Malformed URL")
		//	susScriptDialog.files.removeAt(position)
		//	this.notifyDataSetChanged()
		//	return
		//}

		holder.spinner.adapter = ArrayAdapter(holder.itemView.context!!, android.R.layout.simple_spinner_item, holder.itemView.resources.getStringArray(R.array.sus_array_actions))
		holder.spinner.setSelection(3)

		holder.spinner.onItemSelectedListener = object : OnItemSelectedListener {
			override fun onNothingSelected(parent: AdapterView<*>?) {
			}

			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				fileObj.action = position
			}
		}

		val string = file.nameWithoutExtension
		holder.title1.text = string
		//holder.version1.text = json.getString("version")
		holder.hash1.text = file.readText().md5() ?: ""

		if (false) {
			holder.title2.visibility = View.VISIBLE
			holder.version2.visibility = View.VISIBLE
			holder.hash2.visibility = View.VISIBLE

			//val realJSON = FormatterUtils.sourceJSON.getJSONObject(file.nameWithoutExtension)
			holder.title2.text = file.nameWithoutExtension
		//	holder.version2.text = realJSON.getString("version")
			//holder.hash2.text = realJSON.getString("md5")
		}
	}
}