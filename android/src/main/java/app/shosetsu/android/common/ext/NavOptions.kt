package app.shosetsu.android.common.ext

import androidx.navigation.NavOptionsBuilder
import app.shosetsu.android.R

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
 * Shosetsu
 *
 * @since 22 / 07 / 2022
 * @author Doomsdayrs
 */

fun NavOptionsBuilder.setShosetsuTransition() {
	anim {
		enter = R.anim.fragment_fade_in
		popEnter = R.anim.fragment_fade_in

		exit = R.anim.fragment_fade_out
		popExit = R.anim.fragment_fade_out
	}
}