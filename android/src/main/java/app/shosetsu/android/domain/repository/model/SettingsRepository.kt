package app.shosetsu.android.domain.repository.model

import app.shosetsu.common.com.consts.settings.SettingKey
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.datasource.file.base.IFileSettingsDataSource
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
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
 * 18 / 09 / 2020
 */
class SettingsRepository(
		private val iLocalSettingsDataSource: IFileSettingsDataSource
) : ISettingsRepository {

	override fun observeLong(key: SettingKey<Long>): Flow<Long> =
			iLocalSettingsDataSource.observeLong(key)

	override fun observeString(key: SettingKey<String>): Flow<String> =
			iLocalSettingsDataSource.observeString(key)

	override fun observeInt(key: SettingKey<Int>): Flow<Int> =
			iLocalSettingsDataSource.observeInt(key)

	override fun observeBoolean(key: SettingKey<Boolean>): Flow<Boolean> =
			iLocalSettingsDataSource.observeBoolean(key)

	override fun observeStringSet(key: SettingKey<Set<String>>): Flow<Set<String>> =
			iLocalSettingsDataSource.observeStringSet(key)

	override fun observeFloat(key: SettingKey<Float>): Flow<Float> =
			iLocalSettingsDataSource.observeFloat(key)

	override suspend fun getLong(key: SettingKey<Long>): HResult<Long> =
			iLocalSettingsDataSource.getLong(key)

	override suspend fun getString(key: SettingKey<String>): HResult<String> =
			iLocalSettingsDataSource.getString(key)

	override suspend fun getInt(key: SettingKey<Int>): HResult<Int> =
			iLocalSettingsDataSource.getInt(key)


	override suspend fun getBoolean(key: SettingKey<Boolean>): HResult<Boolean> =
			iLocalSettingsDataSource.getBoolean(key)

	override suspend fun getStringSet(key: SettingKey<Set<String>>): HResult<Set<String>> =
			iLocalSettingsDataSource.getStringSet(key)

	override suspend fun getFloat(key: SettingKey<Float>) =
			iLocalSettingsDataSource.getFloat(key)

	override suspend fun setLong(key: SettingKey<Long>, value: Long) =
			iLocalSettingsDataSource.setLong(key, value)

	override suspend fun setString(key: SettingKey<String>, value: String) =
			iLocalSettingsDataSource.setString(key, value)

	override suspend fun setInt(key: SettingKey<Int>, value: Int) =
			iLocalSettingsDataSource.setInt(key, value)

	override suspend fun setBoolean(key: SettingKey<Boolean>, value: Boolean) =
			iLocalSettingsDataSource.setBoolean(key, value)

	override suspend fun setStringSet(key: SettingKey<Set<String>>, value: Set<String>) =
			iLocalSettingsDataSource.setStringSet(key, value)

	override suspend fun setFloat(key: SettingKey<Float>, value: Float) =
			iLocalSettingsDataSource.setFloat(key, value)
}