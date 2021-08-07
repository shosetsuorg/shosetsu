package app.shosetsu.android.viewmodel.impl

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.domain.usecases.get.GetNovelUIUseCase
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
	private val getNovelUI: GetNovelUIUseCase
) : AMigrationViewModel() {
	private val novelIds: MutableStateFlow<IntArray> = MutableStateFlow<IntArray>(intArrayOf())

	override val extensions: LiveData<HResult<ExtensionUI>> by lazy {
		flow {
			emit(empty) // TODO
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
		}.combine(whichFlow) { list, selected ->
			val result = list.transformToSuccess {
				it.mapIndexed { index, novelUI ->
					novelUI.copy(
						isSelected = index == selected
					)
				}
			}
			logV("New list: $result")
			result
		}.asIOLiveData()
	}

	private val whichFlow = MutableStateFlow(0)

	override val which: LiveData<Int>
		get() = whichFlow.asIOLiveData()

	override fun setWorkingOn(index: Int) {
		logI("Now working on $index")
		whichFlow.value = index
	}

	override fun getResults(novelUI: NovelUI): LiveData<HResult<StrippedBookmarkedNovelEntity>> =
		flow {
			emit(loading)
		}.asIOLiveData()

	override fun setNovels(array: IntArray) {
		novelIds.value = array
	}
}