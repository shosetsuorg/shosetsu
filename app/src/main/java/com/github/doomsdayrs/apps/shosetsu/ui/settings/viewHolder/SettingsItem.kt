package com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder

import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType

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
 */

/**
 * Shosetsu
 * 13 / 07 / 2019
 */
@Suppress("unused")
class SettingsItem(val view: View) : RecyclerView.ViewHolder(view) {
	var type: SettingsType = SettingsType.INFORMATION
	val itemTitle: TextView = itemView.findViewById(R.id.settings_item_title)
	val itemDescription: TextView = itemView.findViewById(R.id.settings_item_desc)
	val button: Button = itemView.findViewById(R.id.button)
	val spinner: Spinner = itemView.findViewById(R.id.spinner)
	val textView: TextView = itemView.findViewById(R.id.text)
	val switchView: Switch = itemView.findViewById(R.id.switchView)
	val numberPicker: NumberPicker = itemView.findViewById(R.id.numberPicker)
	val colorBox: View = itemView.findViewById(R.id.colorBox)
	val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)

	/**
	 * Data for [SettingsItem]
	 */
	class SettingsItemData(val type: SettingsType, val id: Int) {
		enum class SettingsType {
			INFORMATION,
			BUTTON,
			SPINNER,
			TEXT,
			SWITCH,
			NUMBER_PICKER,
			COLOR_PICKER,
			CHECKBOX
		}

		/** Min version required for this setting to be visible */
		var minVersionCode: Int = Build.VERSION_CODES.Q

		var titleID: Int = -1
		var titleText: String = ""

		var descID: Int = -1
		var descText: String = ""

		var textID: Int = -1
		var textText: String = ""
		var textViewOnClickListener: (View) -> Unit

		var buttonOnClickListener: (View) -> Unit

		var itemViewOnClick: (View) -> Unit


		var colorFunction: (color: Int) -> Unit = {}
		var itemColor: Int = Color.WHITE
		var colorPreferenceName = ""

		// Spinner
		var spinnerOnClick: (View) -> Unit
		var spinnerOnItemSelectedListener: AdapterView.OnItemSelectedListener =
				object : AdapterView.OnItemSelectedListener {
					override fun onNothingSelected(parent: AdapterView<*>?) {
					}

					override fun onItemSelected(
							parent: AdapterView<*>?,
							view: View?,
							position: Int,
							id: Long
					) {
					}
				}
		var arrayAdapter: ArrayAdapter<*>? = null
		var spinnerSelection: Int = -1

		// Switch

		var isChecked: Boolean = false
		var onCheckedListener: CompoundButton.OnCheckedChangeListener =
				CompoundButton.OnCheckedChangeListener { _, _ -> }

		// Number Picker
		var lowerBound = 0
		var upperBound = 0
		var numberPickerValue: Int = 0
		lateinit var numberPickerOnValueChangedListener:
				(picker: NumberPicker?, oldVal: Int, newVal: Int) -> Unit

		init {
			textViewOnClickListener = {}
			buttonOnClickListener = {}
			spinnerOnClick = {}
			itemViewOnClick = {}
		}

		fun setTitle(titleID: Int): SettingsItemData {
			this.titleID = titleID
			return this
		}

		fun setDescription(descID: Int): SettingsItemData {
			this.descID = descID
			return this
		}

		fun setTitle(title: String): SettingsItemData {
			this.titleText = title
			return this
		}

		fun setDescription(desc: String): SettingsItemData {
			descText = desc
			return this
		}

		fun setOnClickListenerButton(onClickListener: (View) -> Unit): SettingsItemData {
			buttonOnClickListener = (onClickListener)
			return this
		}

		@Suppress("unused")
		fun setOnClickListenerSpinner(onClickListener: (View) -> Unit): SettingsItemData {
			spinnerOnClick = onClickListener
			return this
		}

		fun setOnClickListener(onClickListener: (View) -> Unit): SettingsItemData {
			itemViewOnClick = onClickListener
			return this
		}

		fun setOnItemSelectedListener(onItemSelectedListener: AdapterView.OnItemSelectedListener)
				: SettingsItemData {
			spinnerOnItemSelectedListener = onItemSelectedListener
			return this
		}

		fun setArrayAdapter(ad: ArrayAdapter<*>): SettingsItemData {
			arrayAdapter = ad
			return this
		}

		fun setSpinnerSelection(i: Int): SettingsItemData {
			spinnerSelection = i
			return this
		}

		fun setTextViewText(s: String): SettingsItemData {
			textText = s
			return this
		}

		fun setTextViewText(id: Int): SettingsItemData {
			textID = id
			return this
		}

		fun setTextOnClickListener(onClickListener: (View) -> Unit): SettingsItemData {
			textViewOnClickListener = onClickListener
			return this
		}

		fun setIsChecked(b: Boolean): SettingsItemData {
			isChecked = b
			return this
		}

		fun setOnCheckedListner(onCheckedChangeListener: CompoundButton.OnCheckedChangeListener)
				: SettingsItemData {
			onCheckedListener = onCheckedChangeListener
			return this
		}

		fun setNumberLowerBound(int: Int): SettingsItemData {
			lowerBound = int
			return this
		}

		fun setNumberUpperBound(int: Int): SettingsItemData {
			upperBound = int
			return this
		}

		fun setNumberValue(int: Int): SettingsItemData {
			numberPickerValue = int
			return this
		}

		@RequiresApi
		fun setNumberPickerOnValueChangedListener(function: (
				picker: NumberPicker?,
				oldVal: Int,
				newVal: Int
		) -> Unit): SettingsItemData {
			this.numberPickerOnValueChangedListener = function
			return this
		}

		fun setPickerColor(color: Int): SettingsItemData {
			this.itemColor = color
			return this
		}

		fun setColorPreference(name: String): SettingsItemData {
			this.colorPreferenceName = name
			return this
		}

		fun setOnColorChosen(function: (color: Int) -> Unit): SettingsItemData {
			this.colorFunction = function
			return this
		}
	}
}