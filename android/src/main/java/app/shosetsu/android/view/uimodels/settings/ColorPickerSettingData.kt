package app.shosetsu.android.view.uimodels.settings

import android.graphics.Color
import androidx.core.view.isVisible
import app.shosetsu.android.view.uimodels.settings.base.BottomSettingsItemData
import com.github.doomsdayrs.apps.shosetsu.databinding.SettingsItemBinding

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

	override fun bindBinding(holder: SettingsItemBinding, payloads: List<Any>) {
		super.bindBinding(holder, payloads)
		with(holder) {
			colorBox.isVisible = true
			colorBox.setBackgroundColor(itemColor)
			colorBox.setOnClickListener {
				/*
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
				*/
			}
		}
	}

	override fun unbindBinding(holder: SettingsItemBinding) {
		super.unbindBinding(holder)
		with(holder) {
			colorBox.setOnClickListener(null)
		}
	}
}