package app.shosetsu.android.view.base

import android.util.Log
import androidx.annotation.CallSuper
import app.shosetsu.android.common.ext.logID
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
interface FABController {

	/**
	 * Accept the FAB to your controller
	 */
	fun acceptFAB(fab: FloatingActionButton) {}

	/**
	 * Hide the FAB
	 */
	fun hideFAB(fab: FloatingActionButton) {
		Log.d(logID(), "Hiding FAB")
		fab.hide()
	}

	/**
	 * Show the FAB
	 */
	fun showFAB(fab: FloatingActionButton) {
		Log.d(logID(), "Showing FAB")
		fab.show()
	}

	/**
	 * Reset the fab to its original state
	 */
	@CallSuper
	fun resetFAB(fab: FloatingActionButton) {
		Log.d(logID(), "Resetting FAB listeners")
		fab.setOnClickListener(null)
		manipulateFAB(fab)
	}

	/**
	 * Change FAB for your use case
	 */
	fun manipulateFAB(fab: FloatingActionButton) {}

	/**
	 * Set the icon of the FAB, between the hide and show
	 */
	fun setFABIcon(fab: FloatingActionButton)
}