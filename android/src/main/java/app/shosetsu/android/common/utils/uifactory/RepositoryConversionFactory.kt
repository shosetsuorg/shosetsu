package app.shosetsu.android.common.utils.uifactory

import app.shosetsu.android.domain.model.local.RepositoryEntity
import app.shosetsu.android.view.uimodels.model.RepositoryUI
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
class RepositoryConversionFactory(data: RepositoryEntity) :
	UIConversionFactory<RepositoryEntity, RepositoryUI>(data) {
	override fun RepositoryEntity.convertTo(): RepositoryUI = RepositoryUI(
		id,
		url,
		name,
		isEnabled
	)
}

fun List<RepositoryEntity>.mapToFactory() =
	map { RepositoryConversionFactory(it) }

@ExperimentalCoroutinesApi
fun Flow<List<RepositoryEntity>>.mapLatestToResultFlowWithFactory() =
	mapLatest { it.mapToFactory() }
