package app.shosetsu.android.datasource.file.impl

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
	override fun observeLong(key: SettingKey<Long>): Flow<Long> =
		provider.observeLong(key)

	@ExperimentalCoroutinesApi
	override fun observeString(key: SettingKey<String>): Flow<String> =
		provider.observeString(key)

	@ExperimentalCoroutinesApi
	override fun observeInt(key: SettingKey<Int>): Flow<Int> =
		provider.observeInt(key)

	@ExperimentalCoroutinesApi
	override fun observeBoolean(key: SettingKey<Boolean>): Flow<Boolean> =
		provider.observeBoolean(key)

	@ExperimentalCoroutinesApi
	override fun observeStringSet(key: SettingKey<Set<String>>): Flow<Set<String>> =
		provider.observeStringSet(key)

	@ExperimentalCoroutinesApi
	override fun observeFloat(key: SettingKey<Float>): Flow<Float> =
		provider.observeFloat(key)

	@ExperimentalCoroutinesApi
	override suspend fun getLong(key: SettingKey<Long>): HResult<Long> =
		successResult(provider.getLong(key))

	@ExperimentalCoroutinesApi
	override suspend fun getString(key: SettingKey<String>): HResult<String> =
		successResult(provider.getString(key))

	@ExperimentalCoroutinesApi
	override suspend fun getInt(key: SettingKey<Int>): HResult<Int> =
		successResult(provider.getInt(key))


	@ExperimentalCoroutinesApi
	override suspend fun getBoolean(key: SettingKey<Boolean>): HResult<Boolean> =
		successResult(provider.getBoolean(key))

	@ExperimentalCoroutinesApi
	override suspend fun getStringSet(key: SettingKey<Set<String>>): HResult<Set<String>> =
		successResult(provider.getStringSet(key))

	@ExperimentalCoroutinesApi
	override suspend fun getFloat(key: SettingKey<Float>) =
		successResult(provider.getFloat(key))

	@ExperimentalCoroutinesApi
	override suspend fun setLong(key: SettingKey<Long>, value: Long): HResult<*> =
		successResult(provider.setLong(key, value))

	@ExperimentalCoroutinesApi
	override suspend fun setString(key: SettingKey<String>, value: String): HResult<*> =
		successResult(provider.setString(key, value))

	@ExperimentalCoroutinesApi
	override suspend fun setInt(key: SettingKey<Int>, value: Int): HResult<*> =
		successResult(provider.setInt(key, value))

	@ExperimentalCoroutinesApi
	override suspend fun setBoolean(key: SettingKey<Boolean>, value: Boolean): HResult<*> =
		successResult(provider.setBoolean(key, value))

	@ExperimentalCoroutinesApi
	override suspend fun setStringSet(
		key: SettingKey<Set<String>>,
		value: Set<String>
	): HResult<*> =
		successResult(provider.setStringSet(key, value))

	@ExperimentalCoroutinesApi
	override suspend fun setFloat(key: SettingKey<Float>, value: Float): HResult<*> =
		successResult(provider.setFloat(key, value))

}