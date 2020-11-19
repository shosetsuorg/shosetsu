package app.shosetsu.android.common.dto

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
): Flow<HResult<O>> = mapLatest {
	it.handleReturn(
			onLoading = { onLoading() },
			onEmpty = { onEmpty() },
			onError = { onError(it) },
			onSuccess = { onSuccess(it) }
	)
}

/**
 *
 */
inline fun <reified I : Any> Flow<I>.mapLatestToSuccess(): Flow<HResult<I>> =
		mapLatest { successResult(it) }

inline fun <reified I : Convertible<O>, reified O : Any> Flow<HResult<List<I>>>.mapLatestListTo()
		: Flow<HResult<List<O>>> = mapLatest { it.mapListTo() }

inline fun <reified I : Convertible<O>, reified O : Any> Flow<HResult<I>>.mapLatestTo()
		: Flow<HResult<O>> = mapLatest { it.mapTo() }