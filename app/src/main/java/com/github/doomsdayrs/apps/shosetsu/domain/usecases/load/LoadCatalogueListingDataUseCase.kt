package com.github.doomsdayrs.apps.shosetsu.domain.usecases.load

import app.shosetsu.lib.Formatter
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.convertTo
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IExtensionsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.INovelsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.ConvertNCToCNUIUseCase
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.catlog.ACatalogNovelUI

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
class LoadCatalogueListingDataUseCase(
		private val extensionRepository: IExtensionsRepository,
		private val novelsRepository: INovelsRepository,
		private val convertNCToCNUIUseCase: ConvertNCToCNUIUseCase,
) {
	suspend operator fun invoke(
			formatter: Formatter,
			currentPage: Int,
	): HResult<List<ACatalogNovelUI>> {
		return when (val it = extensionRepository.loadCatalogueData(
				formatter,
				0,
				currentPage,
				mapOf()
		)) {
			is HResult.Success -> {
				val data: List<Novel.Listing> = it.data
				successResult(data.map { novelListing ->
					novelListing.convertTo(formatter)
				}.mapNotNull { ne ->
					novelsRepository.insertNovelReturnCard(ne).let { result ->
						if (result is HResult.Success)
							convertNCToCNUIUseCase(result.data)
						else null
					}
				})
			}
			is HResult.Loading -> it
			is HResult.Error -> it
			is HResult.Empty -> it
		}
	}
}