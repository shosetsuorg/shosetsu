package com.github.doomsdayrs.apps.shosetsu.domain.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IExtensionsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.model.NovelsRepository
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.IDTitleImageBookUI

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
class LoadCatalogueData(
		val extensionRepository: IExtensionsRepository,
		val novelsRepository: NovelsRepository
) : ((
		@ParameterName("formatter") Formatter,
		@ParameterName("currentPage") Int
) -> LiveData<HResult<IDTitleImageBookUI>>) {
	override fun invoke(
			formatter: Formatter,
			currentPage: Int
	): LiveData<HResult<IDTitleImageBookUI>> = liveData {
		emit(loading())
		emitSource(extensionRepository.loadCatalogueData(formatter, 0, currentPage, arrayOf<Any>()).switchMap {
			when (it) {
				is HResult.Success -> {
					novelsRepository.updateNovel(it.data.convertTo(formatter))
					successResult()
				}
				is HResult.Error -> it
				is HResult.Loading -> it
				is HResult.Empty -> it
			}
		})

	}

	private fun Novel.Listing.convertTo(formatter: Formatter): NovelEntity = NovelEntity(
			url = this.link,
			imageURL = this.imageURL,
			title = this.title,
			formatterID = formatter.formatterID
	)
}