package app.shosetsu.android.domain.usecases.load

import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.handleReturn
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.android.view.uimodels.model.search.SearchRowUI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class LoadSearchRowUIUseCase(
		private val iExtensionsRepository: IExtensionsRepository
) {
	operator fun invoke(): Flow<HResult<List<SearchRowUI>>> = iExtensionsRepository.getCards()
			.mapLatest { hResult ->
				hResult.handleReturn {
					successResult(ArrayList(
							it.map { (id, title, imageURL) ->
								SearchRowUI(id, title, imageURL)
							}
					))
				}
			}
			.mapLatest { result ->
				result.handleReturn {
					successResult((it).apply {
						add(0, SearchRowUI(-1, "My Library", ""))
					}.toList())
				}
			}
}