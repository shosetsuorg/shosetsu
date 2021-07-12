package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.viewmodel.base.ErrorReportingViewModel
import app.shosetsu.android.viewmodel.base.ExposedSettingsRepoViewModel
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.android.viewmodel.base.SubscribeHandleViewModel
import app.shosetsu.common.dto.HResult

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
	SubscribeHandleViewModel<List<ReaderUIItem<*, *>>>,
	ShosetsuViewModel(),
	ErrorReportingViewModel,
	ExposedSettingsRepoViewModel {

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
	abstract val liveIsScreenRotationLocked: LiveData<Boolean>

	/**
	 * Should the reader keep the screen on
	 */
	abstract val liveKeepScreenOn: LiveData<Boolean>

	/**
	 * The current chapter ID that is being read
	 */
	abstract var currentChapterID: Int

	/**
	 * Pair of Text color to background color
	 */
	abstract val liveTheme: LiveData<Pair<Int, Int>>

	abstract val liveIndentSize: LiveData<Int>

	abstract val liveParagraphSpacing: LiveData<Float>

	abstract val liveTextSize: LiveData<Float>

	abstract val liveVolumeScroll: LiveData<Boolean>

	/**
	 * false    -> vertical paging
	 * true     -> horizontal paging
	 */
	abstract val liveChapterDirection: LiveData<Boolean>

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
	abstract fun getChapterPassage(readerChapterUI: ReaderChapterUI): LiveData<HResult<ByteArray>>

	/** An easy method to toggle the state of a bookmark */
	abstract fun toggleBookmark(readerChapterUI: ReaderChapterUI)

	/** Update a [readerChapterUI] */
	abstract fun updateChapter(
		readerChapterUI: ReaderChapterUI,
	)

	/** Called when a [readerChapterUI] is viewed by the user */
	abstract fun markAsReadingOnView(readerChapterUI: ReaderChapterUI)

	/**
	 * Called when a [readerChapterUI] is scrolled,
	 * will also update the [readingPosition] for ease
	 */
	abstract fun markAsReadingOnScroll(readerChapterUI: ReaderChapterUI, readingPosition: Double)

	/**
	 * Loads a [LiveData] reflection of the global custom css
	 */
	abstract fun loadChapterCss(): LiveData<String>

	/**
	 * Loads the settings list for the bottom bar
	 */
	abstract fun getSettings(): LiveData<HResult<List<SettingsItemData>>>


	/**
	 * Toggle the screen lock state
	 */
	abstract fun toggleScreenRotationLock()

}