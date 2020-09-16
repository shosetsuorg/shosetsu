package app.shosetsu.android.domain.usecases.load

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.loading
import app.shosetsu.android.common.dto.mapListTo
import app.shosetsu.android.domain.repository.base.IExtRepoRepository
import app.shosetsu.android.view.uimodels.model.RepositoryUI
import kotlinx.coroutines.Dispatchers

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
	operator fun invoke(): LiveData<HResult<List<RepositoryUI>>> = liveData(context = Dispatchers.IO) {
		emit(loading())
		emitSource(iExtRepoRepository.loadRepositoriesLive().map { it.mapListTo() })
	}
}