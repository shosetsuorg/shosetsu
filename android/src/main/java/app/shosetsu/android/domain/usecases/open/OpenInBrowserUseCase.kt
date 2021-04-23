package app.shosetsu.android.domain.usecases.open

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.domain.usecases.get.GetExtensionUseCase
import app.shosetsu.android.domain.usecases.toast.StringToastUseCase
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.common.dto.handle
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
 * 01 / 09 / 2020
 *
 * Opens the chapter into
 */
class OpenInBrowserUseCase(
	private val getExt: GetExtensionUseCase,
	private val stringToastUseCase: StringToastUseCase,
	private val application: Application,
) {
	operator fun invoke(url: String) {
		Log.d(logID(), "Opening URL $url")
		application.startActivity(Intent(Intent.ACTION_VIEW).apply {
			addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			data = Uri.parse(url)
		})
	}

	suspend operator fun invoke(url: String, extId: Int, type: Int) {
		getExt(extId).handle(
			onEmpty = {
				logE("Empty")
				stringToastUseCase { "Empty??" }
			},
			onError = {
				logE("Error")
				stringToastUseCase { "$it" }

			}
		) {
			this(it.expandURL(url, type))
		}
	}

	suspend operator fun invoke(novelUI: NovelUI): Unit = this(
		novelUI.novelURL,
		novelUI.extID,
		KEY_NOVEL_URL
	)

	suspend operator fun invoke(chapterUI: ChapterUI): Unit = this(
		chapterUI.link,
		chapterUI.extensionID,
		KEY_CHAPTER_URL
	)
}