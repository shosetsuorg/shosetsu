package app.shosetsu.android.common.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

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
 * shosetsu
 * 30 / 09 / 2020
 */

fun ViewModel.launchUI(block: suspend CoroutineScope.() -> Unit): Job =
	GlobalScope.launch(
		viewModelScope.coroutineContext + Dispatchers.Main,
		CoroutineStart.DEFAULT,
		block
	)

fun ViewModel.launchIO(block: suspend CoroutineScope.() -> Unit): Job =
	GlobalScope.launch(
		viewModelScope.coroutineContext + Dispatchers.IO,
		CoroutineStart.DEFAULT,
		block
	)

@ExperimentalCoroutinesApi
fun ViewModel.launchAsync(block: suspend CoroutineScope.() -> Unit): Job =
	GlobalScope.launch(
		viewModelScope.coroutineContext + Dispatchers.Default,
		CoroutineStart.UNDISPATCHED,
		block
	)

@ExperimentalCoroutinesApi
fun ViewModel.launchFree(block: suspend CoroutineScope.() -> Unit): Job =
	GlobalScope.launch(
		viewModelScope.coroutineContext + Dispatchers.Unconfined,
		CoroutineStart.UNDISPATCHED,
		block
	)
