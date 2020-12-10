package app.shosetsu.android.common.ext

import android.util.Log.*
import app.shosetsu.common.dto.HResult

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
 *
 * 14 / 08 / 2020
 */

inline fun <reified T : Any> T.logError(error: () -> HResult.Error) {
	error().let { (k, m, e) ->
		e(logID(), "Error Result:\t$k by $e\tmessage:\n$m")
		if (e != null) {
			e(logID(), "\nStacktrace${e.stackTrace.contentToString()}")
			e(logID(), "\nStacktrace${e.cause}")
		}
	}
}

inline fun <reified T> T.inform(message: String?, t: Throwable? = null) = logI(message, t)

inline fun <reified T> T.logI(message: String?, t: Throwable? = null) = i(T::class.java.simpleName, message, t)

inline fun <reified T> T.logD(message: String?, t: Throwable? = null) = d(T::class.java.simpleName, message, t)

inline fun <reified T> T.logE(message: String?, t: Throwable? = null) = e(T::class.java.simpleName, message, t)

inline fun <reified T> T.warn(message: String?, t: Throwable? = null) = logW(message, t)

inline fun <reified T> T.logW(message: String?, t: Throwable? = null) = w(T::class.java.simpleName, message, t)

inline fun <reified T> T.logV(message: String?, t: Throwable? = null) = v(T::class.java.simpleName, message, t)

inline fun <reified T> T.logWTF(message: String?, t: Throwable? = null) = wtf(T::class.java.simpleName, message, t)
