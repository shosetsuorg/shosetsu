package com.github.doomsdayrs.apps.shosetsu.domain.usecases.load

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IExtensionsRepository
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.search.SearchRowUI

class LoadSearchRowUIUseCase(
		private val iExtensionsRepository: IExtensionsRepository
) {
	operator fun invoke(): LiveData<HResult<List<SearchRowUI>>> = iExtensionsRepository.getCards().map {
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
	}
}