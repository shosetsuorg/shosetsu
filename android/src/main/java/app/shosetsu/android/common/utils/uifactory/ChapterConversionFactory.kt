package app.shosetsu.android.common.utils.uifactory

import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.view.uimodels.model.ChapterUI
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
 * 05 / 12 / 2020
 */
class ChapterConversionFactory(data: ChapterEntity) :
	UIConversionFactory<ChapterEntity, ChapterUI>(data) {
	override fun ChapterEntity.convertTo(): ChapterUI = ChapterUI(
		id = id!!,
		novelID = novelID,
		link = url,
		extensionID = extensionID,
		title = title,
		releaseDate = releaseDate,
		order = order,
		readingPosition = readingPosition,
		readingStatus = readingStatus,
		bookmarked = bookmarked,
		isSaved = isSaved
	)
}

fun List<ChapterEntity>.mapToFactory() =
	map { ChapterConversionFactory(it) }

@OptIn(ExperimentalCoroutinesApi::class)
fun Flow<List<ChapterEntity>>.mapLatestToResultFlowWithFactory() =
	mapLatest { it.mapToFactory() }
