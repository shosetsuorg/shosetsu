package app.shosetsu.android.backend

import android.view.MenuItem

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
 * ====================================================================
 */
/**
 * shosetsu
 * 26 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 *
 * <p>
 *     This file contains random methods/pieces of code that don't seem to be important or make their respective files messy
 * </p>
 */
class SHOWCASE {
	val catalogue: Int = 1
	val downloads: Int = 2
	val library: Int = 3
	val main: Int = 4
	val migration: Int = 5
	val novel: Int = 6
	val novelINFO: Int = 7
	val novelCHAPTERS: Int = 8
	val novelTRACKING: Int = 9
	val reader: Int = 10
	val search: Int = 11
	val updates: Int = 12
	val webView: Int = 13
}

/**
 * Demarks a list of items, setting only one to be checked.
 *
 * @param menuItems      Items to sort through
 * @param positionSpared Item to set checked
 * @param demarkAction   Any action to proceed with
 */
fun unmarkMenuItems(menuItems: Array<MenuItem>, positionSpared: Int, demarkAction: DeMarkAction?) {
	for (x in menuItems.indices) menuItems[x].isChecked = (x == positionSpared)
	demarkAction?.action(positionSpared)
}

/**
 * Abstraction for Actions to take after demarking items. To simplify bulky code
 */
interface DeMarkAction {
	fun action(spared: Int)
}

