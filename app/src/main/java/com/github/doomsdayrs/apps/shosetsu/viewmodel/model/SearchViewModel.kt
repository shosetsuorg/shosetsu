package com.github.doomsdayrs.apps.shosetsu.viewmodel.model

import androidx.lifecycle.*
import com.github.doomsdayrs.apps.shosetsu.common.dto.*
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IExtensionsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.SearchBookMarkedNovelsUseCase
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.catlog.ACatalogNovelUI
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.catlog.FullCatalogNovelUI
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.search.SearchRowUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.ISearchViewModel
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
class SearchViewModel(
		private val searchBookMarkedNovelsUseCase: SearchBookMarkedNovelsUseCase,
		private val iExtensionsRepository: IExtensionsRepository,
) : ISearchViewModel() {
	private val hashMap = HashMap<Int, LiveData<HResult<List<ACatalogNovelUI>>>>()

	override val listings: LiveData<HResult<List<SearchRowUI>>> by lazy {
		liveData {
			emit(loading())
			emitSource(iExtensionsRepository.getCards().map {
				when (it) {
					is HResult.Success -> {
						successResult(ArrayList(
								it.data.map { (id, title, imageURL) ->
									SearchRowUI(id, title, imageURL)
								}
						))
					}
					is HResult.Error -> it
					is HResult.Empty -> it
					is HResult.Loading -> it
				}
			}.switchMap { result ->
				liveData {
					emit(result.let {
						when (it) {
							is HResult.Success -> successResult((it.data).apply {
								add(0, SearchRowUI(-1, "My Library", ""))
							}.toList())
							is HResult.Loading -> it
							is HResult.Empty -> it
							is HResult.Error -> it
						}
					})
				}
			})
		}
	}

	private var query: String = ""

	override fun setQuery(query: String) {
		this.query = query
	}

	override fun searchLibrary(): LiveData<HResult<List<ACatalogNovelUI>>> =
			liveData(viewModelScope.coroutineContext + Dispatchers.Default) {
				emit(loading())
				emitSource(searchBookMarkedNovelsUseCase(query).map {
					when (it) {
						is HResult.Success -> {
							successResult(it.data.map { (id, title, imageURL) ->
								FullCatalogNovelUI(id, title, imageURL, false)
							})
						}
						HResult.Loading -> loading()
						HResult.Empty -> emptyResult()
						is HResult.Error -> errorResult(it.code, it.message, it.error)
					}
				})
			}

	override fun loadQuery() {
	}

	override fun searchFormatter(formatterID: Int): LiveData<HResult<List<ACatalogNovelUI>>> {
		if (!hashMap.containsKey(formatterID)) {
			hashMap[formatterID] = MutableLiveData(loading())
		}
		return hashMap[formatterID]!!
	}
}