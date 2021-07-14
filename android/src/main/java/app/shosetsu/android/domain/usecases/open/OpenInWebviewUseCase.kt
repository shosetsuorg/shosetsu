package app.shosetsu.android.domain.usecases.open

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.os.bundleOf
import app.shosetsu.android.common.consts.BundleKeys
import app.shosetsu.android.common.ext.intent
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.domain.usecases.get.GetExtensionUseCase
import app.shosetsu.android.domain.usecases.toast.StringToastUseCase
import app.shosetsu.android.ui.webView.WebViewApp
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
 */
class OpenInWebviewUseCase(
	private val getExt: GetExtensionUseCase,
	private val stringToastUseCase: StringToastUseCase,
	private val application: Application,
) {
	operator fun invoke(url: String) {
		Log.d(logID(), "Opening URL $url")
		val i = intent(application, WebViewApp::class.java) {
			bundleOf(
				BundleKeys.BUNDLE_URL to url,
				BundleKeys.BUNDLE_ACTION to WebViewApp.Actions.VIEW.action
			)
		}.apply {
			addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		}
		application.startActivity(i)
	}


	suspend operator fun invoke(url: String, formatterID: Int, type: Int) {
		getExt(formatterID).handle(
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