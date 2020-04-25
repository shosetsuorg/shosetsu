package com.github.doomsdayrs.apps.shosetsu.variables

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
 * 04 / 03 / 2020
 *
 * @author github.com/doomsdayrs
 */

/**
 * Used to handle responses from backend to front end
 *
 * @param succeeded [Boolean] True if task completed successfully
 * @param failureReason [String] Reason why the task was not completed
 * @param e [Exception] Exception? of the error
 * @param value [T] Value to be returned
 */
data class HandledReturns<T>(val succeeded: Boolean = false, val failureReason: String = "", val e: Exception? = null, val value: T? = null)