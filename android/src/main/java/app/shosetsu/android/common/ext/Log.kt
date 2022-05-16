package app.shosetsu.android.common.ext

import android.util.Log.*
import java.io.PrintStream

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

var fileOut: PrintStream? = null

const val CRESET: String = "\u001B[0m"
const val CRED: String = "\u001B[31m"

fun writeT(t: Throwable? = null) {
	if (t != null)
		fileOut?.println(t.stackTraceToString())
}

@Suppress("unused")
inline fun <reified T> T.logI(message: String?, t: Throwable? = null): Int {
	val name = Thread.currentThread().stackTrace[2].methodName ?: NULL_METHOD_NAME
	val msg = "${name}:\t$message"
	val tag = T::class.java.simpleName

	fileOut?.println("i:\t$tag:\t$msg")

	writeT(t)

	return i(tag, msg, t)
}

@Suppress("unused")
inline fun <reified T> T.logD(message: String?, t: Throwable? = null): Int {
	val name = Thread.currentThread().stackTrace[2].methodName ?: NULL_METHOD_NAME
	val msg = "${name}:\t$message"
	val tag = T::class.java.simpleName

	fileOut?.println("D:\t$tag:\t$msg")

	writeT(t)

	return d(tag, msg, t)
}

@Suppress("unused")
inline fun <reified T> T.logE(message: String?, t: Throwable? = null): Int {
	val name = Thread.currentThread().stackTrace[2].methodName ?: NULL_METHOD_NAME
	val msg = "${name}:\t$message"
	val tag = T::class.java.simpleName

	fileOut?.println("${CRED}e:\t$tag:\t$msg${CRESET}")

	writeT(t)

	return e(tag, msg, t)
}

@Suppress("unused")
inline fun <reified T> T.logW(message: String?, t: Throwable? = null): Int {
	val name = Thread.currentThread().stackTrace[2].methodName ?: NULL_METHOD_NAME
	val msg = "${name}:\t$message"
	val tag = T::class.java.simpleName

	fileOut?.println("w:\t$tag:\t$msg")

	writeT(t)

	return w(tag, msg, t)
}

@Suppress("unused")
inline fun <reified T> T.logV(message: String?, t: Throwable? = null): Int {
	val name = Thread.currentThread().stackTrace[2].methodName ?: NULL_METHOD_NAME
	val msg = "${name}:\t$message"
	val tag = T::class.java.simpleName

	fileOut?.println("v:\t$tag:\t$msg")

	writeT(t)

	return v(tag, msg, t)
}

@Suppress("unused")
inline fun <reified T> T.logWTF(message: String?, t: Throwable? = null): Int {
	val name = Thread.currentThread().stackTrace[2].methodName ?: NULL_METHOD_NAME
	val msg = "${name}:\t$message"
	val tag = T::class.java.simpleName

	fileOut?.println("wtf:\t$tag:\t$msg")

	writeT(t)

	return wtf(tag, msg, t)
}
