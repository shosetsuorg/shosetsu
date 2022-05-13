package app.shosetsu.android.domain.usecases.get

import app.shosetsu.android.domain.model.local.NovelEntity
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.lib.IExtension.Companion.KEY_CHAPTER_URL
import app.shosetsu.lib.IExtension.Companion.KEY_NOVEL_URL

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
 *
 * 27 / 09 / 2021
 *
 *  Get the URL of a novel / chapter
 */
class GetURLUseCase(private val getExt: GetExtensionUseCase) {
	suspend operator fun invoke(url: String, formatterID: Int, type: Int): String? =
		getExt(formatterID)?.expandURL(url, type)

	suspend operator fun invoke(novelUI: NovelUI): String? =
		this(novelUI.novelURL, novelUI.extID, KEY_NOVEL_URL)

	suspend operator fun invoke(novelUI: NovelEntity): String? =
		this(novelUI.url, novelUI.extensionID, KEY_NOVEL_URL)

	suspend operator fun invoke(chapterUI: ChapterUI): String? =
		this(chapterUI.link, chapterUI.extensionID, KEY_CHAPTER_URL)
}