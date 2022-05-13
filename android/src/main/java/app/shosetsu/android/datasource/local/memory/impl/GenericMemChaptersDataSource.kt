package app.shosetsu.android.datasource.local.memory.impl

import app.shosetsu.android.common.consts.MEMORY_EXPIRE_CHAPTER_TIME
import app.shosetsu.android.common.consts.MEMORY_MAX_CHAPTERS
import app.shosetsu.android.datasource.local.memory.base.IMemChaptersDataSource

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
class GenericMemChaptersDataSource : IMemChaptersDataSource,
	AbstractMemoryDataSource<Int, ByteArray>() {

	override val expireTime = MEMORY_EXPIRE_CHAPTER_TIME * 1000 * 60
	override val maxSize = MEMORY_MAX_CHAPTERS

	override fun saveChapterInCache(chapterID: Int, chapter: ByteArray) =
		put(chapterID, chapter)

	override fun loadChapterFromCache(chapterID: Int): ByteArray? =
		get(chapterID)

}