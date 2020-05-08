package com.github.doomsdayrs.apps.shosetsu.viewmodel

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.default
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ChapterReaderUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IChapterReaderViewModel

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
class ChapterReaderViewModel(
		val context: Context
) : ViewModel(), IChapterReaderViewModel {
	override val liveData: LiveData<HResult<List<ChapterReaderUI>>> by lazy {
		TODO("Not yet implemented")
	}

	override val currentChapterID: Int = -1

	override var novelID: MutableLiveData<Int> = MutableLiveData<Int>().default(-1)

	override val backgroundColor: MutableLiveData<Int> = MutableLiveData<Int>().default(
			when (Settings.readerTheme) {
				Settings.ReaderThemes.NIGHT.i, Settings.ReaderThemes.DARK.i -> Color.BLACK
				Settings.ReaderThemes.LIGHT.i -> Color.WHITE
				Settings.ReaderThemes.SEPIA.i -> ContextCompat.getColor(context, R.color.wheat)
				Settings.ReaderThemes.DARKI.i -> Color.DKGRAY
				Settings.ReaderThemes.CUSTOM.i -> Settings.readerCustomTextColor
				else -> Color.BLACK
			}
	)

	override val textColor: MutableLiveData<Int> = MutableLiveData<Int>().default(
			when (Settings.readerTheme) {
				Settings.ReaderThemes.NIGHT.i -> Color.WHITE
				Settings.ReaderThemes.LIGHT.i, Settings.ReaderThemes.SEPIA.i -> Color.BLACK
				Settings.ReaderThemes.DARK.i -> Color.GRAY
				Settings.ReaderThemes.DARKI.i -> Color.LTGRAY
				Settings.ReaderThemes.CUSTOM.i -> Settings.readerCustomBackColor
				else -> Color.WHITE
			}
	)

	override fun setNovelID(novelID: Int) {
		this.novelID = MutableLiveData(novelID)
	}

	override fun getChapterPassage(): LiveData<HResult<String>> {
		TODO("Not yet implemented")
	}

	override fun appendID(chapterReaderUI: ChapterReaderUI): String {
		TODO("Not yet implemented")
	}


	override fun bookmark() {
		TODO("Not yet implemented")
	}

	override fun updateChapter(chapterReaderUI: ChapterReaderUI) {
		TODO("Not yet implemented")
	}

}