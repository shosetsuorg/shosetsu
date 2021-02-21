package app.shosetsu.android.viewmodel.abstracted

import android.graphics.Color
import androidx.lifecycle.LiveData
import app.shosetsu.android.view.uimodels.model.ColorChoiceUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem
import app.shosetsu.android.viewmodel.base.ErrorReportingViewModel
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.android.viewmodel.base.SubscribeHandleViewModel
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.enums.MarkingTypes
import app.shosetsu.common.enums.ReadingStatus

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
abstract class IChapterReaderViewModel :
	SubscribeHandleViewModel<List<ReaderUIItem<*, *>>>,
	ShosetsuViewModel(),
	ErrorReportingViewModel {

	/**
	 * The current chapter ID that is being read
	 */
	abstract var currentChapterID: Int

	/**
	 * Pair of Text color to background color
	 */
	abstract val liveTheme: LiveData<Pair<Int, Int>>

	abstract val liveThemes: LiveData<List<ColorChoiceUI>>
	abstract val liveMarkingTypes: LiveData<MarkingTypes>
	abstract val liveIndentSize: LiveData<Int>
	abstract val liveParagraphSpacing: LiveData<Float>
	abstract val liveTextSize: LiveData<Float>
	abstract val liveVolumeScroll: LiveData<Boolean>

	/**
	 * false    -> vertical paging
	 * true     -> horizontal paging
	 */
	abstract val liveChapterDirection: LiveData<Boolean>

	var defaultTextSize: Float = SettingKey.ReaderTextSize.default
	var defaultParaSpacing: Float = SettingKey.ReaderParagraphSpacing.default
	var defaultIndentSize: Int = SettingKey.ReaderIndentSize.default
	var defaultForeground: Int = Color.BLACK
	var defaultBackground: Int = Color.WHITE

	var convertStringAsHtml: Boolean = SettingKey.ReaderStringToHtml.default
	var isHorizontalReading: Boolean = SettingKey.ReaderHorizontalPageSwap.default

	var defaultVolumeScroll: Boolean = SettingKey.ReaderVolumeScroll.default

	abstract fun setReaderTheme(value: Int)
	abstract fun setReaderTextSize(value: Float)
	abstract fun setReaderParaSpacing(value: Float)
	abstract fun setReaderIndentSize(value: Int)

	/** Set the novelID */
	abstract fun setNovelID(novelID: Int)

	abstract fun getChapterPassage(readerChapterUI: ReaderChapterUI): LiveData<HResult<String>>
	abstract fun appendID(readerChapterUI: ReaderChapterUI): String
	abstract fun toggleBookmark(readerChapterUI: ReaderChapterUI)

	abstract fun updateChapter(
		readerChapterUI: ReaderChapterUI,
		readingPosition: Int = readerChapterUI.readingPosition,
		readingStatus: ReadingStatus = readerChapterUI.readingStatus,
		bookmarked: Boolean = readerChapterUI.bookmarked,
	)

	abstract fun markAsReadingOnView(readerChapterUI: ReaderChapterUI)
	abstract fun markAsReadingOnScroll(readerChapterUI: ReaderChapterUI, yAswell: Int)

	abstract fun allowVolumeScroll(): Boolean
	abstract fun setOnVolumeScroll(checked: Boolean)

	abstract fun loadChapterCss(): LiveData<String>

	abstract fun updateConvertStringAsHtml(checked: Boolean)
	abstract fun updateHorizontalReading(checked: Boolean)

}