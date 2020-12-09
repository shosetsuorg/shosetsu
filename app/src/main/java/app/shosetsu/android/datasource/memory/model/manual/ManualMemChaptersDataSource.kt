package app.shosetsu.android.datasource.memory.model.manual

import app.shosetsu.android.datasource.memory.base.IMemChaptersDataSource
import app.shosetsu.common.com.consts.MEMORY_EXPIRE_CHAPTER_TIME
import app.shosetsu.common.com.consts.MEMORY_MAX_CHAPTERS
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.com.dto.emptyResult
import app.shosetsu.common.com.dto.successResult

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * shosetsu
 * 19 / 11 / 2020
 */
class ManualMemChaptersDataSource : IMemChaptersDataSource {
	private val chapters = HashMap<Int, Pair<Long, String>>()
		get() {
			recycle(field)
			return field
		}

	private fun recycle(hashMap: HashMap<Int, Pair<Long, String>>) {
		val keys = hashMap.keys
		for (i in keys) {
			val (time) = hashMap[i] ?: continue
			if (time + ((MEMORY_EXPIRE_CHAPTER_TIME * 1000) * 60) <= System.currentTimeMillis())
				hashMap.remove(i)
		}
	}

	override suspend fun saveChapterInCache(chapterID: Int, passage: String): HResult<*> {
		val chapters = chapters
		if (chapters.size > MEMORY_MAX_CHAPTERS) chapters.remove(chapters.keys.first())
		chapters[chapterID] = System.currentTimeMillis() to passage
		return successResult("")
	}

	override suspend fun loadChapterFromCache(chapterID: Int): HResult<String> {
		val chapters = chapters
		return if (chapters.containsKey(chapterID))
			chapters[chapterID]?.let { successResult(it.second) }
					?: emptyResult() else emptyResult()
	}

}