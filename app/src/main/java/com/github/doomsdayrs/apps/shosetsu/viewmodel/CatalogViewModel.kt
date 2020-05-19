package com.github.doomsdayrs.apps.shosetsu.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchUI
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.GetFormatterUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.LoadCatalogueData
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.NovelBackgroundAddUseCase
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.IDTitleImageBookUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ICatalogViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
class CatalogViewModel(
		private val getFormatterUseCase: GetFormatterUseCase,
		private val backgroundAddUseCase: NovelBackgroundAddUseCase,
		private val loadCatalogueData: LoadCatalogueData
) : ICatalogViewModel() {
	private val currentList: ArrayList<String> = arrayListOf()
	private val items: MutableLiveData<HResult<List<IDTitleImageBookUI>>> = MutableLiveData()


	override var displayItems: LiveData<HResult<List<IDTitleImageBookUI>>> =
			liveData { emitSource(items) }

	override val formatterData: MutableLiveData<HResult<Formatter>> = MutableLiveData(HResult.Loading)

	override fun setFormatterID(fID: Int) {
		GlobalScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
			if (formatterData.value !is HResult.Success<Formatter>) {
				Log.d(logID(), "Loading formatter $fID")
				formatterData.postValue(getFormatterUseCase(fID))
				loadMore()
			}
		}
	}

	override fun loadData(formatter: Formatter) =
			GlobalScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
				val values = when (val current = items.value ?: successResult(arrayListOf())) {
					is HResult.Success -> current.data
					else -> arrayListOf()
				}
				items.postValue(loading())

				when (val i = loadCatalogueData(formatter, currentMaxPage)) {
					is HResult.Success -> {
						items.postValue(successResult(values + i.data))
					}
					is HResult.Empty -> {
					}
					is HResult.Error -> {
					}
					is HResult.Empty -> {
					}
				}
			}

	override fun loadQuery(query: String) {
	}

	override fun loadMore() {
		launchIO {

		}
	}

	override fun searchPage(query: String) {
		launchIO {

		}
	}

	override fun resetView(formatter: Formatter) {
		items.postValue(successResult(arrayListOf()))
		loadData(formatter)
	}

	override fun backgroundNovelAdd(novelID: Int) {
		launchUI { backgroundAddUseCase(novelID) }
	}
}