package com.github.doomsdayrs.apps.shosetsu.viewmodel.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ChapterReaderUI
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
interface IChapterReaderViewModel : SubscribeHandleViewModel<List<ChapterReaderUI>> {

	val currentChapterID: MutableLiveData<Int>
	var novelID: MutableLiveData<Int>
	val backgroundColor: MutableLiveData<Int>
	val textColor: MutableLiveData<Int>

	/** Set the novelID */
	fun setNovelID(novelID: Int)

	fun getChapterPassage(): LiveData<HResult<String>>


	fun appendID(): String
	fun markAsRead()
	fun bookmark()


}