package app.shosetsu.android.domain.usecases

import app.shosetsu.android.common.ext.ifSo
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.domain.model.local.DownloadEntity
import app.shosetsu.common.domain.repositories.base.IDownloadsRepository
import app.shosetsu.common.domain.repositories.base.INovelsRepository
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.handle

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
 *
 *
 * Takes either a [ChapterEntity] or a [ChapterUI] and downloads it
 */
class DownloadChapterPassageUseCase(
	private val novelRepo: INovelsRepository,
	private val downloadsRepository: IDownloadsRepository,
	private var iSettingsRepository: ISettingsRepository
) {
	suspend operator fun invoke(chapterUI: ChapterEntity) {
		novelRepo.getNovel(chapterUI.novelID).handle { novel ->
			downloadsRepository.addDownload(
				DownloadEntity(
					chapterUI.id!!,
					chapterUI.novelID,
					chapterUI.url,
					chapterUI.title,
					novel.title,
					chapterUI.extensionID
				)
			)

			if (!novel.bookmarked)
				iSettingsRepository.getBoolean(SettingKey.BookmarkOnDownload).handle {
					it ifSo novelRepo.update(
						novel.copy(
							bookmarked = true
						)
					)
				}
		}
	}

	suspend operator fun invoke(chapterUI: ChapterUI) = invoke(chapterUI.convertTo())

	operator fun invoke(vararg chapterUI: ChapterUI) {
		launchIO {
			chapterUI.forEach {
				invoke(it)
			}
		}
	}
}