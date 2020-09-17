package app.shosetsu.android.domain.usecases.load

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.handleReturn
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.android.view.uimodels.model.search.SearchRowUI

class LoadSearchRowUIUseCase(
		private val iExtensionsRepository: IExtensionsRepository
) {
	operator fun invoke(): LiveData<HResult<List<SearchRowUI>>> = iExtensionsRepository.getCards().map { hResult ->
		hResult.handleReturn {
			successResult(ArrayList(
					it.map { (id, title, imageURL) ->
						SearchRowUI(id, title, imageURL)
					}
			))
		}
	}.switchMap { result ->
		liveData {
			emit(result.handleReturn {
				successResult((it).apply {
					add(0, SearchRowUI(-1, "My Library", ""))
				}.toList())
			})
		}
	}
}