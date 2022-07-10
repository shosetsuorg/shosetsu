package app.shosetsu.android.view.controller.base

import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import app.shosetsu.android.common.ext.logID
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
 */

/**
 * shosetsu
 * 03 / 06 / 2020
 *
 * For views with an FAB, to provide proper transition support
 */
interface ExtendedFABController {
	interface EFabMaintainer {
		fun hide()
		fun show()
		fun setOnClickListener(onClick: ((View) -> Unit)?)
		fun shrink()
		fun extend()
		fun setText(@StringRes textRes: Int)
		fun setIconResource(@DrawableRes iconRes: Int)

	}

	/**
	 * Hide the FAB
	 */
	fun hideFAB(fab: EFabMaintainer) {
		Log.d(logID(), "Hiding FAB")
		fab.hide()
	}

	/**
	 * Show the FAB
	 */
	fun showFAB(fab: EFabMaintainer) {
		Log.d(logID(), "Showing FAB")
		fab.show()
	}

	/**
	 * Reset the fab to its original state
	 */
	@CallSuper
	fun resetFAB(fab: EFabMaintainer) {
		Log.d(logID(), "Resetting FAB listeners")
		fab.setOnClickListener(null)
		manipulateFAB(fab)
	}

	/**
	 * Change FAB for your use case
	 */
	fun manipulateFAB(fab: EFabMaintainer)
}

@Composable
fun syncFABWithCompose(
	state: LazyListState,
	fab: ExtendedFABController.EFabMaintainer
) {
	LaunchedEffect(state.isScrollInProgress) {
		launch {
			if (state.isScrollInProgress) {
				fab.hide()
			} else {
				fab.show()
				if (state.firstVisibleItemIndex > 1)
					fab.shrink()
				else fab.extend()
			}
		}
	}
}

@Composable
fun syncFABWithCompose(
	state: LazyGridState,
	fab: ExtendedFABController.EFabMaintainer
) {
	LaunchedEffect(state.isScrollInProgress) {
		launch {
			if (state.isScrollInProgress) {
				fab.hide()
			} else {
				fab.show()
				if (state.firstVisibleItemIndex > 1)
					fab.shrink()
				else fab.extend()
			}
		}
	}
}

