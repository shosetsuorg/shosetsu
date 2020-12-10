package app.shosetsu.android.view.uimodels.settings.dsl

import android.view.View
import app.shosetsu.android.view.uimodels.settings.base.TextRequiringSettingData

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
inline fun TextRequiringSettingData.text(value: TextRequiringSettingData.() -> Any): Unit =
		value().let {
			when (it) {
				is String -> textText = it
				is Int -> textID = it
				else -> throw IllegalArgumentException("Input must be either an int or string")
			}
		}

@SettingsItemDSL
inline fun TextRequiringSettingData.onClicked(
		crossinline action: TextRequiringSettingData.(
				@ParameterName("view") View,
		) -> Unit,
) {
	textViewOnClickListener = { action(it) }
}