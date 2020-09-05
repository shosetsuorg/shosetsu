package com.github.doomsdayrs.apps.shosetsu.common.dto

import android.database.sqlite.SQLiteException
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys
import com.github.doomsdayrs.apps.shosetsu.domain.model.base.Convertible
import org.json.JSONException


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
			val data: T,
	) : HResult<T>()

	/** This states that the operation is currently pending */
	object Loading : HResult<Nothing>()

	/** This states that the operation has returned nothing */
	object Empty : HResult<Nothing>()

	/**
	 * This states that the operation has failed
	 * @param code ERROR code
	 * @param message ERROR message
	 * @param error ERROR Cause
	 */
	@Suppress("MemberVisibilityCanBePrivate")
	data class Error(
			val code: Int,
			val message: String,
			val error: Exception? = null,
	) : HResult<Nothing>()
}

/** This is a quick way to toss a success */
inline fun <reified T : Any> successResult(data: T): HResult.Success<T> = HResult.Success(data)

/** This is a quick way to create a loading*/
fun loading(): HResult.Loading = HResult.Loading

/** This is a quick way to create an empty result*/
fun emptyResult(): HResult.Empty = HResult.Empty

/** This is an easy way to create an error*/
fun errorResult(code: Int, message: String, error: Exception? = null): HResult.Error =
		HResult.Error(code, message, error)


/** This is an easy way to create an error via its exception */
fun errorResult(code: Int, error: Exception? = null): HResult.Error =
		HResult.Error(code, error?.message ?: "UnknownException", error)

/** An exception occurred in SQL*/
fun errorResult(e: SQLiteException): HResult.Error =
		HResult.Error(ErrorKeys.ERROR_HTTP_SQL, e.message ?: "UnknownSQLException", e)

/** An exception occurred in SQL*/
fun errorResult(e: JSONException): HResult.Error =
		HResult.Error(ErrorKeys.ERROR_JSON, e.message ?: "UnknownJSONException", e)


/**
 * Converts shit
 */
inline fun <reified O : Any, reified I : Convertible<O>> HResult<List<I>>.mapListTo()
		: HResult<List<O>> {
	return when (this) {
		is HResult.Success -> successResult(this.data.mapTo())
		is HResult.Empty -> this
		is HResult.Loading -> this
		is HResult.Error -> this
	}
}

/**
 * Converts shit
 */
inline fun <reified O : Any, reified I : Convertible<O>> HResult<I>.mapTo()
		: HResult<O> = when (this) {
	is HResult.Success -> {
		this.data.convertTo().let {
			successResult(it)
		}
	}
	is HResult.Empty -> this
	is HResult.Loading -> this
	is HResult.Error -> this
}

inline fun <reified O : Any, reified I : Convertible<O>> List<I>.mapTo(): List<O> =
		this.map { it.convertTo() }


inline fun <reified I : Any, O : Any> HResult<I>.withSuccess(action: (I) -> HResult<O>): HResult<O> {
	return when (this) {
		is HResult.Success -> action(this.data)
		is HResult.Empty -> this
		is HResult.Loading -> this
		is HResult.Error -> this
	}
}

inline infix fun <reified I1 : Any, reified I2 : Any> HResult<I1>.and(
		hResult: HResult<I2>,
): HResult<*> {
	if (this is HResult.Success && hResult is HResult.Success) return successResult("")
	when (this) {
		is HResult.Error -> return this
		is HResult.Empty -> return this
		is HResult.Loading -> return this
	}
	when (hResult) {
		is HResult.Error -> return hResult
		is HResult.Empty -> return hResult
		is HResult.Loading -> return hResult
	}
	return errorResult(ErrorKeys.ERROR_GENERAL, "Unknown case for both results")
}