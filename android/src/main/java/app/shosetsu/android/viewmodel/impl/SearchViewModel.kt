package app.shosetsu.android.viewmodel.impl

import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.domain.usecases.SearchBookMarkedNovelsUseCase
import app.shosetsu.android.domain.usecases.get.GetCatalogueQueryDataUseCase
import app.shosetsu.android.domain.usecases.load.LoadSearchRowUIUseCase
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.view.uimodels.model.catlog.FullCatalogNovelUI
import app.shosetsu.android.view.uimodels.model.search.SearchRowUI
import app.shosetsu.android.viewmodel.abstracted.ASearchViewModel
import app.shosetsu.common.GenericSQLiteException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
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
) : ASearchViewModel() {
	private val queryFlow: MutableStateFlow<String> = MutableStateFlow("")

	private val searchFlows =
		HashMap<Int, Flow<List<ACatalogNovelUI>>>()

	private val refreshFlows =
		HashMap<Int, MutableStateFlow<Int>>()

	private val exceptionFlows =
		HashMap<Int, MutableStateFlow<Throwable?>>()

	private val loadingFlows = HashMap<Int, Flow<Boolean>>()

	override val listings: Flow<List<SearchRowUI>> by lazy {
		loadSearchRowUIUseCase().onIO()
	}

	override fun setQuery(query: String) {
		this.queryFlow.value = query
	}

	override fun searchLibrary(): Flow<List<ACatalogNovelUI>> =
		searchFlows.getOrPut(-1) {
			libraryResultFlow
		}

	override fun searchExtension(extensionId: Int): Flow<List<ACatalogNovelUI>> =
		searchFlows.getOrPut(extensionId) {
			loadExtension(extensionId)
		}

	override fun getIsLoading(id: Int): Flow<Boolean> =
		loadingFlows.getOrPut(id) {
			MutableStateFlow(true)
		}

	override fun getException(id: Int): Flow<Throwable?> =
		exceptionFlows.getOrPut(id) {
			MutableStateFlow(null)
		}

	override fun refresh() {
		launchIO {
			refreshFlows.values.forEach {
				it.emit(it.value++)
			}
		}
	}

	override fun refresh(id: Int) {
		logI("$id")
		launchIO {
			val flow = getRefreshFlow(id)
			flow.emit(flow.value++)
		}
	}

	/**
	 * Clears out all the data
	 */
	override fun destroy() {
		logI("Clearing out all flows")
		searchFlows.clear()
	}

	private fun getRefreshFlow(id: Int) =
		refreshFlows.getOrPut(id) {
			MutableStateFlow(0)
		}

	private fun getExceptionFlow(id: Int) =
		exceptionFlows.getOrPut(id) {
			MutableStateFlow(null)
		}

	/**
	 * Creates a flow for a library query
	 */
	@OptIn(ExperimentalCoroutinesApi::class)
	private val libraryResultFlow: Flow<List<ACatalogNovelUI>> by lazy {
		queryFlow.combine(getRefreshFlow(-1)) { query, _ -> query }
			.transformLatest<String, List<ACatalogNovelUI>> { query ->

				(getIsLoading(-1) as MutableStateFlow).emit(true)
				getExceptionFlow(-1).emit(null)

				try {
					emit(searchBookMarkedNovelsUseCase(query).let {
						it.map { (id, title, imageURL) ->
							FullCatalogNovelUI(id, title, imageURL, false)
						}
					})
				} catch (e: GenericSQLiteException) {
					getExceptionFlow(-1).emit(e)
				}


				(getIsLoading(-1) as MutableStateFlow).emit(false)
			}.onIO()
	}

	/**
	 * Creates a flow for an extension query
	 */
	@OptIn(ExperimentalCoroutinesApi::class)
	private fun loadExtension(extensionID: Int): Flow<List<ACatalogNovelUI>> =
		queryFlow.combine(getRefreshFlow(extensionID)) { query, _ -> query }
			.transformLatest { query ->

				(getIsLoading(extensionID) as MutableStateFlow).emit(true)
				getExceptionFlow(extensionID).emit(null)

				try {
					emit(
						loadCatalogueQueryDataUseCase(
							extensionID,
							query,
							mapOf()
						)
					)
				} catch (e: Exception) {
					getExceptionFlow(extensionID).emit(e)
				}

				(getIsLoading(extensionID) as MutableStateFlow).emit(false)
			}.onIO()
}