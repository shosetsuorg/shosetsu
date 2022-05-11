package app.shosetsu.android.viewmodel.impl

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.domain.usecases.NovelBackgroundAddUseCase
import app.shosetsu.android.domain.usecases.get.GetCatalogueListingDataUseCase
import app.shosetsu.android.domain.usecases.get.GetCatalogueQueryDataUseCase
import app.shosetsu.android.domain.usecases.get.GetExtensionUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUIColumnsHUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUIColumnsPUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUITypeUseCase
import app.shosetsu.android.domain.usecases.settings.SetNovelUITypeUseCase
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.viewmodel.abstracted.ACatalogViewModel
import app.shosetsu.common.enums.NovelCardType
import app.shosetsu.lib.Filter
import app.shosetsu.lib.IExtension
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
) : ACatalogViewModel() {
	private val queryFlow: MutableStateFlow<String?> by lazy { MutableStateFlow(null) }

	/**
	 * Map of filter id to the state to pass into the extension
	 */
	private var filterDataState: HashMap<Int, MutableStateFlow<Any>> = hashMapOf()

	private val filterDataFlow by lazy { MutableStateFlow<Map<Int, Any>>(hashMapOf()) }

	private val iExtensionFlow: Flow<IExtension> by lazy {
		extensionIDFlow.mapNotNull { extensionID ->
			getExtensionUseCase(extensionID)
		}.distinctUntilChanged()
	}

	/**
	 * Flow source for extension ID
	 */
	private val extensionIDFlow: MutableStateFlow<Int> by lazy { MutableStateFlow(-1) }

	private val pagerFlow: Flow<Pager<Int, ACatalogNovelUI>> by lazy {
		iExtensionFlow.transformLatest { ext ->
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
		}.onIO()
	}

	override val itemsLive: Flow<PagingData<ACatalogNovelUI>> by lazy {
		pagerFlow.transformLatest {
			emitAll(it.flow)
		}.cachedIn(viewModelScope)
	}

	private val filterItemsFlow by lazy {
		iExtensionFlow.mapLatest {
			it.searchFiltersModel.toList()
		}.onIO()
	}

	override val filterItemsLive: Flow<List<Filter<*>>> by lazy {
		filterItemsFlow.transformLatest {
			filterDataState.clear() // Reset filter state so no data conflicts occur
			emit(it)
		}
	}

	override val hasSearchLive: Flow<Boolean> by lazy {
		iExtensionFlow.mapLatest { it.hasSearch }.onIO()
	}

	override val extensionName: Flow<String> by lazy {
		iExtensionFlow.mapLatest { it.name }.onIO()
	}

	override fun getBaseURL(): Flow<String> =
		flow {
			emitAll(iExtensionFlow.mapLatest { it.baseURL })
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
		extensionIDFlow.tryEmit(extensionID)
	}

	override fun applyQuery(newQuery: String) {
		launchIO {
			queryFlow.emit(newQuery)
			_applyFilter()
		}
	}

	override fun resetView() {
		launchIO {
			resetFilterDataState()
			queryFlow.emit(null)
			_applyFilter()
		}
	}

	private suspend fun resetFilter(filter: Filter<*>) {
		when (filter) {
			is Filter.Text -> _setFilterStringState(filter, filter.state)
			is Filter.Switch -> _setFilterBooleanState(filter, filter.state)
			is Filter.Checkbox -> _setFilterBooleanState(filter, filter.state)
			is Filter.TriState -> _setFilterIntState(filter, filter.state)
			is Filter.Dropdown -> _setFilterIntState(filter, filter.state)
			is Filter.RadioGroup -> _setFilterIntState(filter, filter.state)
			is Filter.List -> filter.filters.forEach { resetFilter(it) }
			is Filter.Group<*> -> filter.filters.forEach { resetFilter(it) }
		}
	}

	private suspend fun resetFilterDataState() {
		filterItemsFlow.first().forEach { filter -> resetFilter(filter) }
	}

	override fun backgroundNovelAdd(novelID: Int): Flow<BackgroundNovelAddProgress> =
		flow {
			emit(BackgroundNovelAddProgress.ADDING)
			backgroundAddUseCase(novelID)
			emit(BackgroundNovelAddProgress.ADDED)
		}.onIO()

	private suspend fun _applyFilter() {
		filterDataFlow.emit(filterDataState.mapValues { it.value.value })

	}

	override fun applyFilter() {
		launchIO {
			_applyFilter()
		}
	}

	override fun getFilterStringState(id: Filter<String>): Flow<String> =
		filterDataState.specialGetOrPut(id.id) {
			MutableStateFlow(id.state)
		}.onIO()

	private suspend fun _setFilterStringState(id: Filter<String>, value: String) {
		filterDataState.specialGetOrPut(id.id) {
			MutableStateFlow(id.state)
		}.emit(value)
	}


	override fun setFilterStringState(id: Filter<String>, value: String) {
		launchIO { _setFilterStringState(id, value) }
	}

	override fun getFilterBooleanState(id: Filter<Boolean>): Flow<Boolean> =
		filterDataState.specialGetOrPut(id.id) {
			MutableStateFlow(id.state)
		}.onIO()

	private suspend fun _setFilterBooleanState(id: Filter<Boolean>, value: Boolean) {
		filterDataState.specialGetOrPut(id.id) {
			MutableStateFlow(id.state)
		}.emit(value)
	}

	override fun setFilterBooleanState(id: Filter<Boolean>, value: Boolean) {
		launchIO { _setFilterBooleanState(id, value) }
	}

	override fun getFilterIntState(id: Filter<Int>): Flow<Int> =
		filterDataState.specialGetOrPut(id.id) {
			MutableStateFlow(id.state)
		}.onIO()

	private suspend fun _setFilterIntState(id: Filter<Int>, value: Int) {
		filterDataState.specialGetOrPut(id.id) {
			MutableStateFlow(id.state)
		}.emit(value)
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



