package com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects

import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */
/**
 * Shosetsu
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 * hXtreme
 */
class SettingsItem(view: View) : RecyclerView.ViewHolder(view) {
    private var type: SettingsItemData.SettingsType = SettingsItemData.SettingsType.INFORMATION

    val itemTitle: TextView = itemView.findViewById(R.id.settings_item_title)
    val itemDescription: TextView = itemView.findViewById(R.id.settings_item_desc)
    val button: Button = itemView.findViewById(R.id.button)
    val spinner: Spinner = itemView.findViewById(R.id.spinner)

    fun setData(data: SettingsItemData): SettingsItem {
        type = data.type
        if (data.titleID != -1)
            itemTitle.setText(data.titleID)
        else
            itemTitle.text = data.titleText

        if (data.descID != -1)
            itemDescription.setText(data.descID)
        else
            itemDescription.text = data.descriptionText

        when (type) {
            SettingsItemData.SettingsType.BUTTON -> {
                button.visibility = Button.VISIBLE
                button.setOnClickListener { data.buttonOnClickListener }
            }
            SettingsItemData.SettingsType.SPINNER -> {
                spinner.visibility = Spinner.VISIBLE
                //spinner.setOnClickListener { data.spinnerOnClick }
                spinner.adapter = data.adapter
                spinner.onItemSelectedListener = data.spinnerOnItemSelectedListener
            }
            SettingsItemData.SettingsType.INFORMATION -> {
                itemView.setOnClickListener(data.itemViewOnClick)
            }
        }
        return this
    }

    fun invalidate() {
        itemView.invalidate()
    }


}