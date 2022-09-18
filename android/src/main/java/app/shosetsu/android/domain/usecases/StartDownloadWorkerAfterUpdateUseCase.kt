package app.shosetsu.android.domain.usecases

import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.SettingKey.ExcludedCategoriesToDownload
import app.shosetsu.android.common.SettingKey.IncludeCategoriesToDownload
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.domain.usecases.get.GetNovelCategoriesUseCase
import app.shosetsu.android.domain.usecases.start.StartDownloadWorkerUseCase
import kotlinx.coroutines.flow.first

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
 * Will not download the chapters if [SettingKey.DownloadNewNovelChapters]=false
 *
 */
class StartDownloadWorkerAfterUpdateUseCase(
	private val sR: ISettingsRepository,
	private val download: DownloadChapterPassageUseCase,
	private val startDownloadWorker: StartDownloadWorkerUseCase,
	private val getNovelCategoriesUseCase: GetNovelCategoriesUseCase
) {

	/**
	 * @return true if updating, false otherwise
	 */
	suspend operator fun invoke(chapters: List<ChapterEntity>): Boolean =
		sR.getBoolean(SettingKey.DownloadNewNovelChapters).let { isDownloadOnUpdate ->
			if (isDownloadOnUpdate) {
				val includedToDownload = sR.getStringSet(IncludeCategoriesToDownload)
					.map(String::toInt)
				val excludedToDownload = sR.getStringSet(ExcludedCategoriesToDownload)
					.map(String::toInt)
				val filteredChapters = chapters
					.groupBy { it.novelID }
					.filter { (novelID) ->
						val categories = getNovelCategoriesUseCase(novelID).first().ifEmpty { listOf(0) }
						categories.any { includedToDownload.isEmpty() || it in includedToDownload } &&
							categories.none { it in excludedToDownload }
					}
					.values
					.flatten()

				download(filteredChapters)
				startDownloadWorker()
				true
			} else
				false
		}
}