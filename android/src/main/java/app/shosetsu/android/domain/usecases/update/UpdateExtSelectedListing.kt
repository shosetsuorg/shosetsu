package app.shosetsu.android.domain.usecases.update

import app.shosetsu.android.domain.repository.base.IExtensionSettingsRepository

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
class UpdateExtSelectedListing(
	private val iExtensionSettingsRepository: IExtensionSettingsRepository
) {
	suspend operator fun invoke(extensionID: Int, selectedListing: Int) {
		if (extensionID == -1)
			return

		return iExtensionSettingsRepository.setSelectedListing(extensionID, selectedListing)
	}

}