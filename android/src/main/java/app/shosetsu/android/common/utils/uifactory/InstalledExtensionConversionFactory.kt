package app.shosetsu.android.common.utils.uifactory

import app.shosetsu.android.domain.model.local.InstalledExtensionEntity
import app.shosetsu.android.view.uimodels.model.InstalledExtensionUI
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
class InstalledExtensionConversionFactory(data: InstalledExtensionEntity) :
	UIConversionFactory<InstalledExtensionEntity, InstalledExtensionUI>(data) {
	override fun InstalledExtensionEntity.convertTo(): InstalledExtensionUI = InstalledExtensionUI(
		id = id,
		repoID = repoID,
		name = name,
		fileName = fileName,
		imageURL = imageURL,
		lang = lang,
		version = version,
		md5 = md5,
		type = type,
		enabled = enabled,
		chapterType = chapterType,
	)
}

fun List<InstalledExtensionEntity>.mapToFactory() =
	map { InstalledExtensionConversionFactory(it) }

@OptIn(ExperimentalCoroutinesApi::class)
fun Flow<List<InstalledExtensionEntity>>.mapLatestToResultFlowWithFactory() =
	mapLatest { it.mapToFactory() }
