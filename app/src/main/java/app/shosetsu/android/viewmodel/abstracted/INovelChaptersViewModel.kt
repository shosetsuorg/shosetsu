package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.viewmodel.base.SubscribeHandleViewModel

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
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
abstract class INovelChaptersViewModel
	: SubscribeHandleViewModel<List<ChapterUI>>, ViewModel() {

	abstract fun setNovelID(novelID: Int)

	/** Instruction to download a specific chapter */
	abstract fun download(vararg chapterUI: ChapterUI)

	/** Deletes the previous chapter */
	abstract fun deletePrevious()

	/** Next chapter to read uwu */
	abstract fun openLastRead(array: List<ChapterUI>): LiveData<HResult<Int>>

	abstract fun updateChapter(
			chapterUI: ChapterUI,
			readingPosition: Int = chapterUI.readingPosition,
			readingStatus: ReadingStatus = chapterUI.readingStatus,
			bookmarked: Boolean = chapterUI.bookmarked,
	)

	abstract fun markAllAs(vararg chapterUI: ChapterUI, readingStatus: ReadingStatus)

	/**
	 * Opens the chapter in webview
	 */
	abstract fun openWebView(chapterUI: ChapterUI)

	abstract fun openBrowser(chapterUI: ChapterUI)


	/**
	 * Deletes a chapter
	 */
	abstract fun delete(vararg chapterUI: ChapterUI)


}