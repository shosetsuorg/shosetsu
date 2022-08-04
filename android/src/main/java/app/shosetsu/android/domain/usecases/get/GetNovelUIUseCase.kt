package app.shosetsu.android.domain.usecases.get

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.utils.uifactory.NovelConversionFactory
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.android.view.uimodels.model.NovelUI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

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
) {
	@Throws(SQLiteException::class)
	@OptIn(ExperimentalCoroutinesApi::class)
	operator fun invoke(novelID: Int): Flow<NovelUI?> = flow {
		if (novelID != -1)
			emitAll(novelsRepository.getNovelFlow(novelID).mapLatest {
				it?.let { novelEntity ->
					(NovelConversionFactory(novelEntity).convertTo())
				}
			}.map { novelUI ->
				if (novelUI != null)
					extensionRepository.getInstalledExtension(novelUI.extID)?.let { ext ->
						novelUI.copy(
							extName = ext.name
						)
					} ?: novelUI
				else null
			})
	}
}