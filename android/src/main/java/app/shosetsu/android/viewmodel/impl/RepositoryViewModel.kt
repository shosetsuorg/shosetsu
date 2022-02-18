package app.shosetsu.android.viewmodel.impl

import app.shosetsu.android.domain.usecases.AddRepositoryUseCase
import app.shosetsu.android.domain.usecases.ForceInsertRepositoryUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.StartRepositoryUpdateManagerUseCase
import app.shosetsu.android.domain.usecases.delete.DeleteRepositoryUseCase
import app.shosetsu.android.domain.usecases.load.LoadRepositoriesUseCase
import app.shosetsu.android.domain.usecases.update.UpdateRepositoryUseCase
import app.shosetsu.android.view.uimodels.model.RepositoryUI
import app.shosetsu.android.viewmodel.abstracted.ARepositoryViewModel
import kotlinx.coroutines.flow.Flow
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
	private val addRepositoryUseCase: AddRepositoryUseCase,
	private val deleteRepositoryUseCase: DeleteRepositoryUseCase,
	private val updateRepositoryUseCase: UpdateRepositoryUseCase,
	private val startRepositoryUpdateManagerUseCase: StartRepositoryUpdateManagerUseCase,
	private val forceInsertRepositoryUseCase: ForceInsertRepositoryUseCase,
	private val isOnlineUseCase: IsOnlineUseCase
) : ARepositoryViewModel() {

	override val liveData: Flow<List<RepositoryUI>> by lazy {
		loadRepositoriesUseCase()
	}

	override fun addRepository(name: String, url: String) = flow {
		addRepositoryUseCase(url = url, name = name)
		emit(Unit)
	}

	override fun undoRemove(item: RepositoryUI): Flow<Unit> = flow {
		forceInsertRepositoryUseCase(item)
		emit(Unit)
	}

	override fun isURL(string: String): Boolean {
		return false
	}

	override fun remove(repositoryInfoUI: RepositoryUI) =
		flow {
			emit(deleteRepositoryUseCase(repositoryInfoUI))
		}

	override fun toggleIsEnabled(repositoryInfoUI: RepositoryUI): Flow<Boolean> =
		flow {
			val newState = !repositoryInfoUI.isRepoEnabled
			emit(updateRepositoryUseCase(repositoryInfoUI.copy(isRepoEnabled = newState)).let {
				newState
			})
		}

	override fun updateRepositories() {
		startRepositoryUpdateManagerUseCase()
	}

	override fun isOnline(): Boolean = isOnlineUseCase()
}