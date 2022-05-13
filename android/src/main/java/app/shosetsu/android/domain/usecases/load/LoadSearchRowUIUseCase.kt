package app.shosetsu.android.domain.usecases.load

import app.shosetsu.android.common.ext.generify
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.view.uimodels.model.search.SearchRowUI
import app.shosetsu.android.common.IncompatibleExtensionException
import app.shosetsu.common.domain.model.local.InstalledExtensionEntity
import app.shosetsu.android.domain.repository.base.IExtensionEntitiesRepository
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transformLatest

class LoadSearchRowUIUseCase(
	private val iExtensionsRepository: IExtensionsRepository,
	private val extEntitiesRepo: IExtensionEntitiesRepository
) {
	@OptIn(ExperimentalCoroutinesApi::class)
	operator fun invoke(): Flow<List<SearchRowUI>> =
		iExtensionsRepository.loadExtensionsFLow()
			.transformLatest { result ->
				emit(
					result.let { list ->
						val arrayList = arrayListOf<InstalledExtensionEntity>()
						list.forEach { extension ->
							try {
								extEntitiesRepo.get(extension.generify()).let { entity ->
									if (entity.hasSearch) {
										arrayList.add(extension)
									}
								}
							} catch (e: IncompatibleExtensionException) {
								logE("Incompatible extension, ignoring", e)
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