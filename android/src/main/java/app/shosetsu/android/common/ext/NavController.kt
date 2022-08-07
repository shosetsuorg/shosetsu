package app.shosetsu.android.common.ext

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions

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
 * Shosetsu
 *
 * @since 07 / 08 / 2022
 * @author Doomsdayrs
 */

/**
 * Navigate safely, ignoring any exceptions that get thrown
 */
fun NavController.navigateSafely(
	@IdRes resId: Int,
	args: Bundle? = null,
	navOptions: NavOptions? = null
) {
	try {
		navigate(resId, args, navOptions)
	} catch (ignored: Exception) {
		// ignore dup
	}
}