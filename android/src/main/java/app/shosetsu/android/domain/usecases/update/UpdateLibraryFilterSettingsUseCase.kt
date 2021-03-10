package app.shosetsu.android.domain.usecases.update

import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.model.local.LibrarySortFilterEntity
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
 * 09 / 03 / 2021
 */
class UpdateLibraryFilterSettingsUseCase(
	private val iSettingsRepository: ISettingsRepository
) {
	suspend operator fun invoke(librarySortFilterEntity: LibrarySortFilterEntity) {
		iSettingsRepository.setString(
			SettingKey.LibraryFilter,
			Json { }.encodeToString(librarySortFilterEntity)
		)
	}
}