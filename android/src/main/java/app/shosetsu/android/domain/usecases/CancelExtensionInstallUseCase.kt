package app.shosetsu.android.domain.usecases

import app.shosetsu.android.domain.repository.base.IExtensionDownloadRepository
import app.shosetsu.android.view.uimodels.model.BrowseExtensionUI

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
 * Shosetsu
 *
 * @since 28 / 07 / 2021
 * @author Doomsdayrs
 */
class CancelExtensionInstallUseCase(
	private val repo: IExtensionDownloadRepository
) {

	suspend operator fun invoke(extension: BrowseExtensionUI) {
		repo.remove(extension.id)
	}
}