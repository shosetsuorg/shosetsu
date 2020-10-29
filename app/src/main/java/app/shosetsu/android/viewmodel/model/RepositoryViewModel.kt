package app.shosetsu.android.viewmodel.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.load.LoadRepositoriesUseCase
import app.shosetsu.android.view.uimodels.model.RepositoryUI
import app.shosetsu.android.viewmodel.abstracted.ARepositoryViewModel
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
class RepositoryViewModel(
		private val loadRepositoriesUseCase: LoadRepositoriesUseCase,
		private val reportExceptionUseCase: ReportExceptionUseCase
) : ARepositoryViewModel() {
	override fun addRepository(url: String) {
	}

	override fun isURL(string: String): Boolean {
		return false
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	override fun remove(repositoryInfoUI: RepositoryUI) {
	}

	override val liveData: LiveData<HResult<List<RepositoryUI>>> by lazy {
		loadRepositoriesUseCase().asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
	}
}