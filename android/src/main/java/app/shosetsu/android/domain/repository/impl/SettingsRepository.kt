package app.shosetsu.android.domain.repository.impl

import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.datasource.file.base.IFileSettingsDataSource
import app.shosetsu.android.domain.repository.base.ISettingsRepository
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

	override fun getLongFlow(key: SettingKey<Long>): Flow<Long> =
		iLocalSettingsDataSource.observeLong(DEFAULT_NAME, key)

	override fun getStringFlow(key: SettingKey<String>): Flow<String> =
		iLocalSettingsDataSource.observeString(DEFAULT_NAME, key)

	override fun getIntFlow(key: SettingKey<Int>): Flow<Int> =
		iLocalSettingsDataSource.observeInt(DEFAULT_NAME, key)

	override fun getBooleanFlow(key: SettingKey<Boolean>): Flow<Boolean> =
		iLocalSettingsDataSource.observeBoolean(DEFAULT_NAME, key)

	override fun getStringSetFlow(key: SettingKey<Set<String>>): Flow<Set<String>> =
		iLocalSettingsDataSource.observeStringSet(DEFAULT_NAME, key)

	override fun getFloatFlow(key: SettingKey<Float>): Flow<Float> =
		iLocalSettingsDataSource.observeFloat(DEFAULT_NAME, key)

	override suspend fun getLong(key: SettingKey<Long>): Long =
		iLocalSettingsDataSource.getLong(DEFAULT_NAME, key)

	override suspend fun getString(key: SettingKey<String>): String =
		iLocalSettingsDataSource.getString(DEFAULT_NAME, key)

	override suspend fun getInt(key: SettingKey<Int>): Int =
		iLocalSettingsDataSource.getInt(DEFAULT_NAME, key)

	override suspend fun getBoolean(key: SettingKey<Boolean>): Boolean =
		iLocalSettingsDataSource.getBoolean(DEFAULT_NAME, key)

	override suspend fun getStringSet(key: SettingKey<Set<String>>): Set<String> =
		iLocalSettingsDataSource.getStringSet(DEFAULT_NAME, key)

	override suspend fun getFloat(key: SettingKey<Float>) =
		iLocalSettingsDataSource.getFloat(DEFAULT_NAME, key)

	override suspend fun setLong(key: SettingKey<Long>, value: Long): Unit =
		iLocalSettingsDataSource.setLong(DEFAULT_NAME, key, value)

	override suspend fun setString(key: SettingKey<String>, value: String): Unit =
		iLocalSettingsDataSource.setString(DEFAULT_NAME, key, value)

	override suspend fun setInt(key: SettingKey<Int>, value: Int): Unit =
		iLocalSettingsDataSource.setInt(DEFAULT_NAME, key, value)

	override suspend fun setBoolean(key: SettingKey<Boolean>, value: Boolean): Unit =
		iLocalSettingsDataSource.setBoolean(DEFAULT_NAME, key, value)

	override suspend fun setStringSet(
		key: SettingKey<Set<String>>,
		value: Set<String>
	): Unit =
		iLocalSettingsDataSource.setStringSet(DEFAULT_NAME, key, value)

	override suspend fun setFloat(key: SettingKey<Float>, value: Float): Unit =
		iLocalSettingsDataSource.setFloat(DEFAULT_NAME, key, value)

	companion object {
		private const val DEFAULT_NAME = "settings"
	}
}