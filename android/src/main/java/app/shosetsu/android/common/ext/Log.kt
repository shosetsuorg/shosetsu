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
const val NULL_METHOD_NAME = "UnknownMethod"

inline fun <reified T : Any> T.logError(error: () -> HResult.Error) {
	error().let { (k, m, e) ->
		e(logID(), "Error Result:\t$k by $e\tmessage:\n$m")
		if (e != null) {
			e(logID(), "\nStacktrace${e.stackTrace.contentToString()}")
			e(logID(), "\nStacktrace${e.cause}")
		}
	}
}

inline fun <reified T> T.logI(message: String?, t: Throwable? = null): Int {
	val name = Thread.currentThread().stackTrace[2].methodName ?: NULL_METHOD_NAME
	return i(T::class.java.simpleName, "${name}:\t$message", t)
}

inline fun <reified T> T.logD(message: String?, t: Throwable? = null): Int {
	val name = Thread.currentThread().stackTrace[2].methodName ?: NULL_METHOD_NAME
	return d(T::class.java.simpleName, "${name}:\t$message", t)
}

inline fun <reified T> T.logE(message: String?, t: Throwable? = null): Int {
	val name = Thread.currentThread().stackTrace[2].methodName ?: NULL_METHOD_NAME
	return e(T::class.java.simpleName, "${name}:\t$message", t)
}

inline fun <reified T> T.logW(message: String?, t: Throwable? = null): Int {
	val name = Thread.currentThread().stackTrace[2].methodName ?: NULL_METHOD_NAME
	return w(T::class.java.simpleName, "${name}:\t$message", t)
}

inline fun <reified T> T.logV(message: String?, t: Throwable? = null): Int {
	val name = Thread.currentThread().stackTrace[2].methodName ?: NULL_METHOD_NAME
	return v(T::class.java.simpleName, "${name}:\t$message", t)
}

inline fun <reified T> T.logWTF(message: String?, t: Throwable? = null): Int {
	val name = Thread.currentThread().stackTrace[2].methodName ?: NULL_METHOD_NAME
	return wtf(T::class.java.simpleName, "${name}:\t$message", t)
}
