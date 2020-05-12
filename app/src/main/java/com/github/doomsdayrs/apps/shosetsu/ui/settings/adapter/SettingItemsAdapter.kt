package com.github.doomsdayrs.apps.shosetsu.ui.settings.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType.*
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener


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
class SettingItemsAdapter(private val items: List<SettingsItemData>)
	: RecyclerView.Adapter<SettingsItem>() {
	private val views: ArrayList<SettingsItem> = arrayListOf()

	@Suppress("KDocMissingDocumentation")
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

	@Suppress("KDocMissingDocumentation")
	override fun getItemCount(): Int = items.size

	@Suppress("KDocMissingDocumentation")
	override fun onBindViewHolder(holder: SettingsItem, position: Int) {
		val data = items[position]
		with(holder) {
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
				BUTTON -> {
					if (data.textID != -1)
						button.setText(data.textID)
					else
						button.text = data.textText
					button.visibility = View.VISIBLE
					button.setOnClickListener(data.buttonOnClickListener)
				}
				SPINNER -> {
					spinner.visibility = View.VISIBLE
					//spinner.setOnClickListener { data.spinnerOnClick }
					spinner.adapter = data.adapter
					spinner.setSelection(data.spinnerSelection)
					spinner.onItemSelectedListener = data.spinnerOnItemSelectedListener
				}
				INFORMATION -> {
					itemView.setOnClickListener(data.itemViewOnClick)
				}
				TEXT -> {
					if (data.textID != -1)
						textView.setText(data.textID)
					else
						textView.text = data.textText
					textView.visibility = View.VISIBLE
					textView.setOnClickListener(data.textViewOnClickListener)
				}
				SWITCH -> {
					switchView.visibility = View.VISIBLE
					switchView.isChecked = data.isChecked
					switchView.setOnCheckedChangeListener(data.onCheckedListener)
				}
				NUMBER_PICKER -> {
					numberPicker.visibility = View.VISIBLE
					numberPicker.minValue = data.lowerBound
					numberPicker.maxValue = data.upperBound
					numberPicker.value = data.numberPickerValue
					numberPicker.setOnValueChangedListener(data.numberPickerOnValueChangedListener)
				}
				CHECKBOX -> {
					checkBox.visibility = View.VISIBLE
					checkBox.isChecked = data.isChecked
					checkBox.setOnCheckedChangeListener(data.onCheckedListener)
				}
				COLOR_PICKER -> {
					colorBox.visibility = View.VISIBLE
					colorBox.setBackgroundColor(data.itemColor)
					colorBox.setOnClickListener {
						ColorPickerDialog.Builder(view.context)
								.setTitle("ColorPicker Dialog")
								.setPreferenceName(data.colorPreferenceName)
								.setPositiveButton(
										view.context.getString(R.string.confirm),
										ColorEnvelopeListener { envelope, _ ->
											data.colorFunction(envelope.color)
											colorBox.setBackgroundColor(envelope.color)
										}
								)
								.setNegativeButton(view.context.getString(android.R.string.cancel))
								{ dialogInterface, _ -> dialogInterface.dismiss() }.show()
					}
				}
			}
		}
	}
}