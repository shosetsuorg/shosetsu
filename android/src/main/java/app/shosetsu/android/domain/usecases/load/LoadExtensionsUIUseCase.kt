package app.shosetsu.android.domain.usecases.load

import app.shosetsu.android.common.utils.uifactory.mapLatestToResultFlowWithFactory
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.common.domain.repositories.base.IExtensionDownloadRepository
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.dto.*
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
class LoadExtensionsUIUseCase(
	private val extensionsRepository: IExtensionsRepository,
	private val extensionDownloadRepository: IExtensionDownloadRepository
) {
	@ExperimentalCoroutinesApi
	operator fun invoke(): Flow<HResult<List<ExtensionUI>>> = flow {
		loading()
		val flow = extensionsRepository.loadExtensionEntitiesFLow()
			.mapLatestToResultFlowWithFactory() // First convert to UI factories
			.mapLatestResultListTo() // Convert to UI entities
			.transformLatest { result -> // Merge with downloadStatus
				result.handle(
					onEmpty = {
						emit(empty)
					},
					onError = {
						emit(it)
					},
					onLoading = {
						emit(loading)
					}
				) { extensionList ->
					val listOfFlows: List<Flow<ExtensionUI?>> =
						extensionList.map { it to extensionDownloadRepository.getStatusFlow(it.id) }
							.map { (extensionUI, statusFlow) ->

								statusFlow.transform { statusResult ->
									if (statusResult is HResult.Success) {
										val status = statusResult.data
										emit(null) // I am smart
										emit(
											extensionUI.apply {
												isInstalling =
													status == DownloadStatus.PENDING || status == DownloadStatus.DOWNLOADING
											}
										)
									} else emit(extensionUI)
								}
							}

					// Merge the flows
					val b: Flow<List<ExtensionUI>> =
						combine(*listOfFlows.toTypedArray()) { it.filterNotNull().toList() }

					// Emit as a success
					emitAll(b.mapLatestToSuccess())
				}
			}
		emitAll(flow)
	}
}