package app.shosetsu.android.viewmodel.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import app.shosetsu.android.common.ext.logD
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.SearchBookMarkedNovelsUseCase
import app.shosetsu.android.domain.usecases.get.GetCatalogueQueryDataUseCase
import app.shosetsu.android.domain.usecases.load.LoadSearchRowUIUseCase
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.view.uimodels.model.catlog.FullCatalogNovelUI
import app.shosetsu.android.view.uimodels.model.search.SearchRowUI
import app.shosetsu.android.viewmodel.abstracted.ASearchViewModel
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.loading
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.transformLatest

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
 * 01 / 05 / 2020
 */
class SearchViewModel(
	private val searchBookMarkedNovelsUseCase: SearchBookMarkedNovelsUseCase,
	private val loadSearchRowUIUseCase: LoadSearchRowUIUseCase,
	private val loadCatalogueQueryDataUseCase: GetCatalogueQueryDataUseCase,
	private val reportExceptionUseCase: ReportExceptionUseCase
) : ASearchViewModel() {
	private val queryFlow: MutableStateFlow<String> = MutableStateFlow("")

	private val searchFlows =
		HashMap<Int, Flow<HResult<List<ACatalogNovelUI>>>>()

	override val listings: LiveData<HResult<List<SearchRowUI>>> by lazy {
		liveData {
			emit(loading())
			emitSource(loadSearchRowUIUseCase().asIOLiveData())
		}
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	override fun setQuery(query: String) {
		this.queryFlow.value = query
	}

	override fun searchLibrary(): LiveData<HResult<List<ACatalogNovelUI>>> =
		searchFlows.getOrPut(-1) {
			libraryResultFlow
		}.asIOLiveData()

	override fun searchExtension(extensionId: Int): LiveData<HResult<List<ACatalogNovelUI>>> =
		searchFlows.getOrPut(extensionId) {
			logD("Creating new flow for extension")
			loadExtension(extensionId)
		}.asIOLiveData()

	/**
	 * Clears out all the data
	 */
	override fun destroy() {
		logI("Clearing out all flows")
		searchFlows.clear()
	}

	/**
	 * Creates a flow for a library query
	 */
	@ExperimentalCoroutinesApi
	private val libraryResultFlow: Flow<HResult<List<ACatalogNovelUI>>> by lazy {
		queryFlow.transformLatest { query ->
			emit(successResult(listOf()))
			emit(loading)

			emit(searchBookMarkedNovelsUseCase(query).transform {
				successResult(it.map { (id, title, imageURL) ->
					FullCatalogNovelUI(id, title, imageURL, false)
				})
			})
		}
	}

	/**
	 * Creates a flow for an extension query
	 */
	@ExperimentalCoroutinesApi
	private fun loadExtension(extensionID: Int): Flow<HResult<List<ACatalogNovelUI>>> =
		queryFlow.transformLatest { query ->
			emit(successResult(listOf()))
			emit(loading)

			emit(
				loadCatalogueQueryDataUseCase(
					extensionID,
					query,
					mapOf()
				)
			)
		}
}