package com.github.doomsdayrs.apps.shosetsu.viewmodel.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ChapterUI
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.NovelUI
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
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
abstract class INovelViewViewModel
	: SubscribeHandleViewModel<NovelUI>, ViewModel() {
	abstract val chapters: LiveData<HResult<ChapterUI>>
	abstract var isArrayReversed: Boolean

	abstract var novelID: MutableLiveData<Int>
	abstract var novelURL: MutableLiveData<String>
	abstract var formatter: MutableLiveData<Formatter>

	abstract fun setNovelURL(novelURL: String)
	abstract fun setNovelID(novelID: Int)

	abstract fun toggleBookmark()

	/** Instruction to download the next [count] chapters */
	abstract fun downloadNext(count: Int)

	/** Instruction to download everything*/
	abstract fun downloadAll()

	/** Deletes the previous chapter */
	abstract fun deletePrevious()

	/** Next chapter to read uwu */
	abstract fun loadLastRead(): LiveData<HResult<ChapterUI>>

	/** Instruction to refresh novel */
	abstract fun refresh()

	abstract val selectedChapters: MutableLiveData<List<Int>>
	abstract fun isChapterSelected(chapterUI: ChapterUI): Boolean
	abstract fun addToSelect(chapterUI: ChapterUI)
	abstract fun updateChapter(chapterUI: ChapterUI)
}