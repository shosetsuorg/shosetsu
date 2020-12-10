package app.shosetsu.android.common.utils.uifactory

import app.shosetsu.android.view.uimodels.model.DownloadUI
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handleReturn
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.domain.model.local.DownloadEntity
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
class DownloadConversionFactory(
		data: DownloadEntity
) : UIConversionFactory<DownloadEntity, DownloadUI>(data) {
	override fun DownloadEntity.convertTo(): DownloadUI = DownloadUI(
			chapterID,
			novelID,
			chapterURL,
			chapterName,
			novelName,
			extensionID,
			status
	)
}

fun List<DownloadEntity>.mapToFactory() =
		map { DownloadConversionFactory(it) }

fun HResult<List<DownloadEntity>>.mapResultWithFactory() =
		handleReturn { successResult(it.mapToFactory()) }

fun Flow<HResult<List<DownloadEntity>>>.mapLatestToResultFlowWithFactory() =
		mapLatest { it.mapResultWithFactory() }
