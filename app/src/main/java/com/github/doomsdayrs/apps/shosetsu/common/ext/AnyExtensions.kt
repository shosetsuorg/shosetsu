package com.github.doomsdayrs.apps.shosetsu.common.ext

import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.util.concurrent.TimeUnit

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
 * 04 / 03 / 2020
 *
 * @author github.com/doomsdayrs
 */

/**
 * Serialize object to string
 *
 * @return Serialised string
 * @throws IOException exception
 */
@Throws(IOException::class)
fun Any.serializeToString(): String {
	val byteArrayOutputStream = ByteArrayOutputStream()
	val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
	objectOutputStream.writeObject(this)
	val bytes = byteArrayOutputStream.toByteArray()
	return "serial-" + Base64.encodeToString(bytes, Base64.NO_WRAP)
}


/**
 * Converts Array of Strings into a String
 *
 * @return String Array
 */
fun Array<String>.convertArrayToString(): String {
	if (isNotEmpty()) {
		for (x in indices) {
			this[x] = this[x].replace(",", ">,<")
		}
		return contentToString()
	}
	return "[]"
}

@Suppress("unused")
inline fun <reified T : Any> T.logID() = T::class.java.simpleName

/**
 * Freezes the thread for x time
 *
 * @param time time, default in MS
 */
inline fun <reified T : Any> T.wait(time: Int, unit: TimeUnit = TimeUnit.MILLISECONDS) =
		try {
			unit.sleep(time.toLong())
		} catch (e: InterruptedException) {
			Log.e(logID(), "Failed to wait", e)
		}


fun ViewModel.launchUI(block: suspend CoroutineScope.() -> Unit) =
		GlobalScope.launch(
				viewModelScope.coroutineContext + Dispatchers.Main,
				CoroutineStart.DEFAULT,
				block
		)

fun ViewModel.launchIO(block: suspend CoroutineScope.() -> Unit) =
		GlobalScope.launch(
				viewModelScope.coroutineContext + Dispatchers.IO,
				CoroutineStart.DEFAULT,
				block
		)

fun ViewModel.launchAsync(block: suspend CoroutineScope.() -> Unit) =
		GlobalScope.launch(
				viewModelScope.coroutineContext + Dispatchers.Default,
				CoroutineStart.UNDISPATCHED,
				block
		)

fun ViewModel.launchFree(block: suspend CoroutineScope.() -> Unit) =
		GlobalScope.launch(
				viewModelScope.coroutineContext + Dispatchers.Unconfined,
				CoroutineStart.UNDISPATCHED,
				block
		)

fun launchUI(block: suspend CoroutineScope.() -> Unit) =
		GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT, block)

fun launchIO(block: suspend CoroutineScope.() -> Unit) =
		GlobalScope.launch(Dispatchers.IO, CoroutineStart.DEFAULT, block)

fun launchAsync(block: suspend CoroutineScope.() -> Unit) =
		GlobalScope.launch(Dispatchers.Default, CoroutineStart.UNDISPATCHED, block)

fun launchFree(block: suspend CoroutineScope.() -> Unit) =
		GlobalScope.launch(Dispatchers.Unconfined, CoroutineStart.UNDISPATCHED, block)
