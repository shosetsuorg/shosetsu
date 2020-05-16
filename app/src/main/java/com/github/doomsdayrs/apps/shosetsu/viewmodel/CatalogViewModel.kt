package com.github.doomsdayrs.apps.shosetsu.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.GetFormatterUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.LoadCatalogueData
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.NovelBackgroundAddUseCase
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.IDTitleImageBookUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ICatalogViewModel
import kotlinx.coroutines.Dispatchers

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
		private val loadCatalogueData: LoadCatalogueData
) : ICatalogViewModel() {
	val currentList: ArrayList<IDTitleImageBookUI> = arrayListOf()
	override var displayItems: MutableLiveData<HResult<List<IDTitleImageBookUI>>> = MutableLiveData()

	private lateinit var formatter: Formatter
	override val formatterData: MutableLiveData<HResult<Formatter>> = MutableLiveData()

	override fun setFormatterID(formatterID: Int) {
		if (formatterData.value == null)
			liveData<Any>(viewModelScope.coroutineContext + Dispatchers.Unconfined) {
				when (val result = getFormatterUseCase.invoke(formatterID)) {
					is HResult.Success ->
						formatterData.postValue(result)
					else -> throw Exception("What the fuck")
				}
			}
	}

	override fun loadData() {
		launchIO {
			displayItems.postValue(loading())
			loadCatalogueData(formatter, currentMaxPage)
			displayItems.postValue(successResult(currentList))
		}
	}

	override fun loadQuery(query: String) {
		launchIO {

		}
	}

	override fun loadMore() {
		launchIO {

		}
	}

	override fun searchPage(query: String) {
		launchIO {

		}
	}

	override fun resetView() {
		launchIO {
			displayItems.postValue(successResult(arrayListOf()))
			displayItems.postValue(loading())
			loadData()
		}
	}

	override fun backgroundNovelAdd(novelID: Int) = backgroundAddUseCase(novelID)
}