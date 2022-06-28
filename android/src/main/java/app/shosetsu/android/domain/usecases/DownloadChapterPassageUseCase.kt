package app.shosetsu.android.domain.usecases

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.domain.model.local.DownloadEntity
import app.shosetsu.android.domain.repository.base.IDownloadsRepository
import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.view.uimodels.model.ChapterUI

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
	@Throws(SQLiteException::class)
	suspend operator fun invoke(chapters: List<ChapterEntity>) {
		val first = chapters.first()
		val novel = novelRepo.getNovel(first.novelID)

		if (novel == null) {
			logE("Null novel for id ${first.novelID}")
			return
		}

		downloadsRepository.addDownload(
			chapters.map { (id, url, novelID, extensionID, title) ->
				DownloadEntity(
					id!!,
					novelID,
					url,
					title,
					novel.title,
					extensionID
				)
			}
		)

		if (!novel.bookmarked)
			if (iSettingsRepository.getBoolean(SettingKey.BookmarkOnDownload)) {
				novelRepo.update(
					novel.copy(
						bookmarked = true
					)
				)
			}

	}

	@Throws(SQLiteException::class)
	suspend operator fun invoke(chapterUI: ChapterUI) = invoke(listOf(chapterUI.convertTo()))

	@Throws(SQLiteException::class)
	suspend operator fun invoke(chapterUI: Array<ChapterUI>) {
		invoke(chapterUI.map { it.convertTo() })
	}
}