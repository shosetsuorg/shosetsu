package app.shosetsu.android

import app.shosetsu.android.common.consts.MEMORY_EXPIRE_CHAPTER_TIME
import app.shosetsu.android.common.consts.MEMORY_MAX_CHAPTERS
import app.shosetsu.android.datasource.local.memory.impl.GenericMemChaptersDataSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Test
import kotlin.system.measureTimeMillis

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
 * 25 / 01 / 2021
 *
 * Tests the [app.shosetsu.android.datasource.local.memory.base.IMemChaptersDataSource]
 */
class MemoryChapterDataSourceTest {
	private val memorySource by lazy { GenericMemChaptersDataSource() }
	private val expireTime by lazy { memorySource.expireTime }

	/**
	 * Double check that the expire time is correct and it is a memory source
	 */
	@Before
	fun before() {
		println("=================================")
		// How long until data expires
		println("Expires in $expireTime ms")
		require((expireTime / (60 * 1000)) == MEMORY_EXPIRE_CHAPTER_TIME) { "Expire time does not match up properly" }
		println("Expire time matches")

		println("Checking if max size is right")
		require(memorySource.maxSize == MEMORY_MAX_CHAPTERS) { "Chapter max is not the same" }
		println("Max size is right")
	}

	@Test
	fun main() {
		run {
			println("=================================")
			println("Testing Max Size")
			val job = GlobalScope.launch {
				println("Saving enough chapters to go over max limit")


				println("Saving #$CHAPTER_ID, to ensure it is deleted")
				// Saving this chapter
				memorySource.saveChapterInCache(CHAPTER_ID, CHAPTER_CONTENT.toByteArray())

				println("Saving chapters")
				for (i in 1 until MEMORY_MAX_CHAPTERS + 2)
					memorySource.saveChapterInCache(i.toInt(), "$i".toByteArray())

				println("Checking if chapter is present")
				require(memorySource.loadChapterFromCache(CHAPTER_ID) != null)
				{ "Chapter still present" }
				println("Chapter is not present, Success")
			}
			measureTimeMillis {
				var i = 0
				while (job.isActive) i++
			}.let {
				println("Completed in $it ms")
			}
		}
		println("=================================")
		run {
			println("Testing expire time")
			val job = GlobalScope.launch {
				println("Save and load test")
				memorySource.saveChapterInCache(CHAPTER_ID, CHAPTER_CONTENT.toByteArray())
				memorySource.loadChapterFromCache(CHAPTER_ID).let {
					println("We have $CHAPTER_CONTENT, ensuring they are the same")
					assert(
						CHAPTER_CONTENT.toByteArray().contentEquals(it)
					) { "They are not the same" }
					println("They match up")
				}
				println("Delaying until time is sufficient for it to be deleted")

				delay(expireTime + 1000)
				assert(memorySource.loadChapterFromCache(CHAPTER_ID) == null) {
					"Did not delete"
				}
				println("Test completed properly")
			}
			measureTimeMillis {
				var i = 0
				while (job.isActive) i++
			}.let {
				println("Completed in $it ms")
			}
		}
		println("=================================")
	}

	companion object {
		private const val CHAPTER_ID = 0
		private const val CHAPTER_CONTENT = "ABC THIS IS CONTENT\nCONTENT"
	}
}