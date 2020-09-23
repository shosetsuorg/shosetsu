package app.shosetsu.android.ui.settings.sub

import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import app.shosetsu.android.common.ShosetsuSettings
import app.shosetsu.android.common.enums.MarkingTypes
import app.shosetsu.android.common.ext.context
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.ui.settings.SettingsSubController
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import com.github.doomsdayrs.apps.shosetsu.R
import org.kodein.di.generic.instance

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
class ViewSettings : SettingsSubController() {
	private val s: ShosetsuSettings by instance()
	private val iSettingRepository: ISettingsRepository by instance()

	override val viewTitleRes: Int = R.string.settings_view

	override val settings: List<SettingsItemData> by settingsList {
		spinnerSettingData(0) {
			title { R.string.marking_mode }
			onSpinnerItemSelected { _, _, position, _ ->
				when (position) {
					0 -> setReaderMarkingType(MarkingTypes.ONVIEW)
					1 -> setReaderMarkingType(MarkingTypes.ONSCROLL)
					else -> Log.e("MarkingMode", "UnknownType")
				}
			}

			arrayAdapter = ArrayAdapter(
					context!!,
					android.R.layout.simple_spinner_dropdown_item,
					resources!!.getStringArray(R.array.marking_names)
			)
		}
		numberPickerSettingData(1) {
			title { R.string.columns_of_novel_listing_p }
			description { (R.string.columns_zero_automatic) }
			numberValue { s::columnsInNovelsViewP }
			range { 0 to 10 }
		}
		numberPickerSettingData(2) {
			title { R.string.columns_of_novel_listing_h }
			description { (R.string.columns_zero_automatic) }
			numberValue { s::columnsInNovelsViewH }
			range { 0 to 10 }
		}
		spinnerSettingData(3) {
			title { R.string.novel_card_type_selector_title }
			description { R.string.novel_card_type_selector_desc }
			spinnerField { s::novelCardType }
			arrayAdapter = ArrayAdapter(
					context!!,
					android.R.layout.simple_spinner_dropdown_item,
					resources!!.getStringArray(R.array.novel_card_types)
			)
		}
		switchSettingData(4) {
			title { "Legacy navigation" }
			description { "Disableds bottom navigation, enables drawer" }
			isChecked = s.navigationStyle == 1
			var state = false
			onChecked { cb, isChecked ->
				if (state) {
					state = false
					return@onChecked
				}
				AlertDialog.Builder(this@ViewSettings.activity!!).apply {
					this.setMessage(R.string.need_restart)
					setPositiveButton(R.string.restart) { d, _ ->
						s.navigationStyle = if (isChecked) 1 else 0
						d.dismiss()
						this@ViewSettings.activity?.finish()
					}
					setNegativeButton(R.string.never_mind) { d, _ ->
						d.dismiss()
						state = true
						cb?.isChecked = !isChecked
					}
				}.show()
			}
		}
	}

	private fun setReaderMarkingType(markingType: MarkingTypes) {
	}
}