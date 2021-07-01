package app.shosetsu.android.domain.usecases

import androidx.work.Data
import app.shosetsu.android.backend.workers.onetime.ExtensionInstallWorker
import app.shosetsu.android.backend.workers.onetime.ExtensionInstallWorker.Companion.KEY_EXTENSION_ID
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.common.domain.repositories.base.IExtensionDownloadRepository
import app.shosetsu.common.dto.*
import app.shosetsu.common.enums.DownloadStatus

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
class InstallExtensionUIUseCase(
	private val repo: IExtensionDownloadRepository,
	private val manager: ExtensionInstallWorker.Manager
) {
	suspend operator fun invoke(extension: ExtensionUI): HResult<*> {
		val doIt = suspend {
			repo.add(extension.id).ifSo {
				successResult(
					manager.start(
						Data.Builder().apply {
							putInt(KEY_EXTENSION_ID, extension.id)
						}.build()
					)
				)
			}
		}
		return repo.getStatus(extension.id).transform(
			onEmpty = {
				doIt()
			}
		) {
			if (it == DownloadStatus.ERROR || it == DownloadStatus.COMPLETE)
				doIt()
			else emptyResult()
		}
	}
}