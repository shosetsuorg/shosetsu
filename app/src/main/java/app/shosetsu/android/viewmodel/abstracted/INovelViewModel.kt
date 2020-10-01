package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.base.IsOnlineCheckViewModel
import com.mikepenz.fastadapter.items.AbstractItem
import javax.security.auth.Destroyable

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
abstract class INovelViewModel
	: ViewModel(), IsOnlineCheckViewModel, Destroyable {
	abstract val uiLive: LiveData<HResult<List<AbstractItem<*>>>>

	abstract val novelLive: LiveData<HResult<NovelUI>>
	abstract val chaptersLive: LiveData<HResult<List<ChapterUI>>>

	/** Name of the formatter */
	abstract val formatterName: LiveData<HResult<String>>

	/** Set's the value to be loaded */
	abstract fun setNovelID(novelID: Int)

	/** Toggles the bookmark of this ui */
	abstract fun toggleBookmark()
	abstract fun openBrowser()
	abstract fun openWebView()
	abstract fun share()

	/** Instruction to download a specific chapter */
	abstract fun downloadChapter(vararg chapterUI: ChapterUI)


	/** Deletes the previous chapter */
	abstract fun deletePrevious()

	/** Next chapter to read uwu */
	abstract fun openLastRead(array: List<ChapterUI>): LiveData<HResult<Int>>

	abstract fun markAllChaptersAs(vararg chapterUI: ChapterUI, readingStatus: ReadingStatus)

	/**
	 * Opens the chapter in webview
	 */
	abstract fun openWebView(chapterUI: ChapterUI)

	abstract fun openBrowser(chapterUI: ChapterUI)

	abstract fun reverseChapters()

	/**
	 * Deletes a chapter
	 */
	abstract fun delete(vararg chapterUI: ChapterUI)


	/** Refresh media */
	abstract fun refresh(): LiveData<HResult<*>>
	abstract fun isBookmarked(): Boolean

	abstract fun markChapterAsRead(chapterUI: ChapterUI)
	abstract fun markChapterAsUnread(chapterUI: ChapterUI)
	abstract fun markChapterAsReading(chapterUI: ChapterUI)
	abstract fun toggleChapterBookmark(chapterUI: ChapterUI)

	abstract fun downloadNextChapter()
	abstract fun downloadNext5Chapters()
	abstract fun downloadNext10Chapters()
	abstract fun downloadNextCustomChapters(max: Int)

	abstract fun downloadAllUnreadChapters()
	abstract fun downloadAllChapters()
}