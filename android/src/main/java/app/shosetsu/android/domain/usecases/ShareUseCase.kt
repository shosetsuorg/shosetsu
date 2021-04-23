package app.shosetsu.android.domain.usecases

import android.app.Application
import android.content.Intent
import android.content.Intent.*
import android.util.Log
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
class ShareUseCase(
	private val stringToastUseCase: StringToastUseCase,
	private val application: Application,
	private val getExt: GetExtensionUseCase,
) {
	operator fun invoke(url: String, title: String) {
		Log.d(logID(), "Opening URL $url")
		val share = createChooser(Intent().apply {
			action = ACTION_SEND
			putExtra(EXTRA_TEXT, url)
			putExtra(EXTRA_TITLE, title)
			type = "text/plain"
		}, null).apply {
			addFlags(FLAG_ACTIVITY_NEW_TASK)
		}
		application.startActivity(share)

	}

	suspend operator fun invoke(url: String, formatterID: Int, title: String, type: Int) {
		getExt(formatterID).handle(
			onEmpty = {
				Log.e(logID(), "Empty")
				stringToastUseCase { "Empty??" }
			},
			onError = {
				Log.e(logID(), "Error")
				stringToastUseCase { "$it" }
			}
		) {
			val formatter = it
			this(formatter.expandURL(url, type), title)
		}
	}

	suspend operator fun invoke(novelUI: NovelUI): Unit = this(
		novelUI.novelURL,
		novelUI.extID,
		novelUI.title,
		KEY_NOVEL_URL
	)

	suspend operator fun invoke(chapterUI: ChapterUI): Unit = this(
		chapterUI.link,
		chapterUI.extensionID,
		chapterUI.title,
		KEY_CHAPTER_URL
	)
}