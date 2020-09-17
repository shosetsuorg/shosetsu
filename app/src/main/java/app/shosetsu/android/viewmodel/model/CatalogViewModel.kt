package app.shosetsu.android.viewmodel.model

import androidx.lifecycle.MutableLiveData
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.loading
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.domain.usecases.NovelBackgroundAddUseCase
import app.shosetsu.android.domain.usecases.load.LoadCatalogueListingDataUseCase
import app.shosetsu.android.domain.usecases.load.LoadCatalogueQueryDataUseCase
import app.shosetsu.android.domain.usecases.load.LoadFormatterUseCase
import app.shosetsu.android.domain.usecases.toast.ToastErrorUseCase
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.viewmodel.abstracted.ICatalogViewModel
import app.shosetsu.lib.Filter
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.mapify
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

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
		private val getFormatterUseCase: LoadFormatterUseCase,
		private val backgroundAddUseCase: NovelBackgroundAddUseCase,
		private val loadCatalogueListingData: LoadCatalogueListingDataUseCase,
		private val loadCatalogueQueryDataUseCase: LoadCatalogueQueryDataUseCase,
		private var toastErrorUseCase: ToastErrorUseCase,
) : ICatalogViewModel() {
	private var formatter: Formatter? = null

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


	private fun setFID(fID: Int): Job = launchIO {
		when {
			formatter == null -> {
				this@CatalogViewModel.logI("Loading formatter")
				when (val v = getFormatterUseCase(fID)) {
					is HResult.Success -> {
						formatter = v.data
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
			formatter!!.formatterID != fID -> {
				this@CatalogViewModel.logI("Resetting formatter")
				destroy()
				setFID(fID).join()
			}
			else -> this@CatalogViewModel.logI("FID are the same, ignoring")
		}
	}

	override fun setFormatterID(fID: Int) {
		setFID(fID)
	}

	/**
	 * Current loading job
	 */
	private var loadingJob: Job = launchIO { }

	override fun setQuery(string: String) {
		this.query = string
	}

	@Synchronized
	override fun loadData(): Job = launchIO {
		if (formatter == null) this.cancel("Extension not loaded")
		checkNotNull(formatter)
		currentMaxPage++
		val values = listingItems

		listingItemsLive.postValue(loading())

		when (val i: HResult<List<ACatalogNovelUI>> = if (query.isEmpty()) loadCatalogueListingData(
				formatter!!,
				currentMaxPage,
				filterData
		) else loadCatalogueQueryDataUseCase(
				formatter!!,
				query,
				currentMaxPage,
				filterData
		)) {
			is HResult.Success -> {
				listingItems = values.also { it.addAll(i.data) }
				listingItemsLive.postValue(successResult(values + i.data))
			}
			is HResult.Empty -> {
			}
			is HResult.Error -> {
				toastErrorUseCase<CatalogViewModel>(i)
				logE("Error: ${i.code}|${i.message}", i.error)
			}
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
			formatter = null
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
}

