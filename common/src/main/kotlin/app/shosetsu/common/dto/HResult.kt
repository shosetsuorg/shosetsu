package app.shosetsu.common.dto

import app.shosetsu.common.consts.ErrorKeys


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
 * A [List] of the type in an [HResult]
 */
typealias HList<T> = HResult<List<T>>

/**
 * shosetsu
 * 01 / 05 / 2020
 *
 * Handles upstream data results
 */
sealed class HResult<out T> {

	/** The operation was a success, here is your data [data] */
	data class Success<out T>(
		/** Returned data */
		val data: T,
	) : HResult<T>()

	/**
	 * This states that the operation has failed
	 * @param code ERROR code
	 * @param message ERROR message
	 * @param exception ERROR Cause
	 */
	@Suppress("MemberVisibilityCanBePrivate")
	data class Error(
		val code: Int,
		val message: String,
		val exception: Exception? = null,
	) : HResult<Nothing>()

	/**
	 * This states that the operation is currently pending
	 * Ideally only ever used in [kotlinx.coroutines.flow.Flow]
	 */
	object Loading : HResult<Nothing>()

	/** This states that the operation has returned nothing */
	object Empty : HResult<Nothing>();
}

/** This is a quick way to toss a success */
inline fun <reified T> successResult(data: T): HResult.Success<T> = HResult.Success(data)

fun successResult(): HResult.Success<*> = HResult.Success(Unit)

/** This is a quick way to create a loading*/
fun loading(): HResult.Loading = HResult.Loading

val loading = HResult.Loading

/** This is a quick way to create an empty result*/
fun emptyResult(): HResult.Empty = HResult.Empty

val empty = HResult.Empty

/** This is an easy way to create an error*/
fun errorResult(code: Int, message: String, error: Exception? = null): HResult.Error =
	HResult.Error(code, message, error)


/** This is an easy way to create an error via its exception */
fun errorResult(code: Int, error: Exception? = null): HResult.Error =
	HResult.Error(
		code, error?.message
			?: "UnknownException", error
	)


fun errorResult(e: NullPointerException): HResult.Error =
	HResult.Error(
		ErrorKeys.ERROR_NPE, e.message
			?: "UnknownNullException", e
	)

/**
 * Used to sequence HResult returning methods together
 *
 * Will get the result of [this] and [other] then process them,
 * this means that [other] will always run
 */
inline infix fun <reified I1, reified I2> HResult<I1>.thenAlso(
	other: HResult<I2>,
): HResult<Pair<I1, I2>> {
	if (this is HResult.Success && other is HResult.Success) return successResult(this.data to other.data)

	/** Returns the reason why the [HResult] [I1] is not a success */
	this.handle(
		onLoading = { return loading },
		onEmpty = { return empty },
		onError = { return it }
	)

	/** Returns the reason why the [HResult] [I2] is not a success */
	other.handle(
		onLoading = { return loading },
		onEmpty = { return empty },
		onError = { return it }
	)

	/**
	 * Default message for when the above somehow does not catch the issue
	 */
	return errorResult(ErrorKeys.ERROR_GENERAL, "Unknown case for both results")
}

/**
 * Sequence HResults one after another
 * If [this] fails, then it will return the status, otherwise
 * [next] is run, and it will check for next
 */
inline infix fun <reified I1, reified I2> HResult<I1>.ifSo(
	next: () -> HResult<I2>,
): HResult<Pair<I1, I2>> {
	var result1: I1? = null
	var result2: I2? = null

	/** Handles this result, if a success continues to the [next] */
	this.handle(
		onLoading = { return loading },
		onEmpty = { return empty },
		onError = { return it }
	) {
		result1 = it
	}

	/** Executes the the next block, */
	with(next()) {
		handle(
			onLoading = { return loading },
			onEmpty = { return empty },
			onError = { return it }
		) {
			result2 = it
		}
	}

	/**
	 * Both were a success, exiting
	 */
	return successResult(result1!! to result2!!)
}

/**
 * Convenience method to easily handle the HResult
 */
inline fun <reified I> HResult<I>.handle(
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


/**
 * If [this] is an [HResult.Success], returns [I]
 * But if [this] is not an [HResult.Success], returns a null
 */
inline fun <reified I> HResult<I>.unwrap(): I? = when (this) {
	is HResult.Success -> this.data
	HResult.Loading -> null
	HResult.Empty -> null
	is HResult.Error -> null
}

/**
 * If [this] is an [HResult.Success], returns [I]
 * But if [this] is not an [HResult.Success], returns a null
 *
 * Unlike the default form of [unwrap] with no parameters,
 * this one allows you to intercept the other results
 */
inline fun <reified I> HResult<I>.unwrap(
	onLoading: () -> I? = { null },
	onEmpty: () -> I? = { null },
	onError: (HResult.Error) -> I? = { null },
): I? = when (this) {
	is HResult.Success -> this.data
	HResult.Loading -> onLoading()
	HResult.Empty -> onEmpty()
	is HResult.Error -> onError(this)
}


/**
 *  similar to [unwrap], but
 *  opens up process to allow custom values for when [this] is not an [HResult.Success]
 *
 *  Shares similarities with [transform]
 *
 *  @see unwrap
 *  @see transform
 */
inline fun <reified I, O> HResult<I>.transmogrify(
	onLoading: () -> O? = { null },
	onEmpty: () -> O? = { null },
	onError: (HResult.Error) -> O? = { null },
	transformSuccess: (I) -> O? = { null }
): O? = when (this) {
	is HResult.Success -> transformSuccess(this.data)
	HResult.Loading -> onLoading()
	HResult.Empty -> onEmpty()
	is HResult.Error -> onError(this)
}


/**
 * Transform [this] [HResult] of [I] into a [HResult.Success] of [O]
 *
 * Note, Only used when the operation is guaranteed to succeed in all case
 */
inline fun <reified I, reified O> HResult<I>.transformToSuccess(
	transformSuccess: (I) -> O
): HResult<O> = when (this) {
	is HResult.Success -> successResult(transformSuccess(this.data))
	HResult.Loading -> this as HResult.Loading
	HResult.Empty -> this as HResult.Empty
	is HResult.Error -> this
}

/**
 * Transform [this] [HResult] of [I] into a [HResult] of [O]
 */
inline fun <reified I, O> HResult<I>.transform(
	transformSuccess: (I) -> HResult<O>
): HResult<O> = when (this) {
	is HResult.Success -> transformSuccess(this.data)
	HResult.Loading -> this as HResult.Loading
	HResult.Empty -> this as HResult.Empty
	is HResult.Error -> this
}


/**
 * Transform [this] [HResult] of [I] into a [HResult] of [O]
 *
 * This exposes the methods to apply custom transformations
 */
inline fun <reified I, O> HResult<I>.transform(
	onLoading: () -> HResult<O> = { this as HResult.Loading },
	onEmpty: () -> HResult<O> = { this as HResult.Empty },
	onError: (HResult.Error) -> HResult<O> = { this as HResult.Error },
	transformSuccess: (I) -> HResult<O>
): HResult<O> = when (this) {
	is HResult.Success -> transformSuccess(this.data)
	HResult.Loading -> onLoading()
	HResult.Empty -> onEmpty()
	is HResult.Error -> onError(this)
}

/**
 * This allows an [HResult] to be "caught". This is useful when you need to handle an [HResult]
 * in a catching manner
 */
inline fun <reified T> HResult<T>.catch(notASuccess: () -> HResult<T>): HResult<T> =
	takeIf { it is HResult.Success } ?: notASuccess()