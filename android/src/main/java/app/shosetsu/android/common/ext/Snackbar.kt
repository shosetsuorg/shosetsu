package app.shosetsu.android.common.ext

import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

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

fun Snackbar.setOnDismissedNotByAction(action: (Snackbar?, @ParameterName("event") Int) -> Unit) =
	addCallback(object :
		BaseTransientBottomBar.BaseCallback<Snackbar>() {
		override fun onDismissed(
			transientBottomBar: Snackbar,
			event: Int
		) {
			if (event != DISMISS_EVENT_ACTION)
				action(transientBottomBar, event)
		}
	})

fun Snackbar.setOnDismissed(action: (Snackbar?, @ParameterName("event") Int) -> Unit) =
	addCallback(object :
		BaseTransientBottomBar.BaseCallback<Snackbar>() {
		override fun onDismissed(
			transientBottomBar: Snackbar,
			event: Int
		) = action(transientBottomBar, event)
	})