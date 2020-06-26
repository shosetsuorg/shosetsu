package com.github.doomsdayrs.apps.shosetsu.ui.settings.data.dsl

import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.SeekBarSettingData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.base.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.base.SettingsListBuilder

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
 * 26 / 06 / 2020
 */

@SettingsItemDSL
inline fun seekBarSettingData(
		id: Int,
		action: SeekBarSettingData.() -> Unit
): SettingsItemData = SeekBarSettingData(id).also(action)

@SettingsItemDSL
inline fun SettingsListBuilder.seekBarSettingData(
		id: Int,
		action: SeekBarSettingData.() -> Unit
): Unit = this.let { list.add(SeekBarSettingData(id).also(action)) }

@SettingsItemDSL
inline fun SeekBarSettingData.range(
		crossinline value: SeekBarSettingData.() -> Pair<
				@ParameterName("lowerBound") Float,
				@ParameterName("upperBound") Float
				>
) = value().let {
	min = it.first
	max = it.second
}