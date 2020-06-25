package com.github.doomsdayrs.apps.shosetsu.ui.settings.data.dsl

import android.widget.CompoundButton
import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.base.TogglableStateSettingData
import kotlin.reflect.KMutableProperty0

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

@SettingsItemDSL
inline fun TogglableStateSettingData.checker(
		crossinline action: TogglableStateSettingData.() -> KMutableProperty0<Boolean>
) {
	val property = action()
	isChecked = property.get()
	onChecked { _: CompoundButton?, isChecked: Boolean ->
		property.set(isChecked)
	}
}

@SettingsItemDSL
inline fun TogglableStateSettingData.onChecked(crossinline action: TogglableStateSettingData.(
		@ParameterName("buttonView") CompoundButton?,
		@ParameterName("isChecked") Boolean
) -> Unit) {
	onCheckedListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
		action(buttonView, isChecked)
	}
}
