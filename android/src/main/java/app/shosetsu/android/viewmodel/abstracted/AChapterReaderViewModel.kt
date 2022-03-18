package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem.ReaderChapterUI
import app.shosetsu.android.viewmodel.base.ExposedSettingsRepoViewModel
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.android.viewmodel.base.SubscribeViewModel
import app.shosetsu.common.domain.model.local.NovelReaderSettingEntity
import app.shosetsu.lib.Novel
import kotlinx.coroutines.flow.Flow

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
 * 06 / 05 / 2020
 */
abstract class AChapterReaderViewModel :
	SubscribeViewModel<List<ReaderUIItem>>,
	ShosetsuViewModel(),
	ExposedSettingsRepoViewModel {

	abstract fun getChapterStringPassage(item: ReaderChapterUI): Flow<String>

	abstract fun getChapterHTMLPassage(item: ReaderChapterUI): Flow<String>

	abstract fun setCurrentPage(page: Int)

	abstract val currentPage: Flow<Int>

	abstract val isCurrentChapterBookmarked: Flow<Boolean>

	/**
	 * Is loading up initial content
	 */
	abstract val isMainLoading: Flow<Boolean>
	abstract val chapterType: Flow<Novel.ChapterType?>

	abstract val currentTitle: Flow<String?>

	abstract val ttsSpeed: Float
	abstract val ttsPitch: Float

	/**
	 * Is tap to scroll enabled
	 */
	abstract val tapToScroll: Boolean

	/**
	 * Should the screen be locked
	 */
	abstract val liveIsScreenRotationLocked: Flow<Boolean>

	/**
	 * Should the reader keep the screen on
	 */
	abstract val liveKeepScreenOn: Flow<Boolean>

	/**
	 * The current chapter ID that is being read
	 */
	abstract val currentChapterID: Flow<Int>

	abstract val textColor: Flow<Int>
	abstract val backgroundColor: Flow<Int>

	abstract val liveTextSize: Flow<Float>

	abstract val liveVolumeScroll: Flow<Boolean>

	/**
	 * false    -> vertical paging
	 * true     -> horizontal paging
	 */
	abstract val isHorizontalReading: Flow<Boolean>

	/**
	 * The state that should be used by default for newly created views
	 * This also is the way to easily get current state without async calls
	 */
	abstract val isVolumeScrollEnabled: Boolean

	/** Set the novelID */
	abstract fun setNovelID(novelID: Int)

	/**
	 * Toggle the bookmark of the current chapter
	 */
	abstract fun toggleBookmark()

	/** Update a [chapter] */
	abstract fun updateChapter(chapter: ReaderChapterUI)

	/** Update [chapter] as Read, this will also clear all reading progress */
	abstract fun updateChapterAsRead(chapter: ReaderChapterUI)

	/** Called when a [chapter] is viewed by the user */
	abstract fun markAsReadingOnView(chapter: ReaderChapterUI)

	/**
	 * Called when a [chapter] is scrolled,
	 * will also update the [readingPosition] for ease
	 */
	abstract fun markAsReadingOnScroll(chapter: ReaderChapterUI, readingPosition: Double)

	/**
	 * Loads a [LiveData] reflection of the global custom css
	 */
	abstract fun loadChapterCss(): Flow<String>

	/**
	 * Loads the settings list for the bottom bar
	 */
	abstract fun getSettings(): Flow<NovelReaderSettingEntity>

	abstract fun updateSetting(novelReaderSettingEntity: NovelReaderSettingEntity)

	/**
	 * Toggle the screen lock state
	 */
	abstract fun toggleScreenRotationLock()

	abstract fun setCurrentChapterID(chapterId: Int, initial: Boolean = false)
	abstract fun incrementProgress()
	abstract fun depleteProgress()
	abstract fun getCurrentChapterURL(): Flow<String>
}