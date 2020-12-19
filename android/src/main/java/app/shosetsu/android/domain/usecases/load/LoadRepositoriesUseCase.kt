package app.shosetsu.android.domain.usecases.load

import app.shosetsu.android.common.utils.uifactory.mapLatestToResultFlowWithFactory
import app.shosetsu.android.view.uimodels.model.RepositoryUI
import app.shosetsu.common.domain.repositories.base.IExtRepoRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.loading
import app.shosetsu.common.dto.mapLatestResultListTo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * shosetsu
 * 16 / 09 / 2020
 */
class LoadRepositoriesUseCase(
	private val iExtRepoRepository: IExtRepoRepository
) {
	@ExperimentalCoroutinesApi
	operator fun invoke(): Flow<HResult<List<RepositoryUI>>> = flow {
		emit(loading())
		emitAll(
			iExtRepoRepository.loadRepositoriesLive().mapLatestToResultFlowWithFactory()
				.mapLatestResultListTo()
		)
	}
}