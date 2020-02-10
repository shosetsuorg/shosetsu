package com.github.doomsdayrs.apps.shosetsu.variables.obj

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
 * ====================================================================
 */

/**
 * shosetsu
 * 08 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 */
object Broadcasts {
    /**
     * Tells receiver to update it's recycler view's adapter
     */
    const val BROADCAST_NOTIFY_DATA_CHANGE = "notifyDataChange"

    const val DOWNLOADS_MARK_ERROR = "markError"
    const val DOWNLOADS_REMOVE = "removeItem"
    const val DOWNLOADS_TOGGLE = "toggleStatus"

    const val DOWNLOADS_RECIEVED_URL = "chapterURL"

}