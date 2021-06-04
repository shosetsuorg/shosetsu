package app.shosetsu.android.domain.usecases

import app.shosetsu.android.domain.usecases.start.StartDownloadWorkerUseCase
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform

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
 * 03 / 06 / 2021
 *
 * Start the download worker after an update
 *
 * Will not download the chapters if [SettingKey.IsDownloadOnUpdate]=false
 *
 */
class StartDownloadWorkerAfterUpdateUseCase(
	private val sR: ISettingsRepository,
	private val download: DownloadChapterPassageUseCase,
	private val startDownloadWorker: StartDownloadWorkerUseCase
) {

	/**
	 * @return true if updating, false otherwise
	 */
	suspend operator fun invoke(chapters: List<ChapterEntity>): HResult<Boolean> =
		sR.getBoolean(SettingKey.IsDownloadOnUpdate).transform { isDownloadOnUpdate ->
			if (isDownloadOnUpdate) {
				chapters.forEach { download(it) }
				startDownloadWorker()
				successResult(true)
			} else
				successResult(false)
		}
}