package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.content.res.Resources
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsSubController
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType.SWITCH
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ISettingsAdvancedViewModel


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
	val viewModel: ISettingsAdvancedViewModel by viewModel()
	override val settings: ArrayList<SettingsItemData> by lazy { viewModel.settings }

	@Throws(Resources.NotFoundException::class)
	override fun onViewCreated(view: View) {
		val theme = (activity as AppCompatActivity).delegate.localNightMode
		settings[0].setSpinnerSelection(if (
				theme == MODE_NIGHT_YES ||
				theme == MODE_NIGHT_FOLLOW_SYSTEM ||
				theme == MODE_NIGHT_AUTO_BATTERY)
			1 else 0)
		if (BuildConfig.DEBUG)
			settings.add(SettingsItemData(SWITCH, 9)
					.setTitle("Show Intro")
					.setIsChecked(Settings.showIntro)
					.setOnCheckedListner(CompoundButton.OnCheckedChangeListener { _, isChecked ->
						Settings.showIntro = isChecked
					})
			)
		super.onViewCreated(view)
	}


}