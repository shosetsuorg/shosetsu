package app.shosetsu.common.dto

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

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
 * Maps the latest result of a Flow<HResult<*>>
 */
inline fun <reified I : Any, O : Any> Flow<HResult<I>>.mapLatestResult(
		noinline onLoading: suspend () -> HResult<O> = { loading() },
		noinline onEmpty: suspend () -> HResult<O> = { emptyResult() },
		noinline onError: suspend (HResult.Error) -> HResult<O> = { it },
		noinline onSuccess: suspend (I) -> HResult<O>
): Flow<HResult<O>> = mapLatest { result ->
	result.handleReturn(
			onLoading = { onLoading() },
			onEmpty = { onEmpty() },
			onError = { onError(it) },
			onSuccess = { onSuccess(it) }
	)
}


/** Converts each [Convertible] emitted by the [Flow] from its [O] form to its [I] form */
inline fun <reified I : Convertible<O>, reified O : Any> Flow<I>.mapLatestTo()
		: Flow<O> = mapLatest { it.convertTo() }

/** Converts each [List] of [Convertible] emitted by the [Flow] from its [O] form to its [I] form */
inline fun <reified I : Convertible<O>, reified O : Any> Flow<List<I>>.mapLatestListTo()
		: Flow<List<O>> = mapLatest { it.mapTo() }

/** Converts each value emitted by the [Flow] as an [HResult.Success] */
inline fun <reified I : Any> Flow<I>.mapLatestToSuccess(): Flow<HResult<I>> =
		mapLatest { successResult(it) }

/** Converts a [HResult.Success] of a [List] of [Convertible]s from its [I] form to its [O] form */
inline fun <reified I : Convertible<O>, reified O : Any> Flow<HResult<List<I>>>.mapLatestResultListTo()
		: Flow<HResult<List<O>>> = mapLatest { it.mapListTo() }


/** Converts a [HResult.Success] of a [Convertible] from its [I] form to its [O] form */
inline fun <reified I : Convertible<O>, reified O : Any> Flow<HResult<I>>.mapLatestResultTo()
		: Flow<HResult<O>> = mapLatest { it.mapTo() }