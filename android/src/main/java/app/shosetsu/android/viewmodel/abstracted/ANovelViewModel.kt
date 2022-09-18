package app.shosetsu.android.viewmodel.abstracted

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.view.uimodels.NovelSettingUI
import app.shosetsu.android.view.uimodels.model.CategoryUI
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.base.IsOnlineCheckViewModel
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import kotlinx.coroutines.flow.Flow
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
	: ShosetsuViewModel(), IsOnlineCheckViewModel, Destroyable {

	abstract val hasSelected: Flow<Boolean>
	abstract fun clearSelection()

	abstract val itemIndex: Flow<Int>
	abstract fun setItemAt(index: Int)

	abstract val isRefreshing: Flow<Boolean>

	abstract val novelLive: Flow<NovelUI?>
	abstract val chaptersLive: Flow<List<ChapterUI>>
	abstract val selectedChaptersState: Flow<SelectedChaptersState>

	abstract val otherException: Flow<Throwable?>
	abstract val novelException: Flow<Throwable?>
	abstract val chaptersException: Flow<Throwable?>

	abstract val novelSettingFlow: Flow<NovelSettingUI?>

	abstract val categories: Flow<List<CategoryUI>>
	abstract val novelCategories: Flow<List<Int>>

	/** Set's the value to be loaded */
	abstract fun setNovelID(novelID: Int)

	/**
	 * Set the categories of the novel
	 */
	abstract fun setNovelCategories(categories: IntArray): Flow<Unit>

	/**
	 * Toggles the bookmark of this ui
	 * @return ToggleBookmarkResponse of what the UI should react with
	 */
	abstract fun toggleNovelBookmark(): Flow<ToggleBookmarkResponse>

	/**
	 * Response to toggling the novel bookmark
	 */
	sealed class ToggleBookmarkResponse {
		/**
		 * UI can ignore response
		 */
		object Nothing : ToggleBookmarkResponse()

		/**
		 * The user should be informed that chapters can be deleted
		 * @param chapters how many chapters to delete
		 */
		data class DeleteChapters(val chapters: Int) : ToggleBookmarkResponse()
		// TODO Possibly warn if a matching novel is in the library or not
	}

	/**
	 * Return the novelURL to utilize in some way
	 */
	abstract fun getNovelURL(): Flow<String?>

	data class NovelShareInfo(
		val novelTitle: String,
		val novelURL: String
	)

	abstract fun getShareInfo(): Flow<NovelShareInfo?>

	/**
	 * Return the chapterURL to utilize in some way
	 */
	abstract fun getChapterURL(chapterUI: ChapterUI): Flow<String?>

	/** Deletes the previous chapter */
	abstract fun deletePrevious(): Flow<Boolean>

	/**
	 * Will return the next chapter to read & scroll to said chapter
	 *
	 * @return Next chapter to read uwu
	 */
	abstract fun openLastRead(): Flow<ChapterUI?>

	/** Refresh media */
	abstract fun refresh(): Flow<Unit>

	/**
	 * Is the novel bookmarked?
	 */
	abstract fun isBookmarked(): Flow<Boolean>

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

	/**
	 * Remember that the next time the novel controller is rendered,
	 * that it is from the chapter reader
	 *
	 * If returns true, will be false on next get
	 */
	abstract var isFromChapterReader: Boolean


	abstract fun getIfAllowTrueDelete(): Flow<Boolean>

	abstract fun getQRCode(): Flow<ImageBitmap?>

	abstract fun bookmarkSelected()
	abstract fun removeBookmarkFromSelected()

	abstract fun selectAll()

	abstract fun invertSelection()
	abstract fun downloadSelected()
	abstract fun deleteSelected()
	abstract fun markSelectedAs(readingStatus: ReadingStatus)
	abstract fun selectBetween()

	abstract fun trueDeleteSelected()

	/**
	 * Try to scroll to a chapter via predicate
	 *
	 * @return false if the chapter could not be found
	 */
	abstract fun scrollTo(predicate: (ChapterUI) -> Boolean): Flow<Boolean>

	abstract fun toggleSelection(it: ChapterUI)
	abstract fun getChapterCount(): Flow<Int>

	/**
	 * Delete downloaded chapters
	 */
	abstract fun deleteChapters()

	/**
	 * @param showRemoveBookmark If any chapters are bookmarked, show the remove bookmark logo
	 * @param showBookmark If any chapters are not bookmarked, show bookmark
	 * @param showDelete  If any are downloaded, show delete
	 * @param showDownload  If any are not downloaded, show download option
	 * @param showMarkAsRead If any are unread, show read option
	 * @param showMarkAsUnread If any are read, show unread option
	 */
	@Immutable
	data class SelectedChaptersState(
		val showRemoveBookmark: Boolean = false,
		val showBookmark: Boolean = false,
		val showDelete: Boolean = false,
		val showDownload: Boolean = false,
		val showMarkAsRead: Boolean = false,
		val showMarkAsUnread: Boolean = false
	)
}