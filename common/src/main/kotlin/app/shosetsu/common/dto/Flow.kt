package app.shosetsu.common.dto

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

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
 * Represents a [Flow] of [HResult]
 */
typealias HFlow<T> = Flow<HResult<T>>

/**
 * Represent a [Flow] of a [List] of [HResult]
 */
typealias HListFlow<T> = Flow<HList<T>>

/**
 * Maps the latest result of a Flow<HResult<*>>
 */
@ExperimentalCoroutinesApi
inline fun <reified I, O> HFlow<I>.mapLatestResult(
	noinline onLoading: suspend () -> HResult<O> = { loading() },
	noinline onEmpty: suspend () -> HResult<O> = { emptyResult() },
	noinline onError: suspend (HResult.Error) -> HResult<O> = { it },
	noinline onSuccess: suspend (I) -> HResult<O>
): HFlow<O> = mapLatest { result ->
	result.transform(
		onLoading = { onLoading() },
		onEmpty = { onEmpty() },
		onError = { onError(it) },
		transformSuccess = { onSuccess(it) }
	)
}

/**
 * Maps each result of a Flow<HResult<*>>
 */
inline fun <reified I, O> HFlow<I>.mapResult(
	noinline onLoading: suspend () -> HResult<O> = { loading() },
	noinline onEmpty: suspend () -> HResult<O> = { emptyResult() },
	noinline onError: suspend (HResult.Error) -> HResult<O> = { it },
	noinline onSuccess: suspend (I) -> HResult<O>
): HFlow<O> = map { result ->
	result.transform(
		onLoading = { onLoading() },
		onEmpty = { onEmpty() },
		onError = { onError(it) },
		transformSuccess = { onSuccess(it) }
	)
}


/**
 * Combine a [List] of [HResult] [Flow] into an [HListFlow]
 *
 * @see [combine]
 */
@ExperimentalCoroutinesApi
inline fun <reified T> Flow<List<HResult<T>>>.combineResultList(): HListFlow<T> =
	transformLatest { list ->
		emit(list.combine())
	}

/**
 * Combine a [List] of [HFlow] into an [HListFlow] using [combineResultList]
 *
 * @see [combineResultList]
 */
@ExperimentalCoroutinesApi
inline fun <reified T> List<HFlow<T>>.combineResults(): HListFlow<T> =
	combine(this) {
		it.toList()
	}.combineResultList()

/** Converts each [Convertible] emitted by the [Flow] from its [O] form to its [I] form */
@ExperimentalCoroutinesApi
inline fun <reified I : Convertible<O>, reified O> Flow<I>.mapLatestTo()
		: Flow<O> = mapLatest { it.convertTo() }

/** Converts each [List] of [Convertible] emitted by the [Flow] from its [O] form to its [I] form */
@ExperimentalCoroutinesApi
inline fun <reified I : Convertible<O>, reified O : Any> Flow<List<I>>.mapLatestListTo()
		: Flow<List<O>> = mapLatest { it.convertList() }

/** Converts each value emitted by the [Flow] as an [HResult.Success] */
@ExperimentalCoroutinesApi
inline fun <reified I> Flow<I>.mapLatestToSuccess(): HFlow<I> =
	mapLatest { successResult(it) }

/** Converts each value emitted by the [Flow] as an [HResult.Success] */
inline fun <reified I> Flow<I>.mapToSuccess(): HFlow<I> =
	map { successResult(it) }

/** Converts a [HResult.Success] of a [List] of [Convertible]s from its [I] form to its [O] form */
@ExperimentalCoroutinesApi
inline fun <reified I : Convertible<O>, reified O : Any> HFlow<List<I>>.mapLatestResultListTo()
		: HFlow<List<O>> = mapLatest { it.convertList() }


/** Converts a [HResult.Success] of a [Convertible] from its [I] form to its [O] form */
@ExperimentalCoroutinesApi
inline fun <reified I : Convertible<O>, reified O : Any> HFlow<I>.mapLatestResultTo()
		: HFlow<O> = mapLatest { it.convertToSettingItems() }

/** Converts a [HResult.Success] of a [Convertible] from its [I] form to its [O] form */
inline fun <reified I : Convertible<O>, reified O : Any> HFlow<I>.mapResultTo()
		: HFlow<O> = map { it.convertToSettingItems() }
