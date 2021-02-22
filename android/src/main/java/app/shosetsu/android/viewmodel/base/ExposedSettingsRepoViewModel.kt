package app.shosetsu.android.viewmodel.base

import android.widget.NumberPicker
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.view.uimodels.settings.DoubleNumberSettingData
import app.shosetsu.android.view.uimodels.settings.NumberPickerSettingData
import app.shosetsu.android.view.uimodels.settings.SpinnerSettingData
import app.shosetsu.android.view.uimodels.settings.TextInputSettingData
import app.shosetsu.android.view.uimodels.settings.base.ToggleableStateSettingData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.repositories.base.*

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
 * 21 / 02 / 2021
 *
 * This is a view model that has extension functions for [ISettingsRepository]
 * for creating setting items that interact with [SettingKey]s
 */
interface ExposedSettingsRepoViewModel {
	val settingsRepo: ISettingsRepository

	@SettingsItemDSL
	suspend fun NumberPickerSettingData.settingValue(
		key: SettingKey<Int>,
		action: suspend (
			@ParameterName("picker") NumberPicker?,
			@ParameterName("oldVal") Int,
			@ParameterName("newVal") Int,
		) -> Unit = { _, _, newVal ->
			settingsRepo.setInt(key, newVal)
		},
	) {
		initalValue { settingsRepo.getIntOrDefault(key) }
		onValueSelected { picker, oldVal, newVal ->
			launchIO { action(picker, oldVal, newVal) }
		}
	}

	/**
	 * Generic function for [SpinnerSettingData]
	 */
	@SettingsItemDSL
	suspend fun SpinnerSettingData.spinnerSettingValue(
		key: SettingKey<Int>
	) {
		spinnerValue { settingsRepo.getIntOrDefault(key) }
		onSpinnerItemSelected { _, _, position, _ ->
			launchIO {
				settingsRepo.setInt(key, position)
			}
		}
	}

	/**
	 * Generic function for [ToggleableStateSettingData]
	 */
	@SettingsItemDSL
	suspend fun ToggleableStateSettingData.checkSettingValue(
		key: SettingKey<Boolean>
	) {
		isChecked = settingsRepo.getBooleanOrDefault(key)
		onChecked { _, isChecked ->
			launchIO {
				settingsRepo.setBoolean(key, isChecked)
			}
		}
	}

	@SettingsItemDSL
	suspend fun TextInputSettingData.textSettingValue(
		key: SettingKey<String>
	) {
		initialText = settingsRepo.getStringOrDefault(key).also {
			logV("Value: $it")
		}
		doAfterTextChanged {
			launchIO {
				settingsRepo.setString(key, it.toString())
			}
		}
	}


	fun Int.orZero() = if (this == -1) 0 else this

	/**
	 * Set min and max before hand
	 */
	@SettingsItemDSL
	suspend fun DoubleNumberSettingData.settingValue(
		key: SettingKey<Float>
	) {
		settingsRepo.getFloatOrDefault(key).let { settingValue ->
			initialWhole = wholeSteps.indexOfFirst { it == settingValue.toInt() }.orZero()
			val decimal: Int = ((settingValue % 1) * 100).toInt()
			initialDecimal = decimalSteps.indexOfFirst { it == decimal }.orZero()
		}
		onValueSelected {
			launchIO {
				settingsRepo.setFloat(key, it.toFloat())
			}
		}
	}
}

@SettingsItemDSL
inline fun SpinnerSettingData.spinnerValue(
	value: SpinnerSettingData.() -> Int,
): Unit = value().let {
	spinnerSelection = it
}