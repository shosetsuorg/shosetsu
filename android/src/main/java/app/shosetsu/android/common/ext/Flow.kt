package app.shosetsu.android.common.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
 * @since 14 / 02 / 2022
 * @author Doomsdayrs
 */

fun <T> Flow<T>.collectLA(
	owner: LifecycleOwner,
	catch: suspend FlowCollector<T>.(Throwable) -> Unit,
	onCollect: FlowCollector<T>
) =
	owner.lifecycleScope.launch {
		owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
			catch(catch)
			collect(onCollect)
		}
	}

fun <T> Flow<T>.collectLatestLA(
	owner: LifecycleOwner,
	catch: suspend FlowCollector<T>.(Throwable) -> Unit,
	onCollect: FlowCollector<T>
) = owner.lifecycleScope.launch {
	owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
		collectLatest {
			catch(catch)
			collect(onCollect)
		}
	}
}