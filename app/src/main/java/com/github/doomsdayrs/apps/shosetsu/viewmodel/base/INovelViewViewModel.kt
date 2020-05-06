package com.github.doomsdayrs.apps.shosetsu.viewmodel.base

import androidx.lifecycle.LiveData
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
interface INovelViewViewModel
	: SubscribeHandleViewModel<NovelUI> {
	val chapters: LiveData<HResult<ChapterUI>>
	var isArrayReversed: Boolean

	/**
	 * Instruction to download the next [count] chapters
	 */
	fun downloadNext(count: Int)

	/**
	 * Instruction to download everything
	 */
	fun downloadAll()

	/**
	 * Deletes the previous chapter
	 */
	fun deletePrevious()

	/**
	 * Next chapter to read uwu
	 */
	fun loadLastRead(): LiveData<HResult<ChapterUI>>
}