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
 *
 * Handles upstream data results
 */
sealed class HResult<out T : Any> {
	/** The operation was a success, here is your data [data] */
	class Success<out T : Any>(
			/** Returned data */
			val data: T
	) : HResult<T>()

	/** This states that the operation is currently pending */
	object Loading : HResult<Nothing>()

	/** This states that the operation has returned nothing*/
	object Empty : HResult<Nothing>()

	/**
	 * This states that the operation has failed
	 * @param code ERROR code
	 * @param message ERROR message
	 */
	@Suppress("MemberVisibilityCanBePrivate")
	class Error(val code: Int, val message: String) : HResult<Nothing>()
}

/** This is a quick way to toss a success */
inline fun <reified T : Any> successResult(data: T): HResult.Success<T> = HResult.Success(data)

/** This is a quick way to create a loading*/
fun loading(): HResult.Loading = HResult.Loading

/** This is a quick way to create an empty result*/
fun emptyResult(): HResult.Empty = HResult.Empty

/** This is an easy way to create an error*/
fun errorResult(code: Int, message: String): HResult.Error = HResult.Error(code, message)

/**
 * Converts shit
 */
inline fun <reified O : Any, reified I : Convertible<O>> HResult<List<I>>.mapListTo()
		: HResult<List<O>> {
	return when (this) {
		is HResult.Success -> successResult(this.data.map { it.convertTo() })
		is HResult.Empty -> this
		is HResult.Loading -> this
		is HResult.Error -> this
	}
}

/**
 * Converts shit
 */
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