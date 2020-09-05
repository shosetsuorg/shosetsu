package com.github.doomsdayrs.apps.shosetsu.viewmodel.model

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.LoadChapterPassageUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.LoadReaderChaptersUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.update.UpdateReaderChapterUseCase
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.ReaderChapterUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.IChapterReaderViewModel

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
		private val context: Context,
		private val loadReaderChaptersUseCase: LoadReaderChaptersUseCase,
		private val loadChapterPassageUseCase: LoadChapterPassageUseCase,
		private val updateReaderChapterUseCase: UpdateReaderChapterUseCase,
		private val settings: ShosetsuSettings,
) : IChapterReaderViewModel() {
	private val hashMap: HashMap<Int, MutableLiveData<*>> = hashMapOf()

	override val liveData: LiveData<HResult<List<ReaderChapterUI>>> by lazy {
		loadReaderChaptersUseCase(nID)
	}

	override var currentChapterID: Int = -1
	private var nID = -1

	override fun setNovelID(novelID: Int) {
		if (nID == -1)
			nID = novelID
	}

	@WorkerThread
	override fun getChapterPassage(readerChapterUI: ReaderChapterUI): LiveData<HResult<String>> {
		if (hashMap.containsKey(readerChapterUI.id)) {
			Log.d(logID(), "Loading existing live data for ${readerChapterUI.id}")
			return hashMap[readerChapterUI.id] as LiveData<HResult<String>>
		}

		Log.d(logID(), "Creating a new live data for ${readerChapterUI.id}")
		val data = MutableLiveData<HResult<String>>()
		hashMap[readerChapterUI.id] = data
		launchIO {
			data.postValue(loading())
			Log.d(logID(), "Loading ${readerChapterUI.link}")
			val v = loadChapterPassageUseCase(readerChapterUI)
			Log.d(logID(), "I got a ${v.javaClass.simpleName}")
			data.postValue(v)
		}
		return data
	}

	override fun appendID(readerChapterUI: ReaderChapterUI): String =
			"${readerChapterUI.id}|${readerChapterUI.link}"

	override fun toggleBookmark(readerChapterUI: ReaderChapterUI) {
		updateChapter(readerChapterUI, bookmarked = !readerChapterUI.bookmarked)
	}

	override fun updateChapter(
			readerChapterUI: ReaderChapterUI,
			readingPosition: Int,
			readingStatus: ReadingStatus,
			bookmarked: Boolean,
	) {
		launchIO {
			updateReaderChapterUseCase(readerChapterUI.copy(
					readingPosition = readingPosition,
					readingStatus = readingStatus,
					bookmarked = bookmarked
			))
		}
	}
}