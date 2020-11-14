package app.shosetsu.android.domain.usecases.load

import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.mapLatestResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.android.view.uimodels.model.search.SearchRowUI
import kotlinx.coroutines.flow.Flow

class LoadSearchRowUIUseCase(
		private val iExtensionsRepository: IExtensionsRepository
) {
	operator fun invoke(): Flow<HResult<List<SearchRowUI>>> = iExtensionsRepository.getCards()
			.mapLatestResult {
				successResult(ArrayList(
						it.map { (id, title, imageURL) ->
							SearchRowUI(id, title, imageURL)
						}
				))
			}
			.mapLatestResult {
				successResult((it).apply {
					add(0, SearchRowUI(-1, "My Library", ""))
				}.toList())
			}
}