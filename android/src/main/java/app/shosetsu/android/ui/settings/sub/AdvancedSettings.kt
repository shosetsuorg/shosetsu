package app.shosetsu.android.ui.settings.sub

import android.content.res.Resources
import android.view.View
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.settings.SettingsSubController
import app.shosetsu.android.viewmodel.abstracted.settings.AAdvancedSettingsViewModel
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