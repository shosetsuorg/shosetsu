package app.shosetsu.android.viewmodel.impl

import androidx.lifecycle.MutableLiveData
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.NovelBackgroundAddUseCase
import app.shosetsu.android.domain.usecases.get.GetCatalogueListingDataUseCase
import app.shosetsu.android.domain.usecases.get.GetCatalogueQueryDataUseCase
import app.shosetsu.android.domain.usecases.get.GetExtensionUseCase
import app.shosetsu.android.domain.usecases.load.*
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.viewmodel.abstracted.ICatalogViewModel
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.loading
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.enums.NovelUIType
import app.shosetsu.lib.Filter
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.PAGE_INDEX
import app.shosetsu.lib.mapify
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest

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
	private var novelUIType: NovelUIType = NovelUIType.fromInt(SettingKey.NovelCardType.default)
	private var columnP: Int = SettingKey.ChapterColumnsInPortait.default
	private var columnH: Int = SettingKey.ChapterColumnsInLandscape.default
	private var iExtension: IExtension? = null
	private var listingItems: ArrayList<ACatalogNovelUI> = arrayListOf()
	private var filterData = hashMapOf<Int, Any>()
	private var query: String = ""

	override val listingItemsLive: MutableLiveData<HResult<List<ACatalogNovelUI>>> by lazy {
		MutableLiveData<HResult<List<ACatalogNovelUI>>>(loading())
	}
	override val filterItemsLive: MutableLiveData<HResult<List<Filter<*>>>> by lazy {
		MutableLiveData(loading())
	}
	override val hasSearchLive: MutableLiveData<HResult<Boolean>> by lazy {
		MutableLiveData(loading())
	}
	override val extensionName: MutableLiveData<HResult<String>> by lazy {
		MutableLiveData<HResult<String>>(loading())
	}

	/**
	 * Current loading job
	 */
	private var loadingJob: Job = launchIO { }

	init {
		launchIO {
			loadNovelUIColumnsHUseCase().collectLatest {
				columnH = it
			}
			loadNovelUIColumnsPUseCase().collectLatest {
				columnP = it
			}
			loadNovelUITypeUseCase().collectLatest {
				novelUIType = it
			}
		}
	}

	private fun setFID(fID: Int): Job = launchIO {
		when {
			iExtension == null -> {
				logI("Loading formatter")
				when (val v = getExtensionUseCase(fID)) {
					is HResult.Success -> {
						iExtension = v.data
						extensionName.postValue(successResult(v.data.name))
						hasSearchLive.postValue(successResult(v.data.hasSearch))
						filterData.putAll(v.data.searchFiltersModel.mapify())
						filterItemsLive.postValue(successResult(v.data.searchFiltersModel.toList()))
					}
					is HResult.Loading -> extensionName.postValue(v)
					is HResult.Error -> extensionName.postValue(v)
					is HResult.Empty -> extensionName.postValue(v)
				}
			}
			iExtension!!.formatterID != fID -> {
				logI("Resetting formatter")
				destroy()
				setFID(fID).join()
			}
			else -> logI("FID are the same, ignoring")
		}
	}

	override fun setExtensionID(fID: Int) {
		setFID(fID)
	}

	override fun setQuery(string: String) {
		this.query = string
	}

	private suspend fun getDataLoaderAndLoad(): HResult<List<ACatalogNovelUI>> {
		return if (query.isEmpty()) {
			logV("Loading listing data")
			getCatalogueListingData(
				iExtension!!,
				filterData.apply {
					this[PAGE_INDEX] = currentMaxPage
				}
			)
		} else {
			logV("Loading query data")
			loadCatalogueQueryDataUseCase(
				iExtension!!,
				query,
				filterData.apply {
					this[PAGE_INDEX] = currentMaxPage
				}
			)
		}
	}

	@Synchronized
	override fun loadData(): Job = launchIO {
		if (iExtension == null) {
			logE("formatter was null")
			this.cancel("Extension not loaded")
			return@launchIO
		}
		currentMaxPage++
		val values = listingItems

		listingItemsLive.postValue(loading())

		getDataLoaderAndLoad().handle(onError = {
			reportError(it)
			logE("Error: ${it.code}|${it.message}", it.error)
		}) { newList ->
			listingItemsLive.postValue(successResult(values + newList))
			listingItems = values.apply { addAll(newList) }
		}
	}

	override fun loadQuery(): Job = launchIO {
		currentMaxPage = 0
		loadingJob.cancel("Loading a query")
		listingItems.clear()
		listingItemsLive.postValue(successResult(arrayListOf()))
		loadingJob = loadData()
	}

	@Synchronized
	override fun loadMore() {
		if (loadingJob.isCompleted)
			loadingJob = loadData()
	}

	override fun resetView() {
		listingItemsLive.postValue(successResult(arrayListOf()))
		listingItems.clear()
		currentMaxPage = 0
		loadData()
	}

	override fun backgroundNovelAdd(novelID: Int) {
		launchIO { backgroundAddUseCase(novelID) }
	}

	override fun setFilters(map: Map<Int, Any>) {
		launchIO {
			filterData.putAll(map)
			listingItems.clear()
			listingItemsLive.postValue(successResult(arrayListOf()))
			loadData()
		}
	}

	override fun destroy() {
		launchIO {
			iExtension = null
			listingItems.clear()
			filterData.clear()
			query = ""
			listingItemsLive.postValue(successResult(arrayListOf()))
			filterItemsLive.postValue(successResult(arrayListOf()))
			hasSearchLive.postValue(successResult(false))
			hasSearchLive.postValue(loading())
			extensionName.postValue(loading())
		}
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) =
		reportExceptionUseCase(error)

	override fun getColumnsInP(): Int = columnP
	override fun getColumnsInH(): Int = columnH
	override fun getNovelUIType(): NovelUIType = novelUIType

}

