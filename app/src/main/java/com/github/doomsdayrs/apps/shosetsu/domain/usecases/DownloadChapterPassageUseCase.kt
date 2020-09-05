package com.github.doomsdayrs.apps.shosetsu.domain.usecases

import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.DownloadEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IDownloadsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.INovelsRepository
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.ChapterUI

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
 * 14 / 05 / 2020
 * Downloads a specific chapter
 */
class DownloadChapterPassageUseCase(
		private val novelRepo: INovelsRepository,
		private val downloadsRepository: IDownloadsRepository,
		private val startDownloadWorkerUseCase: StartDownloadWorkerUseCase,
) {
	suspend operator fun invoke(chapterUI: ChapterUI) {
		novelRepo.loadNovel(chapterUI.novelID).let {
			if (it is HResult.Success) {
				val novel = it.data
				downloadsRepository.addDownload(DownloadEntity(
						chapterUI.id,
						chapterUI.novelID,
						chapterUI.link,
						chapterUI.title,
						novel.title,
						chapterUI.formatterID
				))
				startDownloadWorkerUseCase()
			}
		}
	}
}