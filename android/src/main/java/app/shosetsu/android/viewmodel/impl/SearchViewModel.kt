package app.shosetsu.android.viewmodel.impl

import android.database.sqlite.SQLiteException
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.domain.usecases.SearchBookMarkedNovelsUseCase
import app.shosetsu.android.domain.usecases.get.GetCatalogueQueryDataUseCase
import app.shosetsu.android.domain.usecases.get.GetExtensionUseCase
import app.shosetsu.android.domain.usecases.load.LoadSearchRowUIUseCase
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.view.uimodels.model.search.SearchRowUI
import app.shosetsu.android.viewmodel.abstracted.ASearchViewModel
import app.shosetsu.lib.PAGE_INDEX
import app.shosetsu.lib.mapify
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
 * 01 / 05 / 2020
 */
class SearchViewModel(
	private val searchBookMarkedNovelsUseCase: SearchBookMarkedNovelsUseCase,
	private val loadSearchRowUIUseCase: LoadSearchRowUIUseCase,
	private val loadCatalogueQueryDataUseCase: GetCatalogueQueryDataUseCase,
	private val getExtensionUseCase: GetExtensionUseCase
) : ASearchViewModel() {
	/**
	 * Holds the current query
	 *
	 * Used to save user input
	 */
	private val queryFlow: MutableStateFlow<String?> = MutableStateFlow(null)

	/**
	 * Holds the applied query
	 *
	 * Applied means it is used for data retrieval
	 */
	private val appliedQueryFlow: MutableStateFlow<String?> = MutableStateFlow(null)

	private val searchFlows =
		HashMap<Int, Flow<PagingData<ACatalogNovelUI>>>()

	private val refreshFlows =
		HashMap<Int, MutableStateFlow<Int>>()

	private val exceptionFlows =
		HashMap<Int, MutableStateFlow<Throwable?>>()

	override val query: Flow<String?> by lazy { queryFlow.onIO() }

	override val listings: Flow<List<SearchRowUI>> by lazy {
		loadSearchRowUIUseCase().transformLatest { ogList ->
			emitAll(
				combine(ogList.map { rowUI ->
					getExceptionFlow(rowUI.extensionID).map {
						if (it != null)
							rowUI.copy(hasError = true)
						else rowUI
					}
				}) {
					it.toList()
				}
			)
		}.map { list ->
			list.sortedBy { it.name }.sortedBy { it.extensionID != -1 }.sortedBy { it.hasError }
		}.onIO()
	}

	override fun initQuery(string: String) {
		launchIO {
			if (queryFlow.first() == null) {
				queryFlow.value = string
				appliedQueryFlow.value = string
			}
		}
	}

	override fun setQuery(query: String) {
		this.queryFlow.value = query
	}

	override fun applyQuery(query: String) {
		queryFlow.value = query
		appliedQueryFlow.value = query
	}

	override fun searchLibrary(): Flow<PagingData<ACatalogNovelUI>> =
		libraryResultFlow.cachedIn(viewModelScope).onIO()

	override fun searchExtension(extensionId: Int): Flow<PagingData<ACatalogNovelUI>> =
		searchFlows.getOrPut(extensionId) {
			loadExtension(extensionId).cachedIn(viewModelScope)
		}

	override fun getException(id: Int): Flow<Throwable?> =
		getExceptionFlow(id)

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
		queryFlow.value = null
		appliedQueryFlow.value = null
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
	private val libraryResultFlow: Flow<PagingData<ACatalogNovelUI>> by lazy {
		appliedQueryFlow.combine(getRefreshFlow(-1)) { query, _ -> query }
			.transformLatest { query ->
				if (query == null) return@transformLatest

				val exceptionFlow = getExceptionFlow(-1)

				exceptionFlow.emit(null)

				try {
					emitAll(
						Pager(
							PagingConfig(10)
						) {
							searchBookMarkedNovelsUseCase(query)
						}.flow.map { data ->
							val ids = arrayListOf<Int>()
							data.filter {
								if (ids.contains(it.id)) {
									false
								} else {
									ids.add(it.id)
									true
								}
							}.map { (id, title, imageURL) ->
								ACatalogNovelUI(id, title, imageURL, false)
							}
						}
					)
				} catch (e: SQLiteException) {
					exceptionFlow.emit(e)
				}
			}
	}

	/**
	 * Creates a flow for an extension query
	 */
	@OptIn(ExperimentalCoroutinesApi::class)
	private fun loadExtension(extensionID: Int): Flow<PagingData<ACatalogNovelUI>> {
		return flow {
			val ext = getExtensionUseCase(extensionID)!!
			val exceptionFlow = getExceptionFlow(extensionID)

			emitAll(
				appliedQueryFlow.combine(getRefreshFlow(extensionID)) { query, _ -> query }
					.transformLatest { query ->
						if (query == null) return@transformLatest
						exceptionFlow.emit(null)

						val source = loadCatalogueQueryDataUseCase(
							extensionID,
							query,
							HashMap<Int, Any>().apply {
								putAll(ext.searchFiltersModel.mapify())
								this[PAGE_INDEX] = ext.startIndex
							}
						)
						emitAll(
							Pager(
								PagingConfig(10)
							) {
								source
							}.flow
						)

					}.catch {
						exceptionFlow.emit(it)
					}
			)
		}.onIO()
	}
}