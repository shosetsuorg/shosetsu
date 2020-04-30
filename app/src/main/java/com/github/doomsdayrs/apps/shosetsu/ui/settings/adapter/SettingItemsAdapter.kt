package com.github.doomsdayrs.apps.shosetsu.ui.settings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData


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
 * 18 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
class SettingItemsAdapter(private val items: ArrayList<SettingsItemData>)
	: RecyclerView.Adapter<SettingsItem>() {
	private val views: ArrayList<SettingsItem> = arrayListOf()
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsItem {
		val view = LayoutInflater.from(parent.context).inflate(
				R.layout.settings_item,
				parent,
				false
		)
		val i = SettingsItem(view)
		views.add(i)
		return i
	}

	override fun getItemCount(): Int {
		return items.size
	}

	override fun onBindViewHolder(holder: SettingsItem, position: Int) {
		holder.setData(items[position])
	}
}