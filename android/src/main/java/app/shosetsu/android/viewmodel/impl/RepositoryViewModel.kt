package app.shosetsu.android.viewmodel.impl

import androidx.lifecycle.LiveData
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.AddRepositoryUseCase
import app.shosetsu.android.domain.usecases.ForceInsertRepositoryUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.StartRepositoryUpdateManagerUseCase
import app.shosetsu.android.domain.usecases.delete.DeleteRepositoryUseCase
import app.shosetsu.android.domain.usecases.load.LoadRepositoriesUseCase
import app.shosetsu.android.domain.usecases.update.UpdateRepositoryUseCase
import app.shosetsu.android.view.uimodels.model.RepositoryUI
import app.shosetsu.android.viewmodel.abstracted.ARepositoryViewModel
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class RepositoryViewModel(
	private val loadRepositoriesUseCase: LoadRepositoriesUseCase,
	private val reportExceptionUseCase: ReportExceptionUseCase,
	private val addRepositoryUseCase: AddRepositoryUseCase,
	private val deleteRepositoryUseCase: DeleteRepositoryUseCase,
	private val updateRepositoryUseCase: UpdateRepositoryUseCase,
	private val startRepositoryUpdateManagerUseCase: StartRepositoryUpdateManagerUseCase,
	private val forceInsertRepositoryUseCase: ForceInsertRepositoryUseCase,
	private val isOnlineUseCase: IsOnlineUseCase
) : ARepositoryViewModel() {

	@ExperimentalCoroutinesApi
	override val liveData: LiveData<HResult<List<RepositoryUI>>> by lazy {
		loadRepositoriesUseCase().asIOLiveData()
	}

	override fun addRepository(name: String, url: String) = flow {
		emit(addRepositoryUseCase(RepositoryEntity(url = url, name = name, isEnabled = true)))
	}.asIOLiveData()

	override fun undoRemove(item: RepositoryUI): LiveData<HResult<*>> = flow {
		emit(forceInsertRepositoryUseCase(item))
	}.asIOLiveData()

	override fun isURL(string: String): Boolean {
		return false
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	override fun remove(repositoryInfoUI: RepositoryUI) =
		flow {
			emit(deleteRepositoryUseCase(repositoryInfoUI))
		}.asIOLiveData()

	override fun toggleIsEnabled(repositoryInfoUI: RepositoryUI): LiveData<HResult<Boolean>> =
		flow {
			val newState = !repositoryInfoUI.isRepoEnabled
			emit(updateRepositoryUseCase(repositoryInfoUI.copy(isRepoEnabled = newState)).transform {
				successResult(newState)
			})
		}.asIOLiveData()

	override fun updateRepositories() {
		startRepositoryUpdateManagerUseCase()
	}

	override fun isOnline(): Boolean = isOnlineUseCase()
}