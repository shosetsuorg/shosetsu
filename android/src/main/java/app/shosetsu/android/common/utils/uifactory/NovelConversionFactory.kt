package app.shosetsu.android.common.utils.uifactory

import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform
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
class NovelConversionFactory(data: NovelEntity) : UIConversionFactory<NovelEntity, NovelUI>(data) {
	override fun NovelEntity.convertTo(): NovelUI = NovelUI(
		id = id!!,
		novelURL = url,
		extID = extensionID,
		bookmarked = bookmarked,
		title = title,
		imageURL = imageURL,
		description = description,
		loaded = loaded,
		language = language,
		genres = genres.toList(),
		authors = authors.toList(),
		artists = artists.toList(),
		tags = tags.toList(),
		status = status
	)
}

fun List<NovelEntity>.mapToFactory() =
	map { NovelConversionFactory(it) }

fun HResult<List<NovelEntity>>.mapResultWithFactory() =
	transform { successResult(it.mapToFactory()) }

@ExperimentalCoroutinesApi
fun Flow<HResult<List<NovelEntity>>>.mapLatestToResultFlowWithFactory() =
	mapLatest { it.mapResultWithFactory() }
