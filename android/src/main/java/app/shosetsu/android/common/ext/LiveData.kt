package app.shosetsu.android.common.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle

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

inline fun <T : HResult<D>, reified D> LiveData<T>.handleObserve(
	owner: LifecycleOwner,
	crossinline onLoading: () -> Unit = {},
	crossinline onEmpty: () -> Unit = {},
	crossinline onError: (HResult.Error) -> Unit = {},
	crossinline onSuccess: (D) -> Unit
) {
	observe(owner) {
		it.handle(
			onError = onError,
			onEmpty = onEmpty,
			onLoading = onLoading,
			onSuccess = onSuccess
		)
	}
}