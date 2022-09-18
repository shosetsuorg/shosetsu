package app.shosetsu.android.viewmodel.impl

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.shosetsu.android.common.enums.NovelCardType
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.utils.copy
import app.shosetsu.android.domain.usecases.NovelBackgroundAddUseCase
import app.shosetsu.android.domain.usecases.SetNovelCategoriesUseCase
import app.shosetsu.android.domain.usecases.get.GetCatalogueListingDataUseCase
import app.shosetsu.android.domain.usecases.get.GetCatalogueQueryDataUseCase
import app.shosetsu.android.domain.usecases.get.GetCategoriesUseCase
import app.shosetsu.android.domain.usecases.get.GetExtensionUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUIColumnsHUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUIColumnsPUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUITypeUseCase
import app.shosetsu.android.domain.usecases.settings.SetNovelUITypeUseCase
import app.shosetsu.android.view.uimodels.model.CategoryUI
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.viewmodel.abstracted.ACatalogViewModel
import app.shosetsu.lib.Filter
import app.shosetsu.lib.IExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus

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
@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModel(
	private val getExtensionUseCase: GetExtensionUseCase,
	private val backgroundAddUseCase: NovelBackgroundAddUseCase,
	private val getCatalogueListingData: GetCatalogueListingDataUseCase,
	private val loadCatalogueQueryDataUseCase: GetCatalogueQueryDataUseCase,
	private val loadNovelUITypeUseCase: LoadNovelUITypeUseCase,
	private val loadNovelUIColumnsHUseCase: LoadNovelUIColumnsHUseCase,
	private val loadNovelUIColumnsPUseCase: LoadNovelUIColumnsPUseCase,
	private val setNovelUIType: SetNovelUITypeUseCase,
	private val getCategoriesUseCase: GetCategoriesUseCase,
	private val setNovelCategoriesUseCase: SetNovelCategoriesUseCase
) : ACatalogViewModel() {
	private val queryFlow: MutableStateFlow<String?> by lazy { MutableStateFlow(null) }

	/**
	 * Map of filter id to the state to pass into the extension
	 */
	private var filterDataState: HashMap<Int, MutableStateFlow<Any>> = hashMapOf()

	private val filterDataFlow by lazy { MutableStateFlow<Map<Int, Any>>(hashMapOf()) }

	private val iExtensionFlow: Flow<IExtension?> by lazy {
		extensionIDFlow.transformLatest { extensionID ->
			val ext = getExtensionUseCase(extensionID)

			// Ensure filter is initialized
			ext?.searchFiltersModel?.toList()?.init()
			_applyFilter()
			emit(ext)
		}.distinctUntilChanged()
	}

	private fun List<Filter<*>>.init() {
		forEach { filter ->
			when (filter) {
				is Filter.Text -> getFilterStringState(filter)
				is Filter.Switch -> getFilterBooleanState(filter)
				is Filter.Checkbox -> getFilterBooleanState(filter)
				is Filter.TriState -> getFilterIntState(filter)
				is Filter.Dropdown -> getFilterIntState(filter)
				is Filter.RadioGroup -> getFilterIntState(filter)
				is Filter.List -> {
					filter.filters.toList().init()
				}
				is Filter.Group<*> -> {
					filter.filters.toList().init()
				}
				is Filter.Header -> {
				}
				is Filter.Separator -> {
				}
			}
		}
	}

	/**
	 * Flow source for extension ID
	 */
	private val extensionIDFlow: MutableStateFlow<Int> by lazy { MutableStateFlow(-1) }

	private val pagerFlow: Flow<Pager<Int, ACatalogNovelUI>?> by lazy {
		iExtensionFlow.transformLatest { ext ->
			if (ext == null) {
				emit(null)
			} else {
				emitAll(queryFlow.transformLatest { query ->
					emitAll(filterDataFlow.transformLatest { data ->
						emit(
							Pager(
								PagingConfig(10)
							) {
								if (query == null)
									getCatalogueListingData(ext, data)
								else loadCatalogueQueryDataUseCase(
									ext,
									query,
									data
								)
							}
						)
					}
					)
				})
			}
		}.onIO()
	}

	override val itemsLive: Flow<PagingData<ACatalogNovelUI>> by lazy {
		pagerFlow.transformLatest {
			if (it != null)
				emitAll(it.flow)
			else emit(PagingData.empty())
		}.catch {
			exceptionFlow.emit(it)
		}.cachedIn(viewModelScope)
	}

	override val exceptionFlow: MutableStateFlow<Throwable?> by lazy {
		MutableStateFlow(null)
	}

	private val filterItemsFlow: Flow<List<Filter<*>>> by lazy {
		iExtensionFlow.mapLatest {
			it?.searchFiltersModel?.toList() ?: emptyList()
		}.onIO()
	}

	override val filterItemsLive: Flow<List<Filter<*>>> by lazy {
		filterItemsFlow.transformLatest {
			filterDataState.clear() // Reset filter state so no data conflicts occur
			emit(it)
		}.shareIn(viewModelScope + Dispatchers.IO, SharingStarted.Eagerly, 1)
	}

	override val hasFilters: Flow<Boolean> by lazy {
		iExtensionFlow.mapLatest { it?.searchFiltersModel?.isNotEmpty() ?: false }
	}

	override val hasSearchLive: Flow<Boolean> by lazy {
		iExtensionFlow.mapLatest { it?.hasSearch ?: false }.onIO()
	}

	override val extensionName: Flow<String> by lazy {
		iExtensionFlow.mapLatest { it?.name ?: "" }.onIO()
	}

	override fun getBaseURL(): Flow<String> =
		flow {
			val ext = iExtensionFlow.first() ?: return@flow
			emit(ext.baseURL)
		}.onIO()

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
		extensionIDFlow.value = extensionID
	}

	override fun applyQuery(newQuery: String) {
		queryFlow.value = newQuery
		_applyFilter()
	}

	override fun resetView() {
		launchIO {
			resetFilterDataState()
			queryFlow.value = null
			_applyFilter()
		}
	}

	private fun resetFilter(filter: Filter<*>) {
		when (filter) {
			is Filter.Text -> _setFilterStringState(filter, filter.state)
			is Filter.Switch -> _setFilterBooleanState(filter, filter.state)
			is Filter.Checkbox -> _setFilterBooleanState(filter, filter.state)
			is Filter.TriState -> _setFilterIntState(filter, filter.state)
			is Filter.Dropdown -> _setFilterIntState(filter, filter.state)
			is Filter.RadioGroup -> _setFilterIntState(filter, filter.state)
			is Filter.List -> filter.filters.forEach { resetFilter(it) }
			is Filter.Group<*> -> filter.filters.forEach { resetFilter(it) }
			else -> {}
		}
	}

	private suspend fun resetFilterDataState() {
		filterItemsFlow.first().forEach { filter -> resetFilter(filter) }
	}

	override fun backgroundNovelAdd(novelID: Int, categories: IntArray): Flow<BackgroundNovelAddProgress> =
		flow {
			emit(BackgroundNovelAddProgress.ADDING)
			backgroundAddUseCase(novelID)
			if (categories.isNotEmpty())
				setNovelCategoriesUseCase(novelID, categories)
			emit(BackgroundNovelAddProgress.ADDED)
		}.onIO()

	@Throws(ConcurrentModificationException::class)
	@Synchronized
	private fun _applyFilter(retry: Boolean = false) {
		try {
			filterDataFlow.value = filterDataState.copy().mapValues { it.value.value }
		} catch (e: ConcurrentModificationException) {
			if (!retry)
				_applyFilter(true)
			else throw e
		}
	}

	override fun applyFilter() {
		@Suppress("CheckedExceptionsKotlin")
		_applyFilter()
	}

	override fun getFilterStringState(id: Filter<String>): Flow<String> =
		filterDataState.specialGetOrPut(id.id) {
			MutableStateFlow(id.state)
		}.onIO()

	private fun _setFilterStringState(id: Filter<String>, value: String) {
		filterDataState.specialGetOrPut(id.id) {
			MutableStateFlow(id.state)
		}.value = value
	}


	override fun setFilterStringState(id: Filter<String>, value: String) {
		launchIO { _setFilterStringState(id, value) }
	}

	override fun getFilterBooleanState(id: Filter<Boolean>): Flow<Boolean> =
		filterDataState.specialGetOrPut(id.id) {
			MutableStateFlow(id.state)
		}.onIO()

	private fun _setFilterBooleanState(id: Filter<Boolean>, value: Boolean) {
		filterDataState.specialGetOrPut(id.id) {
			MutableStateFlow(id.state)
		}.value = value
	}

	override fun setFilterBooleanState(id: Filter<Boolean>, value: Boolean) {
		launchIO { _setFilterBooleanState(id, value) }
	}

	override fun getFilterIntState(id: Filter<Int>): Flow<Int> =
		filterDataState.specialGetOrPut(id.id) {
			MutableStateFlow(id.state)
		}.onIO()

	private fun _setFilterIntState(id: Filter<Int>, value: Int) {
		filterDataState.specialGetOrPut(id.id) {
			MutableStateFlow(id.state)
		}.value = value
	}

	override fun setFilterIntState(id: Filter<Int>, value: Int) {
		launchIO { _setFilterIntState(id, value) }
	}

	override fun resetFilter() {
		launchIO {
			resetFilterDataState()
			_applyFilter()
		}
	}

	override fun setViewType(cardType: NovelCardType) {
		launchIO { setNovelUIType(cardType) }
	}

	override val novelCardTypeLive: Flow<NovelCardType> by lazy {
		loadNovelUITypeUseCase().onIO()
	}

	override val columnsInH: Flow<Int> by lazy {
		loadNovelUIColumnsHUseCase().onIO()
	}

	override val columnsInV: Flow<Int> by lazy {
		loadNovelUIColumnsPUseCase().onIO()
	}

	override val categories: Flow<List<CategoryUI>> by lazy {
		getCategoriesUseCase()
	}

	override fun destroy() {
		extensionIDFlow.value = -1
		resetView()
		System.gc()
	}


	/**
	 * @param [V] Value type of the hash map
	 * @param [O] Expected value type
	 */
	private inline fun <reified O, V> HashMap<Int, V>.specialGetOrPut(
		key: Int,
		getDefaultValue: () -> O
	): O {
		// Do not use computeIfAbsent on JVM8 as it would change locking behavior
		return this[key].takeIf { value -> value is O }?.let { value -> value as O }
			?: getDefaultValue().also { defaultValue ->
				@Suppress("UNCHECKED_CAST")
				put(key, defaultValue as V)
			}
	}
}



