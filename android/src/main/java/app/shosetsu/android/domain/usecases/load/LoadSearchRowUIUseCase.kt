package app.shosetsu.android.domain.usecases.load

import app.shosetsu.android.view.uimodels.model.search.SearchRowUI
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.mapLatestResult
import app.shosetsu.common.dto.successResult
import kotlinx.coroutines.flow.Flow

class LoadSearchRowUIUseCase(
	private val iExtensionsRepository: IExtensionsRepository
) {
	operator fun invoke(): Flow<HResult<List<SearchRowUI>>> =
		iExtensionsRepository.loadStrippedExtensionEntityFlow()
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