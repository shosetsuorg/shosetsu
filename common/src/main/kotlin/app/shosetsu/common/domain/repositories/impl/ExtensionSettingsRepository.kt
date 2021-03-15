package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.datasource.file.base.IFileSettingsDataSource
import app.shosetsu.common.domain.repositories.base.IExtensionSettingsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.mapLatestToSuccess
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
 * 11 / 03 / 2021
 */
class ExtensionSettingsRepository(
	private val iFileSettingSystem: IFileSettingsDataSource
) : IExtensionSettingsRepository {
	private fun selectedListingKey() = SettingKey.CustomInt("selectedListing", 0)

	override suspend fun getSelectedListing(extensionID: Int): HResult<Int> =
		iFileSettingSystem.getInt("$extensionID", selectedListingKey())

	@ExperimentalCoroutinesApi
	override suspend fun observeSelectedListing(extensionID: Int): Flow<HResult<Int>> =
		iFileSettingSystem.observeInt("$extensionID", selectedListingKey())
			.mapLatestToSuccess()

	override suspend fun setSelectedListing(extensionID: Int, selectedListing: Int): HResult<*> =
		iFileSettingSystem.setInt("$extensionID", selectedListingKey(), selectedListing)


}