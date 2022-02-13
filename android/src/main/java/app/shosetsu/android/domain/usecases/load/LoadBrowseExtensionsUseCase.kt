package app.shosetsu.android.domain.usecases.load

import app.shosetsu.common.domain.model.local.BrowseExtensionEntity
import app.shosetsu.common.domain.repositories.base.IExtensionDownloadRepository
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.enums.DownloadStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

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
 * 13 / 05 / 2020
 */
class LoadBrowseExtensionsUseCase(
	private val extensionsRepository: IExtensionsRepository,
	private val extensionDownloadRepository: IExtensionDownloadRepository
) {
	@ExperimentalCoroutinesApi
	operator fun invoke(): Flow<List<BrowseExtensionEntity>> = flow {
		val flow = extensionsRepository.loadBrowseExtensions()
			.transformLatest { extensionList -> // Merge with downloadStatus
				val listOfFlows: List<Flow<BrowseExtensionEntity?>> =
					extensionList.map { it to extensionDownloadRepository.getStatusFlow(it.id) }
						.map { (extensionUI, statusFlow) ->
							statusFlow.transform { status ->
								emit(null) // I am smart
								emit(
									extensionUI.copy(
										isInstalling = status == DownloadStatus.PENDING || status == DownloadStatus.DOWNLOADING
									)
								)
							}
						}

				// Merge the flows
				val b: Flow<List<BrowseExtensionEntity>> =
					combine(*listOfFlows.toTypedArray()) { it.filterNotNull().toList() }

				// Emit as a success
				emitAll(b)
			}
		emitAll(flow)
	}
}