package app.shosetsu.android.view.base

import android.view.View
import app.shosetsu.android.view.widget.SlidingUpBottomMenu

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
 * 13 / 12 / 2020
 *
 * For controllers to have a sliding up bottom menu
 */
interface BottomMenuController {
	var bottomMenuRetriever: (() -> SlidingUpBottomMenu?)?

	/**
	 * Creates a view to pass into the sliding up menu
	 *
	 * Built when the view is called to be shown
	 */
	fun getBottomMenuView(): View
}