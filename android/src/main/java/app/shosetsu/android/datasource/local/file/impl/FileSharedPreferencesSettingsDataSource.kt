package app.shosetsu.android.datasource.local.file.impl

import app.shosetsu.android.providers.prefrences.SharedPreferenceProvider
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.datasource.file.base.IFileSettingsDataSource
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.successResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

	@ExperimentalCoroutinesApi
	override fun observeLong(name: String, key: SettingKey<Long>): Flow<Long> =
		provider.observeLong(name, key)

	@ExperimentalCoroutinesApi
	override fun observeString(name: String, key: SettingKey<String>): Flow<String> =
		provider.observeString(name, key)

	@ExperimentalCoroutinesApi
	override fun observeInt(name: String, key: SettingKey<Int>): Flow<Int> =
		provider.observeInt(name, key)

	@ExperimentalCoroutinesApi
	override fun observeBoolean(name: String, key: SettingKey<Boolean>): Flow<Boolean> =
		provider.observeBoolean(name, key)

	@ExperimentalCoroutinesApi
	override fun observeStringSet(name: String, key: SettingKey<Set<String>>): Flow<Set<String>> =
		provider.observeStringSet(name, key)

	@ExperimentalCoroutinesApi
	override fun observeFloat(name: String, key: SettingKey<Float>): Flow<Float> =
		provider.observeFloat(name, key)

	@ExperimentalCoroutinesApi
	override suspend fun getLong(name: String, key: SettingKey<Long>): HResult<Long> =
		successResult(provider.getLong(name, key))

	@ExperimentalCoroutinesApi
	override suspend fun getString(name: String, key: SettingKey<String>): HResult<String> =
		successResult(provider.getString(name, key))

	@ExperimentalCoroutinesApi
	override suspend fun getInt(name: String, key: SettingKey<Int>): HResult<Int> =
		successResult(provider.getInt(name, key))


	@ExperimentalCoroutinesApi
	override suspend fun getBoolean(name: String, key: SettingKey<Boolean>): HResult<Boolean> =
		successResult(provider.getBoolean(name, key))

	@ExperimentalCoroutinesApi
	override suspend fun getStringSet(
		name: String,
		key: SettingKey<Set<String>>
	): HResult<Set<String>> =
		successResult(provider.getStringSet(name, key))

	@ExperimentalCoroutinesApi
	override suspend fun getFloat(name: String, key: SettingKey<Float>) =
		successResult(provider.getFloat(name, key))

	@ExperimentalCoroutinesApi
	override suspend fun setLong(name: String, key: SettingKey<Long>, value: Long): HResult<*> =
		successResult(provider.setLong(name, key, value))

	@ExperimentalCoroutinesApi
	override suspend fun setString(
		name: String,
		key: SettingKey<String>,
		value: String
	): HResult<*> =
		successResult(provider.setString(name, key, value))

	@ExperimentalCoroutinesApi
	override suspend fun setInt(name: String, key: SettingKey<Int>, value: Int): HResult<*> =
		successResult(provider.setInt(name, key, value))

	@ExperimentalCoroutinesApi
	override suspend fun setBoolean(
		name: String,
		key: SettingKey<Boolean>,
		value: Boolean
	): HResult<*> =
		successResult(provider.setBoolean(name, key, value))

	@ExperimentalCoroutinesApi
	override suspend fun setStringSet(
		name: String,
		key: SettingKey<Set<String>>,
		value: Set<String>
	): HResult<*> =
		successResult(provider.setStringSet(name, key, value))

	@ExperimentalCoroutinesApi
	override suspend fun setFloat(name: String, key: SettingKey<Float>, value: Float): HResult<*> =
		successResult(provider.setFloat(name, key, value))

}