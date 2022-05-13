package app.shosetsu.android.domain.repository.base

import app.shosetsu.android.common.SettingKey
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
interface ISettingsRepository {

	fun getLongFlow(key: SettingKey<Long>): Flow<Long>

	fun getStringFlow(key: SettingKey<String>): Flow<String>

	fun getIntFlow(key: SettingKey<Int>): Flow<Int>

	fun getBooleanFlow(key: SettingKey<Boolean>): Flow<Boolean>

	fun getFloatFlow(key: SettingKey<Float>): Flow<Float>

	fun getStringSetFlow(key: SettingKey<Set<String>>): Flow<Set<String>>


	suspend fun getLong(key: SettingKey<Long>): Long

	suspend fun getString(key: SettingKey<String>): String

	suspend fun getInt(key: SettingKey<Int>): Int

	suspend fun getBoolean(key: SettingKey<Boolean>): Boolean

	suspend fun getStringSet(key: SettingKey<Set<String>>): Set<String>

	suspend fun getFloat(key: SettingKey<Float>): Float


	suspend fun setLong(key: SettingKey<Long>, value: Long)

	suspend fun setString(key: SettingKey<String>, value: String)

	suspend fun setInt(key: SettingKey<Int>, value: Int)

	suspend fun setBoolean(key: SettingKey<Boolean>, value: Boolean)

	suspend fun setStringSet(key: SettingKey<Set<String>>, value: Set<String>)

	suspend fun setFloat(key: SettingKey<Float>, value: Float)
}