package app.shosetsu.android.providers.prefrences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import app.shosetsu.common.consts.settings.SettingKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

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
 * shosetsu
 * 17 / 09 / 2020
 * This class contains all SharedPrefrenceData for shosetsu
 */
class SharedPreferenceProvider(
	/** Application context for internal use */
	val context: Context,
	val settings: SharedPreferences = context.getSharedPreferences("view", 0),
) : SharedPreferences.OnSharedPreferenceChangeListener {
	private val longMap: HashMap<SettingKey<Long>, MutableStateFlow<Long>> by lazy { hashMapOf() }
	private val stringMap: HashMap<SettingKey<String>, MutableStateFlow<String>> by lazy { hashMapOf() }
	private val intMap: HashMap<SettingKey<Int>, MutableStateFlow<Int>> by lazy { hashMapOf() }
	private val booleanMap: HashMap<SettingKey<Boolean>, MutableStateFlow<Boolean>> by lazy { hashMapOf() }
	private val stringSetMap: HashMap<SettingKey<Set<String>>, MutableStateFlow<Set<String>>> by lazy { hashMapOf() }
	private val floatMap: HashMap<SettingKey<Float>, MutableStateFlow<Float>> by lazy { hashMapOf() }

	override fun onSharedPreferenceChanged(sp: SharedPreferences?, s: String) {
		val key = SettingKey.valueOf(s)
		when (key.default) {
			is String -> {
				val key = key as SettingKey<String>
				val value = getString(key)
				stringMap[key]?.let { it.value = value } ?: MutableStateFlow(value).also {
					stringMap[key] = it
				}
			}
			is Int -> {
				val key = key as SettingKey<Int>
				val value = getInt(key)
				intMap[key]?.let { it.value = value } ?: MutableStateFlow(value).also {
					intMap[key] = it
				}
			}
			is Boolean -> {
				val key = key as SettingKey<Boolean>
				val value = getBoolean(key)
				booleanMap[key]?.let { it.value = value } ?: MutableStateFlow(value).also {
					booleanMap[key] = it
				}
			}
			is Long -> {
				val key = key as SettingKey<Long>
				val value = getLong(key)
				longMap[key]?.let { it.value = value } ?: MutableStateFlow(value).also {
					longMap[key] = it
				}
			}
			is Float -> {
				val key = key as SettingKey<Float>
				val value = getFloat(key)
				floatMap[key]?.let { it.value = value } ?: MutableStateFlow(value).also {
					floatMap[key] = it
				}
			}
			is Set<*> -> {
				val key = key as SettingKey<Set<String>>
				val value = getStringSet(key)
				stringSetMap[key]?.let { it.value = value } ?: MutableStateFlow(value).also {
					stringSetMap[key] = it
				}
			}
		}
	}

	init {
		settings.registerOnSharedPreferenceChangeListener(this)
	}

	fun observeLong(key: SettingKey<Long>): Flow<Long> =
		longMap[key] ?: MutableStateFlow(getLong(key)).also {
			longMap[key] = it
		}

	fun observeString(key: SettingKey<String>): Flow<String> =
		stringMap[key] ?: MutableStateFlow(getString(key)).also {
			stringMap[key] = it
		}

	fun observeInt(key: SettingKey<Int>): Flow<Int> =
		intMap[key] ?: MutableStateFlow(getInt(key)).also {
			intMap[key] = it
		}

	fun observeBoolean(key: SettingKey<Boolean>): Flow<Boolean> =
		booleanMap[key] ?: MutableStateFlow(getBoolean(key)).also {
			booleanMap[key] = it
		}

	fun observeStringSet(key: SettingKey<Set<String>>): Flow<Set<String>> =
		stringSetMap[key] ?: MutableStateFlow(getStringSet(key)).also {
			stringSetMap[key] = it
		}

	fun observeFloat(key: SettingKey<Float>): Flow<Float> =
		floatMap[key] ?: MutableStateFlow(getFloat(key)).also {
			floatMap[key] = it
		}

	fun getLong(key: SettingKey<Long>): Long =
		settings.getLong(key.name, key.default)

	fun getString(key: SettingKey<String>): String =
		settings.getString(key.name, key.default) ?: ""

	fun getInt(key: SettingKey<Int>): Int =
		settings.getInt(key.name, key.default)

	fun getBoolean(key: SettingKey<Boolean>): Boolean =
		settings.getBoolean(key.name, key.default)

	fun getStringSet(key: SettingKey<Set<String>>): Set<String> =
		settings.getStringSet(key.name, key.default) ?: setOf()

	fun getFloat(key: SettingKey<Float>): Float =
		try {
			settings.getFloat(key.name, key.default)
		} catch (e: ClassCastException) {
			setFloat(key, key.default)
			key.default
		}

	fun setLong(key: SettingKey<Long>, value: Long): Unit =
		settings.edit { putLong(key.name, value) }

	fun setString(key: SettingKey<String>, value: String): Unit =
		settings.edit { putString(key.name, value) }

	fun setInt(key: SettingKey<Int>, value: Int): Unit =
		settings.edit { putInt(key.name, value) }

	fun setBoolean(key: SettingKey<Boolean>, value: Boolean): Unit =
		settings.edit { putBoolean(key.name, value) }

	fun setStringSet(key: SettingKey<Set<String>>, value: Set<String>): Unit =
		settings.edit { putStringSet(key.name, value) }

	fun setFloat(key: SettingKey<Float>, value: Float): Unit =
		settings.edit { putFloat(key.name, value) }

}


