package com.github.doomsdayrs.apps.shosetsu.ui.settings.data

import android.graphics.Color
import android.view.View
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.base.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem
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
 */

/**
 * shosetsu
 * 25 / 06 / 2020
 */
class ColorPickerSettingData(id: Int) : SettingsItemData(id) {
	var colorFunction: (color: Int) -> Unit = {}
	var itemColor: Int = Color.WHITE
	var colorPreferenceName = ""
	override fun setupView(settingsItem: SettingsItem) {
		with(settingsItem) {
			colorBox.visibility = View.VISIBLE
			colorBox.setBackgroundColor(itemColor)
			colorBox.setOnClickListener {
				ColorPickerDialog.Builder(view.context)
						.setTitle("ColorPicker Dialog")
						.setPreferenceName(colorPreferenceName)
						.setPositiveButton(
								view.context.getString(R.string.confirm),
								ColorEnvelopeListener { envelope, _ ->
									colorFunction(envelope.color)
									colorBox.setBackgroundColor(envelope.color)
								}
						)
						.setNegativeButton(view.context.getString(android.R.string.cancel))
						{ dialogInterface, _ -> dialogInterface.dismiss() }.show()
			}
		}
	}
}