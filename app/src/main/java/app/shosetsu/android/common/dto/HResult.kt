package app.shosetsu.android.common.dto

import app.shosetsu.android.common.consts.ErrorKeys


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

interface Convertible<T> {
	/** States this [Convertible] can turn into [T]*/
	fun convertTo(): T
}

/**
 * Converts shit
 */
inline fun <reified O : Any, reified I : Convertible<O>> HResult<List<I>>.mapListTo()
		: HResult<List<O>> = this.handleReturn { successResult(it.mapTo()) }

/**
 * Converts shit
 */
inline fun <reified O : Any, reified I : Convertible<O>> HResult<I>.mapTo()
		: HResult<O> = this.handleReturn { successResult(it.convertTo()) }

inline fun <reified O : Any, reified I : Convertible<O>> List<I>.mapTo(): List<O> =
		this.map { it.convertTo() }


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


fun errorResult(e: NullPointerException): HResult.Error =
		HResult.Error(ErrorKeys.ERROR_NPE, e.message ?: "UnknownNullException", e)


inline fun <reified I : Any, O : Any> HResult<I>.withSuccess(action: (I) -> HResult<O>): HResult<O> =
		this.handleReturn { action(it) }

inline infix fun <reified I1 : Any, reified I2 : Any> HResult<I1>.and(
		hResult: HResult<I2>,
): HResult<*> {
	if (this is HResult.Success && hResult is HResult.Success) return successResult("")
	this.handle({ return this }, { return this }, { return this })
	hResult.handle({ return hResult }, { return hResult }, { return hResult })
	return errorResult(ErrorKeys.ERROR_GENERAL, "Unknown case for both results")
}

inline fun <reified I : Any> HResult<I>.handle(
		onLoading: () -> Unit = {},
		onEmpty: () -> Unit = {},
		onError: (HResult.Error) -> Unit = {},
		onSuccess: (I) -> Unit = {}
) = when (this) {
	is HResult.Success -> onSuccess(this.data)
	HResult.Loading -> onLoading()
	HResult.Empty -> onEmpty()
	is HResult.Error -> onError(this)
}

inline fun <reified I : Any, O : Any> HResult<I>.handledReturnAny(
		onLoading: () -> O? = { null },
		onEmpty: () -> O? = { null },
		onError: (HResult.Error) -> O? = { null },
		onSuccess: (I) -> O? = { null }
): O? = when (this) {
	is HResult.Success -> onSuccess(this.data)
	HResult.Loading -> onLoading()
	HResult.Empty -> onEmpty()
	is HResult.Error -> onError(this)
}


inline fun <reified I : Any, O : Any> HResult<I>.handleReturn(
		onLoading: () -> HResult<O> = { this as HResult.Loading },
		onEmpty: () -> HResult<O> = { this as HResult.Empty },
		onError: (HResult.Error) -> HResult<O> = { this as HResult.Error },
		onSuccess: (I) -> HResult<O>
): HResult<O> = when (this) {
	is HResult.Success -> onSuccess(this.data)
	HResult.Loading -> onLoading()
	HResult.Empty -> onEmpty()
	is HResult.Error -> onError(this)
}