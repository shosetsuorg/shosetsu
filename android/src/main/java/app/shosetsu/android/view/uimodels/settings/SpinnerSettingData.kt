package app.shosetsu.android.view.uimodels.settings

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import app.shosetsu.android.view.uimodels.settings.base.RightSettingsItemData
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
class SpinnerSettingData(id: Int) : RightSettingsItemData(id) {
	var spinnerOnClick: (View) -> Unit = {}
	var spinnerOnItemSelectedListener: AdapterView.OnItemSelectedListener =
		object : AdapterView.OnItemSelectedListener {
			override fun onNothingSelected(parent: AdapterView<*>?) {
			}

			override fun onItemSelected(
				parent: AdapterView<*>?,
				view: View?,
				position: Int,
				id: Long,
			) {
			}
		}
	var arrayAdapter: ArrayAdapter<*>? = null
	var spinnerSelection: Int = -1

	override fun bindBinding(holder: SettingsItemBinding, payloads: List<Any>) {
		super.bindBinding(holder, payloads)
		with(holder) {
			spinner.visibility = View.VISIBLE
			//spinner.setOnClickListener { data.spinnerOnClick }
			spinner.adapter = arrayAdapter!!
			spinner.setSelection(spinnerSelection)
			spinner.onItemSelectedListener = spinnerOnItemSelectedListener
		}
	}

	override fun unbindBinding(holder: SettingsItemBinding) {
		super.unbindBinding(holder)
		with(holder) {
			spinner.onItemSelectedListener = null
		}
	}
}