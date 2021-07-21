package app.shosetsu.android.viewmodel.impl

import android.app.Application
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.NovelBackgroundAddUseCase
import app.shosetsu.android.domain.usecases.get.GetCatalogueListingDataUseCase
import app.shosetsu.android.domain.usecases.get.GetCatalogueQueryDataUseCase
import app.shosetsu.android.domain.usecases.get.GetExtensionUseCase
import app.shosetsu.android.domain.usecases.load.*
import app.shosetsu.android.domain.usecases.settings.SetNovelUITypeUseCase
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.view.uimodels.model.catlog.CompactCatalogNovelUI
import app.shosetsu.android.view.uimodels.model.catlog.FullCatalogNovelUI
import app.shosetsu.android.view.uimodels.settings.DividerSettingItemData
import app.shosetsu.android.view.uimodels.settings.ListSettingData
import app.shosetsu.android.view.uimodels.settings.TriStateButtonSettingData
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.view.widget.TriState
import app.shosetsu.android.viewmodel.abstracted.ACatalogViewModel
import app.shosetsu.common.dto.*
import app.shosetsu.common.enums.NovelCardType
import app.shosetsu.lib.Filter
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.PAGE_INDEX
import app.shosetsu.lib.mapify
import com.github.doomsdayrs.apps.shosetsu.R
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
	private val application: Application,
	private val getExtensionUseCase: GetExtensionUseCase,
	private val backgroundAddUseCase: NovelBackgroundAddUseCase,
	private val getCatalogueListingData: GetCatalogueListingDataUseCase,
	private val loadCatalogueQueryDataUseCase: GetCatalogueQueryDataUseCase,
	private var reportExceptionUseCase: ReportExceptionUseCase,
	private val loadNovelUITypeUseCase: LoadNovelUITypeUseCase,
	private val loadNovelUIColumnsHUseCase: LoadNovelUIColumnsHUseCase,
	private val loadNovelUIColumnsPUseCase: LoadNovelUIColumnsPUseCase,
	private val setNovelUIType: SetNovelUITypeUseCase,
) : ACatalogViewModel() {
	private var queryState: String = ""

	/**
	 * Map of filter id to the state to pass into the extension
	 */
	private var filterDataState: HashMap<Int, Any> = hashMapOf()

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
		itemsFlow.combine(novelCardTypeFlow) { items, type ->
			items.transformToSuccess { list ->
				list.map { card ->
					when (type) {
						NovelCardType.NORMAL -> {
							if (card is FullCatalogNovelUI)
								card
							else FullCatalogNovelUI(
								card.id,
								card.title,
								card.imageURL,
								card.bookmarked
							)
						}
						NovelCardType.COMPRESSED -> {
							if (card is CompactCatalogNovelUI)
								card
							else CompactCatalogNovelUI(
								card.id,
								card.title,
								card.imageURL,
								card.bookmarked
							)
						}
						NovelCardType.COZY -> {
							logE("Cozy type not implemented")
							card
						}
					}
				}
			}
		}.asIOLiveData()
	}

	private val itemsFlow: MutableStateFlow<HResult<List<ACatalogNovelUI>>> by lazy {
		MutableStateFlow(loading)
	}

	private val filterItemFlow: Flow<HResult<Array<Filter<*>>>> by lazy {
		iExtensionFlow.mapLatestResult { successResult(it.searchFiltersModel) }
	}

	private fun Array<Filter<*>>.convert(): List<SettingsItemData> = convertToSettingItems(this)

	private fun convertToSettingItems(filters: Array<Filter<*>>): List<SettingsItemData> =
		filters.map { filter ->
			when (filter) {
				is Filter.Header -> headerSettingItemData(filter.id) {
					titleText = filter.name
				}
				is Filter.Separator -> DividerSettingItemData(filter.id)
				is Filter.Text -> textInputSettingData(filter.id) {
					titleText = filter.name

					// Check if the result is of the expected type
					val result = filterDataState.getOrElse(filter.id) { filter.state }
					(result as? String)?.let {
						initialText = it
					}
						?: this@CatalogViewModel.logE("Filter(${filter.id}) expected String, received ${result.javaClass.simpleName}")
					doAfterTextChanged { editable ->
						filterDataState[filter.id] = editable.toString()
					}
				}
				is Filter.Switch -> asSettingItem(filter)
				is Filter.Checkbox -> asSettingItem(filter)
				is Filter.TriState -> TriStateButtonSettingData(filter.id).apply {
					titleText = filter.name
					checkedRes = R.drawable.checkbox_checked
					uncheckedRes = R.drawable.checkbox_inter
					ignoredRes = R.drawable.checkbox_ignored

					onStateChanged = {
						filterDataState[filter.id] = state.ordinal
					}

					// Check if the result is of the expected type
					val result = filterDataState.getOrElse(filter.id) { filter.state }
					(result as? Int)?.let {
						state = when (it) {
							Filter.TriState.STATE_EXCLUDE -> TriState.State.UNCHECKED
							Filter.TriState.STATE_IGNORED -> TriState.State.IGNORED
							Filter.TriState.STATE_INCLUDE -> TriState.State.CHECKED
							else -> TriState.State.IGNORED
						}
					}
						?: this@CatalogViewModel.logE("Filter(${filter.id}) expected Int, received ${result.javaClass.simpleName}")


				}

				is Filter.Dropdown -> spinnerSettingData(filter.id) {
					titleText = filter.name
					arrayAdapter = ArrayAdapter(
						application,
						android.R.layout.simple_spinner_dropdown_item,
						filter.choices
					)

					// Check if the result is of the expected type
					val result = filterDataState.getOrElse(filter.id) { filter.state }
					(result as? Int)?.let {
						spinnerValue { it }
					}
						?: this@CatalogViewModel.logE("Filter(${filter.id}) expected Int, received ${result.javaClass.simpleName}")

					onSpinnerItemSelected { _, _, position, _ ->
						filterDataState[filter.id] = position
					}
				}

				is Filter.RadioGroup -> spinnerSettingData(filter.id) {
					titleText = filter.name
					arrayAdapter = ArrayAdapter(
						application,
						android.R.layout.simple_spinner_dropdown_item,
						filter.choices
					)
					val result = filterDataState.getOrElse(filter.id) { filter.state }
					(result as? Int)?.let {
						spinnerValue { it }
					}
						?: this@CatalogViewModel.logE("Filter(${filter.id}) expected Int, received ${result.javaClass.simpleName}")
					onSpinnerItemSelected { _, _, position, _ ->
						filterDataState[filter.id] = position
					}
				}
				is Filter.List -> ListSettingData(filter.id).apply {
					titleText = filter.name

					val f: Array<Filter<*>> = filter.filters.toList().toTypedArray()
					launchIO {
						val result = f.convert()
						launchUI {
							FastAdapterDiffUtil[itemAdapter] =
								FastAdapterDiffUtil.calculateDiff(itemAdapter, result)
						}
					}

				}
				is Filter.Group<*> -> ListSettingData(filter.id).apply {
					titleText = filter.name

					val f: Array<Filter<*>> = filter.filters.toList().toTypedArray()
					launchIO {
						val result = f.convert()
						launchUI {
							FastAdapterDiffUtil[itemAdapter] =
								FastAdapterDiffUtil.calculateDiff(itemAdapter, result)
						}
					}

				}
			}
		}

	private fun asSettingItem(filter: Filter<Boolean>) = switchSettingData(filter.id) {
		titleText = filter.name

		// Check if the result is of the expected type
		val result = filterDataState.getOrElse(filter.id) { filter.state }
		(result as? Boolean)?.let {
			isChecked = it
		}
			?: this@CatalogViewModel.logE("Filter(${filter.id}) expected Boolean, received ${result.javaClass.simpleName}")

		onChecked { _, isChecked ->
			filterDataState[filter.id] = isChecked
		}
	}

	/**
	 * This flow is used to reload the filters
	 */
	private val filterReloadFlow = MutableStateFlow(true)

	override val filterItemsLive: LiveData<HResult<List<SettingsItemData>>> by lazy {
		channelFlow {
			launch {
				// We ignore any results from filterReloadFlow, as we are using it to trigger the reload
				filterItemFlow.combine(filterReloadFlow) { a, b -> a }
					.collectLatest { result ->
						result.handle {
							logD("Mapping filter array to setting item array")
							send(successResult(listOf()))
							send(successResult(convertToSettingItems(it)))
						}
					}
			}
		}.asIOLiveData()

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
				currentMaxPage++
			}

			private suspend fun getDataLoaderAndLoad(queryFilter: QueryFilter): HResult<List<ACatalogNovelUI>> {
				return if (queryFilter.query.isEmpty()) {
					logV("Loading listing data")
					getCatalogueListingData(
						ext!!,
						HashMap<Int, Any>().apply {
							putAll(ext!!.searchFiltersModel.mapify())
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
							putAll(ext!!.searchFiltersModel.mapify())
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
		queryState = newQuery
		stateManager = StateManager()
		stateManager.loadMore()
	}

	@Synchronized
	override fun loadMore() {
		stateManager.loadMore()
	}

	override fun resetView() {
		itemsFlow.tryEmit(successResult(arrayListOf()))
		applyFilter()
	}

	override fun backgroundNovelAdd(novelID: Int): LiveData<HResult<*>> =
		flow {
			emit(loading)
			emit(backgroundAddUseCase(novelID))
		}.asIOLiveData()

	override fun applyFilter() {
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
		filterDataState.clear()
		launchIO {
			filterReloadFlow.emit(!filterReloadFlow.value)
			applyFilter()
		}
	}

	override fun setViewType(cardType: NovelCardType) {
		launchIO { setNovelUIType(cardType) }
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		logE("Exception", error.exception)
		//reportExceptionUseCase(error)
	}


	private val novelCardTypeFlow = loadNovelUITypeUseCase()

	override val novelCardTypeLive: LiveData<NovelCardType> by lazy {
		novelCardTypeFlow.asIOLiveData()
	}

	override val columnsInH: LiveData<Int> by lazy {
		loadNovelUIColumnsHUseCase().asIOLiveData()
	}

	override val columnsInP: LiveData<Int> by lazy {
		loadNovelUIColumnsPUseCase().asIOLiveData()
	}
}

