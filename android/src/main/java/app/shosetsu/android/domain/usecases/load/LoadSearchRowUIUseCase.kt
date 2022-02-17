package app.shosetsu.android.domain.usecases.load

import app.shosetsu.android.common.ext.generify
import app.shosetsu.android.view.uimodels.model.search.SearchRowUI
import app.shosetsu.common.domain.model.local.GenericExtensionEntity
import app.shosetsu.common.domain.repositories.base.IExtensionEntitiesRepository
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transformLatest

class LoadSearchRowUIUseCase(
	private val iExtensionsRepository: IExtensionsRepository,
	private val extEntitiesRepo: IExtensionEntitiesRepository
) {
	operator fun invoke(): Flow<List<SearchRowUI>> =
		iExtensionsRepository.loadExtensionsFLow()
			.transformLatest { result ->
				emit(
					result.let { list ->
						val arrayList = arrayListOf<GenericExtensionEntity>()
						list.forEach { extension ->
							extEntitiesRepo.get(extension.generify()).let { entity ->
								if (entity.hasSearch) {
									arrayList.add(extension.generify())
								}
							}
						}
						arrayList.map {
							SearchRowUI(it.id, it.name, it.imageURL)
						}
					}
				)
			}
			.mapLatest { list ->
				ArrayList(list).apply {
					add(0, SearchRowUI(-1, "My Library", ""))
					sortBy { it.name }
				}
			}
}