package com.github.doomsdayrs.apps.shosetsu.common.dto

import com.github.doomsdayrs.apps.shosetsu.domain.model.base.Convertible


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
 * 01 / 05 / 2020
 */

sealed class HResult<out T : Any> {
	class Success<out T : Any>(
			val data: T
	) : HResult<T>()

	object Loading : HResult<Nothing>()
	object Empty : HResult<Nothing>()
	class Error(val code: Int, val message: String) : HResult<Nothing>()
}

inline fun <reified T : Any> successResult(data: T) = HResult.Success(data)
fun loading() = HResult.Loading
fun emptyResult() = HResult.Empty
fun errorResult(code: Int, message: String) = HResult.Error(code, message)

inline fun <reified O : Any, reified I : Convertible<O>> HResult<List<I>>.mapListTo()
		: HResult<List<O>> {
	return when (this) {
		is HResult.Success -> successResult(this.data.map { it.convertTo() })
		is HResult.Empty -> this
		is HResult.Loading -> this
		is HResult.Error -> this
	}
}

inline fun <reified O : Any, reified I : Convertible<O>> HResult<I>.mapTo()
		: HResult<O> {
	return when (this) {
		is HResult.Success -> {
			this.data.convertTo()?.let {
				successResult(it)
			} ?: emptyResult()
		}
		is HResult.Empty -> this
		is HResult.Loading -> this
		is HResult.Error -> this
	}
}