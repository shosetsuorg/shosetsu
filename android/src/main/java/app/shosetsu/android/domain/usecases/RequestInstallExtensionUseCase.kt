package app.shosetsu.android.domain.usecases

import androidx.work.Data
import app.shosetsu.android.backend.workers.onetime.ExtensionInstallWorker
import app.shosetsu.android.backend.workers.onetime.ExtensionInstallWorker.Companion.KEY_EXTENSION_ID
import app.shosetsu.android.backend.workers.onetime.ExtensionInstallWorker.Companion.KEY_REPOSITORY_ID
import app.shosetsu.android.common.enums.DownloadStatus
import app.shosetsu.android.domain.model.local.ExtensionInstallOptionEntity
import app.shosetsu.android.domain.repository.base.IExtensionDownloadRepository
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
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
 * shosetsu
 * 13 / 05 / 2020
 */
class RequestInstallExtensionUseCase(
	private val extRepo: IExtensionsRepository,
	private val repo: IExtensionDownloadRepository,
	private val manager: ExtensionInstallWorker.Manager
) {
	suspend operator fun invoke(
		extension: BrowseExtensionUI,
		option: ExtensionInstallOptionEntity
	) =
		invoke(extension.id, option.repoId)

	/**
	 * Update an extension
	 */
	suspend operator fun invoke(
		extension: BrowseExtensionUI,
	) =
		invoke(extension.id, extension.installedRepo)

	suspend operator fun invoke(id: Int, repoId: Int) {
		val doIt = suspend {
			repo.add(id)
			manager.start(
				Data.Builder().apply {
					putInt(KEY_EXTENSION_ID, id)
					putInt(KEY_REPOSITORY_ID, repoId)
				}.build()
			)
		}

		when (val status = repo.getStatus(id)) {
			DownloadStatus.WAITING -> doIt()
			else -> {
				if (status == DownloadStatus.ERROR || status == DownloadStatus.COMPLETE)
					doIt()
			}
		}
	}
}