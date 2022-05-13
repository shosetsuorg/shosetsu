package app.shosetsu.android.domain.repository.impl

import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.android.datasource.file.base.IFileSettingsDataSource
import app.shosetsu.android.domain.repository.base.IExtensionSettingsRepository
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
 * 11 / 03 / 2021
 */
class ExtensionSettingsRepository(
	private val iFileSettingSystem: IFileSettingsDataSource
) : IExtensionSettingsRepository {
	private fun selectedListingKey() = CustomInt("selectedListing", 0)

	override suspend fun getSelectedListing(extensionID: Int): Int =
		iFileSettingSystem.getInt("$extensionID", selectedListingKey())

	override suspend fun observeSelectedListing(extensionID: Int): Flow<Int> =
		iFileSettingSystem.observeInt("$extensionID", selectedListingKey())

	override suspend fun setSelectedListing(extensionID: Int, selectedListing: Int): Unit =
		iFileSettingSystem.setInt("$extensionID", selectedListingKey(), selectedListing)


	override suspend fun getInt(extensionID: Int, settingID: Int, default: Int): Int =
		iFileSettingSystem.getInt("$extensionID", CustomInt("$settingID", default))

	override suspend fun getString(
		extensionID: Int,
		settingID: Int,
		default: String
	): String =
		iFileSettingSystem.getString("$extensionID", CustomString("$settingID", default))

	override suspend fun getBoolean(
		extensionID: Int,
		settingID: Int,
		default: Boolean
	): Boolean =
		iFileSettingSystem.getBoolean("$extensionID", CustomBoolean("$settingID", default))

	override suspend fun getFloat(
		extensionID: Int,
		settingID: Int,
		default: Float
	): Float =
		iFileSettingSystem.getFloat("$extensionID", CustomFloat("$settingID", default))


	override fun getIntFlow(
		extensionID: Int,
		settingID: Int,
		default: Int
	): Flow<Int> =
		iFileSettingSystem.observeInt("$extensionID", CustomInt("$settingID", default))

	override fun getStringFlow(
		extensionID: Int,
		settingID: Int,
		default: String
	): Flow<String> =
		iFileSettingSystem.observeString("$extensionID", CustomString("$settingID", default))

	override fun getBooleanFlow(
		extensionID: Int,
		settingID: Int,
		default: Boolean
	): Flow<Boolean> =
		iFileSettingSystem.observeBoolean("$extensionID", CustomBoolean("$settingID", default))

	override fun getFloatFlow(
		extensionID: Int,
		settingID: Int,
		default: Float
	): Flow<Float> =
		iFileSettingSystem.observeFloat("$extensionID", CustomFloat("$settingID", default))


	override suspend fun setInt(extensionID: Int, settingID: Int, value: Int): Unit =
		iFileSettingSystem.setInt("$extensionID", CustomInt("$settingID", 0), value)

	override suspend fun setString(extensionID: Int, settingID: Int, value: String): Unit =
		iFileSettingSystem.setString("$extensionID", CustomString("$settingID", ""), value)

	override suspend fun setBoolean(extensionID: Int, settingID: Int, value: Boolean): Unit =
		iFileSettingSystem.setBoolean("$extensionID", CustomBoolean("$settingID", false), value)

	override suspend fun setFloat(extensionID: Int, settingID: Int, value: Float): Unit =
		iFileSettingSystem.setFloat("$extensionID", CustomFloat("$settingID", 0f), value)
}