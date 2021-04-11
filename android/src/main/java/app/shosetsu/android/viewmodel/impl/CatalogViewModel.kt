package app.shosetsu.android.viewmodel.impl

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.NovelBackgroundAddUseCase
import app.shosetsu.android.domain.usecases.get.GetCatalogueListingDataUseCase
import app.shosetsu.android.domain.usecases.get.GetCatalogueQueryDataUseCase
import app.shosetsu.android.domain.usecases.get.GetExtensionUseCase
import app.shosetsu.android.domain.usecases.load.*
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.viewmodel.abstracted.ICatalogViewModel
import app.shosetsu.common.dto.*
import app.shosetsu.common.enums.NovelCardType
import app.shosetsu.lib.Filter
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.PAGE_INDEX
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
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
@ExperimentalCoroutinesApi
class CatalogViewModel(
	private val getExtensionUseCase: GetExtensionUseCase,
	private val backgroundAddUseCase: NovelBackgroundAddUseCase,
	private val getCatalogueListingData: GetCatalogueListingDataUseCase,
	private val loadCatalogueQueryDataUseCase: GetCatalogueQueryDataUseCase,
	private var reportExceptionUseCase: ReportExceptionUseCase,
	private val loadNovelUITypeUseCase: LoadNovelUITypeUseCase,
	private val loadNovelUIColumnsHUseCase: LoadNovelUIColumnsHUseCase,
	private val loadNovelUIColumnsPUseCase: LoadNovelUIColumnsPUseCase,
) : ICatalogViewModel() {
	private var queryState: String = ""

	private var filterDataState: Map<Int, Any> = hashMapOf()

	private var ext: IExtension? = null

	private val iExtensionFlow: Flow<HResult<IExtension>> by lazy {
		extensionIDFlow.mapLatest { extensionID ->
			getExtensionUseCase(extensionID).apply { ext = unwrap() }
		}
	}

	/**
	 * Flow source for extension ID
	 */
	private val extensionIDFlow: MutableStateFlow<Int> by lazy { MutableStateFlow(-1) }

	override val itemsLive: LiveData<HResult<List<ACatalogNovelUI>>> by lazy {
		itemsFlow.asIOLiveData()
	}

	private val itemsFlow: MutableStateFlow<HResult<List<ACatalogNovelUI>>> by lazy {
		MutableStateFlow(loading)
	}

	override val filterItemsLive: LiveData<HResult<List<Filter<*>>>> by lazy {
		iExtensionFlow.mapLatestResult { successResult(it.searchFiltersModel.toList()) }
			.asIOLiveData()
	}

	override val hasSearchLive: LiveData<Boolean> by lazy {
		iExtensionFlow.mapLatest { it.unwrap()?.hasSearch == true }.asIOLiveData()
	}

	override val extensionName: LiveData<HResult<String>> by lazy {
		iExtensionFlow.mapLatestResult { successResult(it.name) }.asIOLiveData()
	}

	private var stateManager = StateManager()


	/**
	 * Handles the current state of the UI
	 */
	private inner class StateManager {
		private val loaderManager by lazy { LoaderManager() }

		fun loadMore() {
			loaderManager.loadMore(QueryFilter(queryState, filterDataState))
		}

		/**
		 * The idea behind this class is the squeeze all the loading jobs into a single class.
		 * There will only be 1 instance at a time
		 */
		private inner class LoaderManager {
			/**
			 * Current loading job
			 */
			private var loadingJob: Job? = null

			private var _canLoadMore = true

			init {
				itemsFlow.tryEmit(successResult(emptyList()))
				itemsFlow.tryEmit(loading)
			}

			/**
			 * The current max page loaded.
			 *
			 * if 2, then the current page that has been appended is 2
			 */
			private var currentMaxPage: Int = 0

			private var values = arrayListOf<ACatalogNovelUI>()

			fun loadMore(queryFilter: QueryFilter) {
				logD("")
				if (_canLoadMore && ((loadingJob != null && (loadingJob!!.isCancelled || loadingJob!!.isCompleted)) || (loadingJob == null))) {
					logD("Proceeding with loading")
					loadingJob = null
					loadingJob = loadData(queryFilter)
				}
			}

			private fun loadData(queryFilter: QueryFilter): Job = launchIO {
				if (ext == null) {
					logE("formatter was null")
					this.cancel("Extension not loaded")
					return@launchIO
				}
				logD("Passed null extension check")
				currentMaxPage++
				itemsFlow.tryEmit(loading())

				getDataLoaderAndLoad(queryFilter).handle(onError = {
					_canLoadMore = false
					reportError(it)
					logE("Error: ${it.code}|${it.message}", it.exception)
					itemsFlow.tryEmit(it)
				}, onEmpty = {
					_canLoadMore = false
				}, onLoading = {
				}) { newList ->
					values.plusAssign(newList)
					itemsFlow.tryEmit(successResult(values))
				}
			}

			private suspend fun getDataLoaderAndLoad(queryFilter: QueryFilter): HResult<List<ACatalogNovelUI>> {
				return if (queryFilter.query.isEmpty()) {
					logV("Loading listing data")
					getCatalogueListingData(
						ext!!,
						HashMap<Int, Any>().apply {
							putAll(queryFilter.filters)
							this[PAGE_INDEX] = currentMaxPage
						}
					)
				} else {
					logV("Loading query data")
					loadCatalogueQueryDataUseCase(
						ext!!,
						queryFilter.query,
						HashMap<Int, Any>().apply {
							putAll(queryFilter.filters)
							this[PAGE_INDEX] = currentMaxPage
						}
					)
				}
			}
		}
	}

	private data class QueryFilter(
		var query: String,
		var filters: Map<Int, Any>
	)


	override fun setExtensionID(extensionID: Int) {
		when {
			extensionIDFlow.value == -1 ->
				logI("Setting NovelID")
			extensionIDFlow.value != extensionID ->
				logI("NovelID not equal, resetting")
			extensionIDFlow.value == extensionID -> {
				logI("Ignore if the same")
				return
			}
		}
		extensionIDFlow.tryEmit(extensionID)
	}

	override fun applyQuery(newQuery: String) {
		logV("")
		queryState = newQuery
		stateManager = StateManager()
		stateManager.loadMore()
	}

	@Synchronized
	override fun loadMore() {
		logV("")
		stateManager.loadMore()
	}

	override fun resetView() {
		logV("")
		itemsFlow.tryEmit(successResult(arrayListOf()))
		stateManager = StateManager()
		stateManager.loadMore()
	}

	override fun backgroundNovelAdd(novelID: Int) {
		launchIO { backgroundAddUseCase(novelID) }
	}

	override fun applyFilter(map: Map<Int, Any>) {
		logV("")
		filterDataState = map
		stateManager = StateManager()
		stateManager.loadMore()
	}

	override fun destroy() {
		queryState = ""
		extensionIDFlow.value = -1
		itemsFlow.tryEmit(successResult(arrayListOf()))
		stateManager = StateManager()
	}

	override fun resetFilter() {
		filterDataState = mapOf()
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) =
		reportExceptionUseCase(error)


	override val novelCardTypeLive: LiveData<NovelCardType> by lazy {
		loadNovelUITypeUseCase().asIOLiveData()
	}

	override val columnsInH: LiveData<Int> by lazy {
		loadNovelUIColumnsHUseCase().asIOLiveData()
	}

	override val columnsInP: LiveData<Int> by lazy {
		loadNovelUIColumnsPUseCase().asIOLiveData()
	}
}

