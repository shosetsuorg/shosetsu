package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem
import app.shosetsu.android.viewmodel.base.ExposedSettingsRepoViewModel
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.android.viewmodel.base.SubscribeViewModel
import app.shosetsu.common.domain.model.local.NovelReaderSettingEntity
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
	SubscribeViewModel<List<ReaderUIItem<*, *>>>,
	ShosetsuViewModel(),
	ExposedSettingsRepoViewModel {

	abstract val ttsSpeed: Float
	abstract val ttsPitch: Float

	/**
	 * User CSS to store for repeat use from reader
	 */
	abstract val userCss: String

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
	abstract var currentChapterID: Int

	/**
	 * Pair of Text color to background color
	 */
	abstract val liveTheme: Flow<Pair<Int, Int>>

	abstract val liveIndentSize: Flow<Int>

	abstract val liveParagraphSpacing: Flow<Float>

	abstract val liveTextSize: Flow<Float>

	abstract val liveVolumeScroll: Flow<Boolean>

	/**
	 * false    -> vertical paging
	 * true     -> horizontal paging
	 */
	abstract val liveChapterDirection: Flow<Boolean>

	/**
	 * The text size that should be used by default for newly created views
	 * This also is the way to easily get current size without async calls
	 */
	abstract val defaultTextSize: Float

	/**
	 * The para space size that should be used by default for newly created views
	 * This also is the way to easily get current size without async calls
	 */
	abstract val defaultParaSpacing: Float

	/**
	 * The indent size that should be used by default for newly created views
	 * This also is the way to easily get current size without async calls
	 */
	abstract val defaultIndentSize: Int

	/**
	 * The text color that should be used by default for newly created views
	 * This also is the way to easily get current color without async calls
	 */
	abstract val defaultForeground: Int

	/**
	 * The background color that should be used by default for newly created views
	 * This also is the way to easily get current color without async calls
	 */
	abstract val defaultBackground: Int

	/**
	 * The horizontal option that should be used by default for newly created views
	 * This also is the way to easily get current state without async calls
	 */
	abstract val isHorizontalReading: Boolean

	/**
	 * The state that should be used by default for newly created views
	 * This also is the way to easily get current state without async calls
	 */
	abstract val defaultVolumeScroll: Boolean

	/** Set the novelID */
	abstract fun setNovelID(novelID: Int)

	/** Start loading up a [readerChapterUI]'s passage */
	abstract fun getChapterPassage(readerChapterUI: ReaderChapterUI): Flow<ByteArray?>

	/** An easy method to toggle the state of a bookmark */
	abstract fun toggleBookmark(readerChapterUI: ReaderChapterUI)

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
}