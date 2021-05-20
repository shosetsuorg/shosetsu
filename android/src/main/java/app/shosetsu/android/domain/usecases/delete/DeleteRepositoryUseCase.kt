package app.shosetsu.android.domain.usecases.delete

import app.shosetsu.android.view.uimodels.model.RepositoryUI
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.domain.repositories.base.IExtensionRepoRepository
import app.shosetsu.common.dto.HResult

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
 * 18 / 01 / 2021
 */
class DeleteRepositoryUseCase(
	private val iExtensionRepoRepository: IExtensionRepoRepository
) {
	suspend operator fun invoke(repositoryEntity: RepositoryEntity): HResult<*> =
		iExtensionRepoRepository.remove(repositoryEntity)

	suspend operator fun invoke(repositoryUI: RepositoryUI) = invoke(repositoryUI.convertTo())
}