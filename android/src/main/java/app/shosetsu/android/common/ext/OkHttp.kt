package app.shosetsu.android.common.ext

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okio.IOException
import kotlin.coroutines.resumeWithException

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
 * Shosetsu
 *
 * @since 24 / 06 / 2022
 * @author Doomsdayrs
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun Call.executeAsync(): Response = suspendCancellableCoroutine { continuation ->
	continuation.invokeOnCancellation {
		this.cancel()
	}
	this.enqueue(object : Callback {
		override fun onFailure(call: Call, e: IOException) {
			continuation.resumeWithException(e)
		}

		override fun onResponse(call: Call, response: Response) {
			continuation.resume(value = response, onCancellation = { call.cancel() })
		}
	})
}