package app.shosetsu.android.domain.usecases.load

import app.shosetsu.android.view.uimodels.model.search.SearchRowUI
import app.shosetsu.common.domain.model.local.GenericExtensionEntity
import app.shosetsu.common.domain.repositories.base.IExtensionEntitiesRepository
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.dto.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest

class LoadSearchRowUIUseCase(
	private val iExtensionsRepository: IExtensionsRepository,
	private val extEntitiesRepo: IExtensionEntitiesRepository
) {
	operator fun invoke(): Flow<HResult<List<SearchRowUI>>> =
		iExtensionsRepository.loadExtensionsFLow()
			.transformLatest { result ->
				emit(
					result.transform { list ->
						val arrayList = arrayListOf<GenericExtensionEntity>()
						list.forEach { extension ->
							extEntitiesRepo.get(extension).handle { entity ->
								if (entity.hasSearch) {
									arrayList.add(extension)
								}
							}
						}
						successResult(arrayList.map {
							SearchRowUI(it.id, it.name, it.imageURL)
						})
					}
				)
			}
			.mapLatestResult {
				successResult(
					ArrayList(it).apply {
						add(0, SearchRowUI(-1, "My Library", ""))
					}
				)
			}.mapLatestResult { list ->
				successResult(list.sortedBy { it.name })
			}
}