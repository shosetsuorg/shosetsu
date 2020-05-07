package com.github.doomsdayrs.apps.shosetsu.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.common.utils.DownloadManager
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.common.ext.handle
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.DownloadEntity
import com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters.ChaptersAdapter
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ChapterUI
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.NovelUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.INovelViewViewModel
import java.util.*
import kotlin.collections.ArrayList

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
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class NovelViewViewModel : ViewModel(), INovelViewViewModel {
	override fun downloadNext(count: Int) {
		TODO("Not yet implemented")
	}

	override fun downloadAll() {
		TODO("Not yet implemented")
	}

	override fun deletePrevious() {
		TODO("Not yet implemented")
	}

	override val liveData: LiveData<HResult<NovelUI>>
		get() = TODO("Not yet implemented")

	override val chapters: LiveData<HResult<ChapterUI>>
		get() = TODO("Not yet implemented")

	/**
	 * Sets the novel chapters down
	 */
	fun setChapters() {
		recyclerView?.post {
			recyclerView?.setHasFixedSize(false)
			if (!DatabaseNovels.isNotInNovels(novelID)) {
				recyclerArray = getChapters(novelID) as ArrayList<Novel.Chapter>
				if (recyclerArray.isNotEmpty())
					resume?.visibility = View.VISIBLE
			}
			recyclerView?.adapter = ChaptersAdapter(this)
		}
	}

	fun updateAdapter(): Boolean {
		return recyclerView!!.post { if (adapter != null) adapter!!.notifyDataSetChanged() }
	}

	private fun customAdd(count: Int) {
		val ten = getCustom(count) { true }
		if (!ten.isNullOrEmpty())
			for ((_, title, link) in ten)
				DownloadManager.addToDownload(activity!!, DownloadEntity(
						getChapterIDFromChapterURL(link),
						novelController!!.novelInfoController!!.novelPage!!.title,
						title,
						status = "Pending"
				))
	}

	operator fun contains(novelChapter: Novel.Chapter): Boolean {
		try {
			for (n in selectedChapters)
				if (getChapter(n)!!.link.equals(novelChapter.link, ignoreCase = true))
					return true
		} catch (e: MissingResourceException) {
			e.handle(logID(), true)
		}
		return false
	}

	private fun findMinPosition(): Int {
		var min: Int = recyclerArray.size
		for (x in recyclerArray.indices) if (contains(recyclerArray[x]))
			if (x < min) min = x
		return min
	}

	private fun findMaxPosition(): Int {
		var max = -1
		for (x in recyclerArray.indices.reversed()) if (contains(recyclerArray[x]))
			if (x > max) max = x
		return max
	}

	private fun handleExceptionLogging(e: Exception) = e.handle(logID())

	@Suppress("unused")
			/**
			 * @param chapterURL Current chapter URL
			 * @return chapter after the input, returns the current chapter if no more
			 */
	fun getNextChapter(chapterURL: Int, novelChapters: IntArray?): Novel.Chapter? {
		if (novelChapters != null && novelChapters.isNotEmpty())
			for (x in novelChapters.indices) {
				if (novelChapters[x] == chapterURL) {
					return if (isArrayReversed!!) {
						if (x - 1 != -1) getChapter(novelChapters[x - 1])
						else
							getChapter(novelChapters[x])
					} else {
						if (x + 1 != novelChapters.size)
							getChapter(novelChapters[x + 1]) else
							getChapter(novelChapters[x])
					}
				}
			}
		return null
	}

	fun getNextChapter(chapterURL: String, novelChapters: List<Novel.Chapter>): Novel.Chapter? {
		if (novelChapters.isNotEmpty())
			for (x in novelChapters.indices) {
				if (novelChapters[x].link == chapterURL) {
					return if (isArrayReversed) {
						if (x - 1 != -1)
							getChapter(getChapterIDFromChapterURL(novelChapters[x - 1].link))
						else
							getChapter(getChapterIDFromChapterURL(novelChapters[x].link))
					} else {
						if (x + 1 != novelChapters.size)
							getChapter(getChapterIDFromChapterURL(novelChapters[x + 1].link))
						else
							getChapter(getChapterIDFromChapterURL(novelChapters[x].link))
					}
				}
			}
		return null
	}

	fun getCustom(count: Int, check: (ReadingStatus) -> Boolean): List<Novel.Chapter> {
		Log.d("NovelFragment", "CustomGet of chapters: Count:$count")
		val customChapters: ArrayList<Novel.Chapter> = ArrayList()
		val lastReadChapter = getLastRead()
		var found = false
		if (!recyclerArray.isNullOrEmpty()) if (!isArrayReversed) {
			for (x in recyclerArray.size - 1 downTo 0) {
				if (lastReadChapter.link == recyclerArray[x].url)
					found = true
				if (found) {
					var y = x
					while (y < recyclerArray.size) {
						if (customChapters.size <= count) {
							if (check(getChapterStatus(getChapterIDFromChapterURL(recyclerArray[y].url))))
								customChapters.add(recyclerArray[y])
						}
						Log.d("NovelFragment", "Size ${customChapters.size}")
						y++
					}
				}

			}
		} else {
			for (x in recyclerArray.indices) {
				if (lastReadChapter.link == recyclerArray[x].url)
					found = true
				if (found) {
					var y = x
					while (y > 0) {
						if (customChapters.size <= count) {
							if (check(getChapterStatus(getChapterIDFromChapterURL(recyclerArray[y].url))))
								customChapters.add(recyclerArray[y])
						}
						y--
					}
				}

			}
		}

		return customChapters
	}

	fun getLastRead(): Novel.Chapter {
		if (!recyclerArray.isNullOrEmpty())
			if (!isArrayReversed)
				for (x in recyclerArray.size - 1 downTo 0) {
					val stat = getChapterStatus(getChapterIDFromChapterURL(recyclerArray[x].url))
					if (stat == ReadingStatus.READ || stat == ReadingStatus.READING)
						return recyclerArray[x]
				}
			else for (x in recyclerArray) {
				val stat = getChapterStatus(getChapterIDFromChapterURL(x.url))
				if (stat == ReadingStatus.READ || stat == ReadingStatus.READING)
					return x
			}
		return if (isArrayReversed) recyclerArray[0] else recyclerArray[recyclerArray.size - 1]
	}

	/**
	 * @return position of last read chapter, reads array from reverse.
	 * If -1 then the array is null, if -2 the array is empty, else if not found plausible chapter returns the first.
	 */
	fun lastRead(): Int {
		return if (recyclerArray.isNotEmpty()) {
			if (!isArrayReversed!!) {
				for (x in recyclerArray.indices.reversed()) {
					when (getChapterStatus(getChapterIDFromChapterURL(recyclerArray[x].url))) {
						ReadingStatus.READ -> return x + 1
						ReadingStatus.READING -> return x
						else -> {
						}
					}
				}
			} else {
				for (x in recyclerArray.indices) {
					when (getChapterStatus(getChapterIDFromChapterURL(recyclerArray[x].url))) {
						ReadingStatus.READ -> return x - 1
						ReadingStatus.READING -> return x
						else -> {
						}
					}
				}
			}
			0
		} else -2
	}
}