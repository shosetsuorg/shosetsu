package app.shosetsu.android.datasource.file.model

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.consts.settings.SettingKey
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.datasource.file.base.IFileSettingsDataSource
import app.shosetsu.android.providers.prefrences.SharedPreferenceProvider

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
	override fun observeLong(key: SettingKey<Long>): LiveData<Long> =
			provider.observeLong(key)

	override fun observeString(key: SettingKey<String>): LiveData<String> =
			provider.observeString(key)

	override fun observeInt(key: SettingKey<Int>): LiveData<Int> =
			provider.observeInt(key)

	override fun observeBoolean(key: SettingKey<Boolean>): LiveData<Boolean> =
			provider.observeBoolean(key)

	override fun observeStringSet(key: SettingKey<Set<String>>): LiveData<Set<String>> =
			provider.observeStringSet(key)

	override fun observeFloat(key: SettingKey<Float>): LiveData<Float> =
			provider.observeFloat(key)

	override suspend fun getLong(key: SettingKey<Long>): HResult<Long> =
			successResult(provider.getLong(key))

	override suspend fun getString(key: SettingKey<String>): HResult<String> =
			successResult(provider.getString(key))

	override suspend fun getInt(key: SettingKey<Int>): HResult<Int> =
			successResult(provider.getInt(key))


	override suspend fun getBoolean(key: SettingKey<Boolean>): HResult<Boolean> =
			successResult(provider.getBoolean(key))

	override suspend fun getStringSet(key: SettingKey<Set<String>>): HResult<Set<String>> =
			successResult(provider.getStringSet(key))

	override suspend fun getFloat(key: SettingKey<Float>) =
			successResult(provider.getFloat(key))

	override suspend fun setLong(key: SettingKey<Long>, value: Long) =
			provider.setLong(key, value)

	override suspend fun setString(key: SettingKey<String>, value: String) =
			provider.setString(key, value)

	override suspend fun setInt(key: SettingKey<Int>, value: Int) =
			provider.setInt(key, value)

	override suspend fun setBoolean(key: SettingKey<Boolean>, value: Boolean) =
			provider.setBoolean(key, value)

	override suspend fun setStringSet(key: SettingKey<Set<String>>, value: Set<String>) =
			provider.setStringSet(key, value)

	override suspend fun setFloat(key: SettingKey<Float>, value: Float) =
			provider.setFloat(key, value)

}