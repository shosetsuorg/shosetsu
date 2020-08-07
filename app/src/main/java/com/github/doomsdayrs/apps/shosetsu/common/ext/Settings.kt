package com.github.doomsdayrs.apps.shosetsu.common.ext

import android.content.Context
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings

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


fun Context.getIntSetting(formatter: Formatter, settingID: Int): Int? =
		formatter.settingsModel.find { it.id == settingID }?.let {
			getSharedPreferences("FORMATTER-${formatter.formatterID}", 0)
					.getInt("$settingID", it.state as Int)
		}

fun Context.getBooleanSetting(formatter: Formatter, settingID: Int): Boolean? =
		formatter.settingsModel.find { it.id == settingID }?.let {
			getSharedPreferences("FORMATTER-${formatter.formatterID}", 0)
					.getBoolean("$settingID", it.state as Boolean)
		}

fun Context.getDoubleSetting(formatter: Formatter, settingID: Int): Float? =
		formatter.settingsModel.find { it.id == settingID }?.let {
			getSharedPreferences("FORMATTER-${formatter.formatterID}", 0)
					.getFloat("$settingID", it.state as Float)
		}

fun Context.getStringSetting(formatter: Formatter, settingID: Int): String? =
		formatter.settingsModel.find { it.id == settingID }?.let {
			getSharedPreferences("FORMATTER-${formatter.formatterID}", 0)
					.getString("$settingID", it.state as String)
		}


inline fun <reified I : Any> ShosetsuSettings.getFormSetting(formatter: Formatter, settingID: Int): I? {
	return when (I::class) {
		Int::class -> context.getIntSetting(formatter, settingID)
		String::class -> context.getStringSetting(formatter, settingID)
		Boolean::class -> context.getBooleanSetting(formatter, settingID)
		Double::class -> context.getDoubleSetting(formatter, settingID)
		else -> null
	} as I?
}

inline fun <reified I : Any> ShosetsuSettings.setFormSetting(
		formatter: Formatter,
		settingID: Int,
		v: I
) {
	val s = "$settingID"
	when (I::class) {
		Int::class ->
			context.getSharedPreferences("FORMATTER-${formatter.formatterID}", 0)
					.edit().putInt(s, v as Int).apply()
		String::class ->
			context.getSharedPreferences("FORMATTER-${formatter.formatterID}", 0)
					.edit().putString(s, v as String).apply()
		Boolean::class ->
			context.getSharedPreferences("FORMATTER-${formatter.formatterID}", 0)
					.edit().putBoolean(s, v as Boolean).apply()
		Double::class ->
			context.getSharedPreferences("FORMATTER-${formatter.formatterID}", 0)
					.edit().putFloat(s, (v as Double).toFloat()).apply()
	}
}