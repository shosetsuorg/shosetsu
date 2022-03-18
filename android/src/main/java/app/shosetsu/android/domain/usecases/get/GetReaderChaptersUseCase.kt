package app.shosetsu.android.domain.usecases.get

import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem.ReaderChapterUI
import app.shosetsu.common.consts.settings.SettingKey.ReaderStringToHtml
import app.shosetsu.common.domain.repositories.base.IChaptersRepository
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.domain.repositories.base.INovelsRepository
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

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
 * 07 / 06 / 2020
 */
class GetReaderChaptersUseCase(
	private val chapterRepo: IChaptersRepository,
	private val settingsRepo: ISettingsRepository,
	private val extRepo: IExtensionsRepository,
	private val novelRepo: INovelsRepository
) {
	operator fun invoke(novelID: Int): Flow<List<ReaderChapterUI>> =
		flow {
			emitAll(
				novelRepo.getNovelFlow(novelID)
					.combine(chapterRepo.getReaderChaptersFlow(novelID)) { novel, chapters ->
						novel to chapters
					}
					.combine(settingsRepo.getBooleanFlow(ReaderStringToHtml)) { (novel, list), convertToHtml ->
						if (novel != null) {
							val extensionEntity =
								extRepo.getInstalledExtension(novel.extensionID)

							if (extensionEntity != null) {
								list.map { (id, url, title, readingPosition, readingStatus, bookmarked) ->
									ReaderChapterUI(
										id,
										url,
										title,
										readingPosition,
										readingStatus,
										bookmarked,
										extensionEntity.chapterType,
										convertToHtml
									)
								}
							} else emptyList()
						} else emptyList()
					}
			)
		}
}