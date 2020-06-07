package com.github.doomsdayrs.apps.shosetsu.viewmodel.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ReaderChapterUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.base.SubscribeHandleViewModel

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
abstract class IChapterReaderViewModel : SubscribeHandleViewModel<List<ReaderChapterUI>>, ViewModel() {

	abstract val backgroundColor: MutableLiveData<Int>
	abstract val textColor: MutableLiveData<Int>
	abstract val currentChapterID: Int

	/** Set the novelID */
	abstract fun setNovelID(novelID: Int)

	abstract fun getChapterPassage(): LiveData<HResult<String>>
	abstract fun appendID(readerChapterUI: ReaderChapterUI): String
	abstract fun bookmark()
	abstract fun updateChapter(
			readerChapterUI: ReaderChapterUI,
			readingPosition: Int = readerChapterUI.readingPosition,
			readingStatus: ReadingStatus = readerChapterUI.readingStatus,
			bookmarked: Boolean = readerChapterUI.bookmarked
	)
}