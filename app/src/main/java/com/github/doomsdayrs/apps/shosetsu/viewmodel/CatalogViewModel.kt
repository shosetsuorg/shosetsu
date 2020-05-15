package com.github.doomsdayrs.apps.shosetsu.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.defaultListing
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.domain.repository.model.NovelsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.GetFormatterUseCase
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
		private var backgroundAddUseCase: NovelBackgroundAddUseCase
) : ICatalogViewModel() {
	inner class PageLoader(
			val currentMaxPage: Int = 1,
			val formatter: Formatter,
			val filterValues: Array<*>,
			val selectedListing: Int = formatter.defaultListing,
			val novelsRepository: NovelsRepository
	) {
		/*	suspend fun execute() {
				try {
					val loader = CatalogueLoader(formatter, filterValues, selectedListing)
					val novels =
							if (v.isNotEmpty())
								loader.execute(v[0])
							else loader.execute()
					it.recyclerArray.addAll(novels.map {
						with(it) {
							NovelListingCard(imageURL, title, ID, link)
						}
					})
					Log.d("FragmentRefresh", "Complete")
					true
				} catch (e: LuaError) {
					catalogController.activity?.toast(e.smallMessage())
					Log.e("CataloguePageLoader", e.message ?: "UNKNOWN ERROR")
					false
				} catch (e: Exception) {
					catalogController.activity?.toast(e.message ?: "UNKNOWN ERROR")
					false
				}
			}

		 */
	}


	val currentList: ArrayList<IDTitleImageBookUI> = arrayListOf()
	override var displayItems: MutableLiveData<HResult<List<IDTitleImageBookUI>>> = MutableLiveData()

	override val formatter: MutableLiveData<Formatter> = MutableLiveData()

	override fun setFormatterID(formatterID: Int) {
		liveData<Any>(viewModelScope.coroutineContext + Dispatchers.Unconfined) {
			when (val result = getFormatterUseCase.invoke(formatterID)) {
				is HResult.Success ->
					formatter.postValue(result.data)
				else -> throw Exception("What the fuck")
			}
		}
	}

	override fun loadData() {
		launchIO {
			displayItems.postValue(loading())

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