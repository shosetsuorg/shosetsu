package com.github.doomsdayrs.apps.shosetsu.view.uimodels.settings.base

import android.os.Build
import android.view.View
import android.widget.*
import androidx.annotation.CallSuper
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.consts.VISIBLE
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.base.BaseRecyclerItem
import com.google.android.material.textfield.TextInputEditText
import com.mikepenz.fastadapter.FastAdapter
import com.xw.repo.BubbleSeekBar

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
 */

/**
 * shosetsu
 * 25 / 06 / 2020
 * Data for [SettingsItem]
 */
abstract class SettingsItemData(val id: Int) : BaseRecyclerItem<SettingsItemData.ViewHolder>() {
	/** Min version required for this setting to be visible */
	var minVersionCode: Int = Build.VERSION_CODES.Q

	var titleID: Int = -1
	var titleText: String = ""

	var descID: Int = -1
	var descText: String = ""

	@CallSuper
	override fun bindView(settingsItem: ViewHolder, payloads: List<Any>) {
		super.bindView(settingsItem, payloads)
		with(settingsItem) {
			if (titleID != -1)
				itemTitle.setText(titleID)
			else
				itemTitle.text = titleText

			if (descID != -1) {
				itemDescription.visibility = VISIBLE
				itemDescription.setText(descID)
			} else if (descText.isNotEmpty()) {
				itemDescription.visibility = VISIBLE
				itemDescription.text = descText
			}
		}
	}

	@CallSuper
	override fun unbindView(settingsItem: ViewHolder) {
		with(settingsItem) {
			itemTitle.text = null
			itemDescription.text = null
		}
	}

	class ViewHolder(itemView: View) : FastAdapter.ViewHolder<SettingsItemData>(itemView) {
		/** Item main view */
		val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayout)

		/** Item title */
		val itemTitle: TextView = itemView.findViewById(R.id.settings_item_title)

		/** Item description */
		val itemDescription: TextView = itemView.findViewById(R.id.settings_item_desc)

		/** @see Button */
		val button: Button = itemView.findViewById(R.id.button)

		/** @see Spinner */
		val spinner: Spinner = itemView.findViewById(R.id.spinner)

		/** @see TextView */
		val textView: TextView = itemView.findViewById(R.id.text)

		/** @see SwitchCompat */
		val switchView: SwitchCompat = itemView.findViewById(R.id.switchView)

		/** @see NumberPicker */
		val numberPicker: NumberPicker = itemView.findViewById(R.id.numberPicker)

		/** @see View */
		val colorBox: View = itemView.findViewById(R.id.colorBox)

		/** @see CheckBox */
		val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)

		/** @see BubbleSeekBar */
		val seekbar: BubbleSeekBar = itemView.findViewById(R.id.bubbleSeekBar)

		/** @see TextInputEditText */
		val textInputEditText: TextInputEditText = itemView.findViewById(R.id.textInputEditText)

		/** Contains fields on the right */
		val rightField: ConstraintLayout = itemView.findViewById(R.id.rightField)

		/** Contains fields on the bottom */
		val bottomField: ConstraintLayout = itemView.findViewById(R.id.bottomField)

		override fun bindView(item: SettingsItemData, payloads: List<Any>) =
				item.bindView(this, payloads)

		override fun unbindView(item: SettingsItemData) =
				item.unbindView(this)

	}

	override val layoutRes: Int = R.layout.settings_item
	override val type: Int = javaClass.hashCode()
	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)
}
