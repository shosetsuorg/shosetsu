package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.base.ErrorReportingViewModel
import app.shosetsu.android.viewmodel.base.IsOnlineCheckViewModel
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.enums.ReadingStatus
import app.shosetsu.common.view.uimodel.NovelSettingUI
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
abstract class ANovelViewModel
	: ShosetsuViewModel(), IsOnlineCheckViewModel, Destroyable, ErrorReportingViewModel {

	abstract val novelLive: LiveData<HResult<NovelUI>>
	abstract val chaptersLive: LiveData<HResult<List<ChapterUI>>>
	abstract val novelSettingFlow: LiveData<HResult<NovelSettingUI>>

	/** Set's the value to be loaded */
	abstract fun setNovelID(novelID: Int)

	/** Toggles the bookmark of this ui */
	abstract fun toggleNovelBookmark()

	/**
	 * Opens the novel in browser
	 */
	abstract fun openBrowser()

	/**
	 * Opens the novel in web view
	 */
	abstract fun openWebView()

	/**
	 * Opens the share menu
	 */
	abstract fun share()

	/** Instruction to download a specific chapter */
	abstract fun downloadChapter(vararg chapterUI: ChapterUI, startManager: Boolean = false)


	/** Deletes the previous chapter */
	abstract fun deletePrevious()

	/** Next chapter to read uwu */
	abstract fun openLastRead(array: List<ChapterUI>): LiveData<HResult<Int>>

	/**
	 * Marks all the provided chapters as whatever [readingStatus] is
	 */
	abstract fun markAllChaptersAs(vararg chapterUI: ChapterUI, readingStatus: ReadingStatus)

	/**
	 * Opens the chapter in webview
	 */
	abstract fun openWebView(chapterUI: ChapterUI)

	/**
	 * Opens the chapter in the browser
	 */
	abstract fun openBrowser(chapterUI: ChapterUI)

	/**
	 * Deletes a chapter
	 */
	abstract fun delete(vararg chapterUI: ChapterUI)


	/** Refresh media */
	abstract fun refresh(): LiveData<HResult<*>>

	/**
	 * Is the novel bookmarked?
	 */
	abstract fun isBookmarked(): Boolean

	/** Self explanatory */
	abstract fun markChapterAsRead(chapterUI: ChapterUI)

	/** Self explanatory */
	abstract fun markChapterAsUnread(chapterUI: ChapterUI)

	/** Self explanatory */
	abstract fun markChapterAsReading(chapterUI: ChapterUI)

	/** Self explanatory */
	abstract fun toggleChapterBookmark(chapterUI: ChapterUI)

	/** Removes the bookmarks of the chapters provided */
	abstract fun removeChapterBookmarks(vararg chapterUI: ChapterUI)

	/** Bookmarks the chapters provided */
	abstract fun bookmarkChapters(vararg chapterUI: ChapterUI)

	/** Download the next unread chapters */
	abstract fun downloadNextChapter()

	/** Download the next 5 unread chapters */
	abstract fun downloadNext5Chapters()

	/** Download the next 10 unread chapters */
	abstract fun downloadNext10Chapters()

	/** Download the next [max] unread chapters */
	abstract fun downloadNextCustomChapters(max: Int)

	/** Download all unread chapters */
	abstract fun downloadAllUnreadChapters()

	/** Download all chapters */
	abstract fun downloadAllChapters()

	abstract fun updateNovelSetting(novelSettingUI: NovelSettingUI)
}