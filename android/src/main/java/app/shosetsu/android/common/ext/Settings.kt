package app.shosetsu.android.common.ext

import android.content.Context
import app.shosetsu.lib.IExtension

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
 * 22 / 07 / 2020
 */


fun Context.getIntSetting(formatter: IExtension, settingID: Int): Int? =
	formatter.settingsModel.find { it.id == settingID }?.let {
		getSharedPreferences("FORMATTER-${formatter.formatterID}", 0)
			.getInt("$settingID", it.state as Int)
	}

fun Context.getBooleanSetting(formatter: IExtension, settingID: Int): Boolean? =
	formatter.settingsModel.find { it.id == settingID }?.let {
		getSharedPreferences("FORMATTER-${formatter.formatterID}", 0)
			.getBoolean("$settingID", it.state as Boolean)
	}

fun Context.getDoubleSetting(formatter: IExtension, settingID: Int): Float? =
	formatter.settingsModel.find { it.id == settingID }?.let {
		getSharedPreferences("FORMATTER-${formatter.formatterID}", 0)
			.getFloat("$settingID", it.state as Float)
	}

fun Context.getStringSetting(formatter: IExtension, settingID: Int): String? =
	formatter.settingsModel.find { it.id == settingID }?.let {
		getSharedPreferences("FORMATTER-${formatter.formatterID}", 0)
			.getString("$settingID", it.state as String)
	}
