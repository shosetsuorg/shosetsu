package app.shosetsu.android.ui.settings.sub

import android.content.res.Resources
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import app.shosetsu.android.common.ext.toast
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.settings.SettingsSubController
import app.shosetsu.android.view.uimodels.settings.ButtonSettingData
import app.shosetsu.android.view.uimodels.settings.SpinnerSettingData
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.onButtonClicked
import app.shosetsu.android.view.uimodels.settings.dsl.onSpinnerItemSelected
import app.shosetsu.android.view.uimodels.settings.dsl.title
import app.shosetsu.android.viewmodel.abstracted.settings.AAdvancedSettingsViewModel
import app.shosetsu.android.viewmodel.model.settings.AdvancedSettingsViewModel
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
 */

/**
 * Shosetsu
 * 13 / 07 / 2019
 */
class AdvancedSettings : SettingsSubController() {
	override val viewModel: AAdvancedSettingsViewModel by viewModel()
	override val viewTitleRes: Int = R.string.settings_advanced


	override val adjustments: List<SettingsItemData>.() -> Unit = {
		find<SpinnerSettingData>(1).onSpinnerItemSelected { adapterView, _, position, _ ->
			if (position in 0..1) {
				val delegate = (activity as AppCompatActivity).delegate
				when (position) {
					0 -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
					1 -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
				}
				val theme = delegate.localNightMode
				adapterView?.setSelection(if (
						theme == AppCompatDelegate.MODE_NIGHT_YES ||
						theme == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM ||
						theme == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
				) 1 else 0)
			}
		}
	}

	@Throws(Resources.NotFoundException::class)
	override fun onViewCreated(view: View) {
		/*
		val theme = (activity as AppCompatActivity).delegate.localNightMode
		(settings[0] as SpinnerSettingData).spinnerSelection = (if (
				theme == MODE_NIGHT_YES ||
				theme == MODE_NIGHT_FOLLOW_SYSTEM ||
				theme == MODE_NIGHT_AUTO_BATTERY)
			1 else 0)

		if (BuildConfig.DEBUG && findDataByID(9) == -1)
			settings.add(switchSettingData(9) {
				title { "Show Intro" }
				checker { s::showIntro }
			})
		 */
		super.onViewCreated(view)
	}
}