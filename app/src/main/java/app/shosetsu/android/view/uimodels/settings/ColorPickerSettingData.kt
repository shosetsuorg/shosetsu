package app.shosetsu.android.view.uimodels.settings

import android.graphics.Color
import android.view.View
import app.shosetsu.android.view.uimodels.settings.base.BottomSettingsItemData
import com.github.doomsdayrs.apps.shosetsu.R
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.ColorPickerDialog.Builder as CPDB

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
class ColorPickerSettingData(id: Int) : BottomSettingsItemData(id) {
	var colorFunction: (color: Int) -> Unit = {}
	var itemColor: Int = Color.WHITE
	var colorPreferenceName: String = ""

	override fun bindView(holder: ViewHolder, payloads: List<Any>) {
		super.bindView(holder, payloads)
		with(holder) {
			colorBox.visibility = View.VISIBLE
			colorBox.setBackgroundColor(itemColor)
			colorBox.setOnClickListener {
				CPDB(itemView.context)
						.setTitle("ColorPicker Dialog")
						.setPreferenceName(colorPreferenceName)
						.setPositiveButton(
								itemView.context.getString(R.string.confirm),
								ColorEnvelopeListener { envelope, _ ->
									colorFunction(envelope.color)
									colorBox.setBackgroundColor(envelope.color)
								}
						)
						.setNegativeButton(itemView.context.getString(android.R.string.cancel))
						{ dialogInterface, _ -> dialogInterface.dismiss() }.show()
			}
		}
	}

	override fun unbindView(holder: ViewHolder) {
		super.unbindView(holder)
		with(holder) {
			colorBox.setOnClickListener(null)
		}
	}
}