package com.github.doomsdayrs.apps.shosetsu.domain.usecases

import android.util.Log
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IExtensionsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.INovelsRepository
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
		val novelsRepository: INovelsRepository
) {
	suspend operator fun invoke(
			formatter: Formatter,
			currentPage: Int
	): HResult<List<IDTitleImageBookUI>> {
		val it = extensionRepository.loadCatalogueData(
				formatter,
				0,
				currentPage,
				mapOf()
		)
		return when (it) {
			is HResult.Success -> {
				val data = it.data
				successResult(data.map {
					it.convertTo(formatter)
				}.map { ne ->
					Log.d(logID(), "Converting $ne")
					novelsRepository.insertNovelReturnCard(ne).convertTo().also {
						Log.d(logID(), "Converted $it")
					}
				})
			}
			is HResult.Loading -> it
			is HResult.Error -> it
			is HResult.Empty -> it
		}
	}

	private fun Novel.Listing.convertTo(formatter: Formatter): NovelEntity = NovelEntity(
			url = this.link,
			imageURL = this.imageURL,
			title = this.title,
			formatterID = formatter.formatterID
	)
}