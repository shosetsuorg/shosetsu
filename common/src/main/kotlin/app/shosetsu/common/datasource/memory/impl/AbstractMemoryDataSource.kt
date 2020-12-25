package app.shosetsu.common.datasource.memory.impl

import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.emptyResult
import app.shosetsu.common.dto.successResult
import kotlin.time.TimeSource

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
 * 24 / 12 / 2020
 *
 * Abstracted memory data source
 *
 * This provides limitation features and expiration times, along with more thread safety then normally
 */
abstract class AbstractMemoryDataSource<K, V : Any> {

	/**
	 * How long something can last in memory in MS
	 *
	 * Default is 1 minute
	 */
	open val expireTime: Long = 60000

	/**
	 * how many entries to store at max
	 */
	open val maxSize: Long = 0

	private val _hashMap: HashMap<K, Pair<Long, V>> = hashMapOf()

	fun recycle() {
		val keys = _hashMap.keys.reversed()
		for (i in keys) {
			val (time) = _hashMap[i] ?: continue
			if (time + expireTime <= System.currentTimeMillis()) {
				_hashMap.remove(i)
			}
		}
	}

	fun remove(key: K): HResult<*> =
		if (!contains(key)) emptyResult()
		else {
			_hashMap.remove(key)
			successResult("")
		}

	fun put(key: K, value: V): HResult<*> {
		if (_hashMap.size > maxSize) _hashMap.remove(_hashMap.keys.first())

		_hashMap[key] = System.currentTimeMillis() to value
		return successResult("")
	}

	fun contains(key: K): Boolean {
		val keys = _hashMap.keys.reversed()
		for (i in keys) {
			if (i == key)
				return true
		}
		return false
	}

	fun get(key: K): HResult<V> {
		recycle()
		return if (contains(key)) {
			_hashMap[key]?.let { HResult.Success(it.second) }
				?: emptyResult()
		} else emptyResult()
	}

}