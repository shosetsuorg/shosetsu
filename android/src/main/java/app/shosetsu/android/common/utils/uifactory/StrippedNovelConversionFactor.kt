package app.shosetsu.android.common.utils.uifactory

import app.shosetsu.android.domain.model.local.StrippedBookmarkedNovelEntity
import app.shosetsu.android.view.uimodels.model.IDTitleImageUI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

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
 * 21 / 12 / 2020
 */
class StrippedNovelConversionFactory(data: StrippedBookmarkedNovelEntity) :
	UIConversionFactory<StrippedBookmarkedNovelEntity, IDTitleImageUI>(data) {
	override fun StrippedBookmarkedNovelEntity.convertTo(): IDTitleImageUI =
		IDTitleImageUI(id, title, imageURL)

}

fun List<StrippedBookmarkedNovelEntity>.mapToFactory() =
	map { StrippedNovelConversionFactory(it) }

@ExperimentalCoroutinesApi
fun Flow<List<StrippedBookmarkedNovelEntity>>.mapLatestToResultFlowWithFactory() =
	mapLatest { it.mapToFactory() }
