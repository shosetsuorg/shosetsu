package com.github.doomsdayrs.apps.shosetsu.viewmodel.model.catalog

import androidx.lifecycle.MutableLiveData
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchUI
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.GetFormatterUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.LoadCatalogueDataUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.NovelBackgroundAddUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.toast.StringToastUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.toast.ToastErrorUseCase
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.catlog.ACatalogNovelUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.ICatalogViewModel
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
		private val getFormatterUseCase: GetFormatterUseCase,
		private val backgroundAddUseCase: NovelBackgroundAddUseCase,
		private val loadCatalogueData: LoadCatalogueDataUseCase,
		private val stringToastUseCase: StringToastUseCase,
		private var toastErrorUseCase: ToastErrorUseCase,
) : ICatalogViewModel() {
	private val listingItems: HashMap<Int, List<ACatalogNovelUI>> = hashMapOf()

	private var formatter: Formatter? = null

	override val listingItemsLive: MutableLiveData<HResult<List<ACatalogNovelUI>>> by lazy {
		MutableLiveData<HResult<List<ACatalogNovelUI>>>(loading())
	}

	override val extensionName: MutableLiveData<HResult<String>> by lazy {
		MutableLiveData<HResult<String>>(loading())
	}

	override fun setFormatterID(fID: Int) {
		launchIO {
			if (formatter == null) {
				when (val v = getFormatterUseCase(fID)) {
					is HResult.Success -> {
						formatter = v.data
						extensionName.postValue(successResult(v.data.name))
					}
					is HResult.Loading -> extensionName.postValue(v)
					is HResult.Error -> extensionName.postValue(v)
					is HResult.Empty -> extensionName.postValue(v)
				}
			}
		}
	}

	/**
	 * Current loading job
	 */
	private var loadingJob: Job = launchIO { }

	@Synchronized
	override fun loadData(): Job = launchIO {
		if (formatter == null) this.cancel("Extension not loaded")
		checkNotNull(formatter)
		currentMaxPage++
		val values = listingItems[0] ?: arrayListOf()

		listingItemsLive.postValue(loading())

		when (val i: HResult<List<ACatalogNovelUI>> = loadCatalogueData(
				formatter!!,
				currentMaxPage
		)) {
			is HResult.Success -> {
				listingItems[0] = values + i.data
				listingItemsLive.postValue(successResult(values + i.data))
			}
			is HResult.Empty -> {
			}
			is HResult.Error -> toastErrorUseCase<CatalogViewModel>(i)
		}
	}

	override fun loadQuery(query: String) {
		if (formatter?.hasSearch == true) {

		} else stringToastUseCase { "No search functionality" }
	}

	@Synchronized
	override fun loadMore() {
		if (loadingJob.isCompleted) loadingJob = launchIO { loadData().join() }
	}

	override fun searchPage(query: String) {
		launchIO {
		}
	}

	override fun resetView() {
		listingItemsLive.postValue(successResult(arrayListOf()))
		listingItems.clear()
		currentMaxPage = 0
		loadData()
	}

	override fun backgroundNovelAdd(novelID: Int) {
		launchUI { backgroundAddUseCase(novelID) }
	}
}

