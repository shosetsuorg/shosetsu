package app.shosetsu.android

import app.shosetsu.android.datasource.local.memory.impl.AbstractMemoryDataSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.future
import org.junit.Test
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

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
 * 13 / 02 / 2021
 */
class AbstractMemoryDataSourceTest {

	private val source: AbstractMemoryDataSource<Int, String> by lazy {
		object : AbstractMemoryDataSource<Int, String>() {
			override val maxSize: Long
				get() = 100
			override val expireTime: Long
				get() = 5000
		}
	}

	@ExperimentalTime
	@Test
	fun main() {
		GlobalScope.future {
			// fill data
			println("Time to input 100 chapters: " + measureTime {
				for (i in 0 until 100)
					source.put(i, i.toString())
			})
			delay(6000)
			println("Putting 1 in to override old time")
			source.put(111, "a")

			println("Time to get with a full recycle: " + measureTime {
				println("Value: " + source.get(111))
			})

			println("Time to to get when clean: " + measureTime {
				println("Value: " + source.get(111))
			})
		}.join()
	}
}