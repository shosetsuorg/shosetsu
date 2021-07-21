package app.shosetsu.android.domain.usecases.get

import app.shosetsu.android.common.ext.convertTo
import app.shosetsu.android.domain.usecases.ConvertNCToCNUIUseCase
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.repositories.base.IExtensionSettingsRepository
import app.shosetsu.common.domain.repositories.base.INovelsRepository
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform
import app.shosetsu.common.dto.transmogrify
import app.shosetsu.lib.IExtension

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * shosetsu
 * 15 / 05 / 2020
 */
class GetCatalogueListingDataUseCase(
	private val novelsRepository: INovelsRepository,
	private val convertNCToCNUIUseCase: ConvertNCToCNUIUseCase,
	private val settingsRepo: ISettingsRepository,
	private val extSettingsRepo: IExtensionSettingsRepository
) {
	suspend operator fun invoke(
		iExtension: IExtension,
		data: Map<Int, Any>
	): HResult<List<ACatalogNovelUI>> =
		settingsRepo.getInt(SettingKey.SelectedNovelCardType).transform { cardType ->
			extSettingsRepo.getSelectedListing(iExtension.formatterID)
				.transform { selectedListing ->
					// Load catalogue data

					novelsRepository.getCatalogueData(
						iExtension,
						selectedListing,
						data
					).transform { list ->
						successResult(list.map { novelListing ->
							novelListing.convertTo(iExtension)
						}.mapNotNull { ne ->
						// For each, insert and return a stripped card
						// This operation is to pre-cache URL and ID so loading occurs smoothly
						novelsRepository.insertReturnStripped(ne).transmogrify { result ->
							convertNCToCNUIUseCase(result, cardType)
						}
					})
				}
			}
		}
}