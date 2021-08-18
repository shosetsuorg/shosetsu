package app.shosetsu.android.viewmodel.impl

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.domain.usecases.get.GetNovelUIUseCase
import app.shosetsu.android.domain.usecases.load.LoadExtensionsUIUseCase
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.view.uimodels.model.MigrationNovelUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.abstracted.AMigrationViewModel
import app.shosetsu.common.domain.model.local.StrippedBookmarkedNovelEntity
import app.shosetsu.common.dto.*
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
 * Shosetsu
 *
 * @since 04 / 08 / 2021
 * @author Doomsdayrs
 */
class MigrationViewModel(
	private val getNovelUI: GetNovelUIUseCase,
	private val loadExtensionsFlow: LoadExtensionsUIUseCase
) : AMigrationViewModel() {
	private val novelIds: MutableStateFlow<IntArray> = MutableStateFlow<IntArray>(intArrayOf())

	/**
	 * Map of novel id to which extension is selected
	 */
	private val selectedExtensionMap = hashMapOf<Int, MutableStateFlow<Int>>()

	@ExperimentalCoroutinesApi
	override val extensions: LiveData<HResult<List<ExtensionUI>>> by lazy {
		flow {
			emitAll(loadExtensionsFlow().transform { extensionsResult ->
				extensionsResult.handle(
					onEmpty = {
						emit(empty)
					},
					onError = {
						emit(it)
					},
					onLoading = {
						emit(loading)
					}
				) { mExtensions ->
					emitAll(
						whichFlow.transformLatest { selectedId ->
							emitAll(
								selectedExtensionMap.getOrPut(selectedId) {
									MutableStateFlow(mExtensions.firstOrNull()?.id ?: -1)
								}
									.transformLatest<Int, HResult<List<ExtensionUI>>> { selectedExtension ->
										emit(
											successResult(mExtensions.map { extension ->
												extension.apply {
													extension.isSelected =
														selectedExtension == extension.id
												}
											})
										)
									}
							)
						}
					)
				}
			})
		}.asIOLiveData()
	}

	@ExperimentalCoroutinesApi
	override val novels: LiveData<HResult<List<MigrationNovelUI>>> by lazy {
		novelIds.transformLatest { ids ->
			emitAll(
				ids.map {
					getNovelUI(it)
				}.combineResults()
			)
		}.mapLatest { result ->
			result.transformToSuccess { list ->
				list.map {
					MigrationNovelUI(it.id, it.title, it.imageURL)
				}
			}
		}.combine(whichFlow) { list, id ->
			val result = list.transformToSuccess {
				it.map { novelUI ->
					novelUI.copy(
						isSelected = novelUI.id == id
					)
				}
			}
			logV("New list: $result")
			result
		}.asIOLiveData()
	}

	/**
	 * Which novel is being worked on rn
	 *
	 * Contains the novelId
	 */
	private val whichFlow: MutableStateFlow<Int> by lazy {
		MutableStateFlow(novelIds.value.firstOrNull() ?: -1)
	}

	override val which: LiveData<Int>
		get() = whichFlow.asIOLiveData()

	override fun setWorkingOn(novelId: Int) {
		logI("Now working on $novelId")
		whichFlow.value = novelId
	}

	override fun getResults(novelUI: NovelUI): LiveData<HResult<StrippedBookmarkedNovelEntity>> =
		flow {
			emit(loading)
		}.asIOLiveData()

	override fun setNovels(array: IntArray) {
		novelIds.value = array
	}

	override fun setSelectedExtension(extensionUI: ExtensionUI) {
		if (selectedExtensionMap.containsKey(whichFlow.value)) {
			selectedExtensionMap[whichFlow.value]?.tryEmit(extensionUI.id)
		} else {
			selectedExtensionMap[whichFlow.value] = MutableStateFlow(extensionUI.id)
		}
	}
}