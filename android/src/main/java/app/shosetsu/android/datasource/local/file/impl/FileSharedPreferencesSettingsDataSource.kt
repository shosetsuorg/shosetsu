package app.shosetsu.android.datasource.local.file.impl

import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.providers.prefrences.SharedPreferenceProvider
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.android.datasource.file.base.IFileSettingsDataSource
import kotlinx.coroutines.flow.Flow

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
 */
class FileSharedPreferencesSettingsDataSource(
	private val provider: SharedPreferenceProvider
) : IFileSettingsDataSource {

	override fun observeLong(name: String, key: SettingKey<Long>): Flow<Long> =
		provider.observeLong(name, key)

	override fun observeString(name: String, key: SettingKey<String>): Flow<String> =
		provider.observeString(name, key)

	override fun observeInt(name: String, key: SettingKey<Int>): Flow<Int> =
		provider.observeInt(name, key)

	override fun observeBoolean(name: String, key: SettingKey<Boolean>): Flow<Boolean> =
		provider.observeBoolean(name, key)

	override fun observeStringSet(name: String, key: SettingKey<Set<String>>): Flow<Set<String>> =
		provider.observeStringSet(name, key)

	override fun observeFloat(name: String, key: SettingKey<Float>): Flow<Float> =
		provider.observeFloat(name, key)

	override suspend fun getLong(name: String, key: SettingKey<Long>): Long =
		(provider.getLong(name, key))

	override suspend fun getString(name: String, key: SettingKey<String>): String =
		(provider.getString(name, key))

	override suspend fun getInt(name: String, key: SettingKey<Int>): Int =
		(provider.getInt(name, key))

	override suspend fun getBoolean(name: String, key: SettingKey<Boolean>): Boolean =
		(provider.getBoolean(name, key))

	override suspend fun getStringSet(
		name: String,
		key: SettingKey<Set<String>>
	): Set<String> =
		(provider.getStringSet(name, key))

	override suspend fun getFloat(name: String, key: SettingKey<Float>) =
		(provider.getFloat(name, key))

	override suspend fun setLong(name: String, key: SettingKey<Long>, value: Long): Unit =
		(provider.setLong(name, key, value))

	override suspend fun setString(
		name: String,
		key: SettingKey<String>,
		value: String
	): Unit =
		(provider.setString(name, key, value))

	override suspend fun setInt(name: String, key: SettingKey<Int>, value: Int): Unit =
		(provider.setInt(name, key, value))

	override suspend fun setBoolean(
		name: String,
		key: SettingKey<Boolean>,
		value: Boolean
	): Unit =
		(provider.setBoolean(name, key, value))

	override suspend fun setStringSet(
		name: String,
		key: SettingKey<Set<String>>,
		value: Set<String>
	): Unit =
		(provider.setStringSet(name, key, value))

	override suspend fun setFloat(name: String, key: SettingKey<Float>, value: Float): Unit =
		(provider.setFloat(name, key, value))
}