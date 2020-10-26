package app.shosetsu.android.domain.usecases.get

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import app.shosetsu.android.common.dto.*
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.android.view.uimodels.model.NovelUI
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
 * 18 / 05 / 2020
 */
class GetNovelUIUseCase(
		private val novelsRepository: INovelsRepository,
		private val extensionRepository: IExtensionsRepository
) : ((@ParameterName("novelID") Int) -> LiveData<HResult<NovelUI>>) {
	override fun invoke(novelID: Int): LiveData<HResult<NovelUI>> = liveData(context = Dispatchers.IO) {
		emit(loading())
		if (novelID != -1)
			emitSource(novelsRepository.loadNovelLive(novelID).map { it.mapTo() }.switchMap { novelUIResult ->
				liveData(context = Dispatchers.IO) {
					emit(novelUIResult.handleReturn { novelUI ->
						extensionRepository.getExtensionEntity(novelUI.extID).handleReturn { ext ->
							successResult(novelUI.apply {
								extName = ext.name
							})
						}
					})
				}
			})

	}
}