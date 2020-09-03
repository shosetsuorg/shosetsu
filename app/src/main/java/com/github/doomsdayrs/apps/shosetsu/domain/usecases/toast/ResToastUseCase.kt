package com.github.doomsdayrs.apps.shosetsu.domain.usecases.toast

import android.app.Application
import android.widget.Toast
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchUI
import com.github.doomsdayrs.apps.shosetsu.common.ext.toast

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
 * 14 / 08 / 2020
 */
class ResToastUseCase(
		private val application: Application
) {

	operator fun invoke(duration: Int = Toast.LENGTH_SHORT, message: () -> Int) {
		launchUI {
			application.toast(
					duration = duration,
					string = application.getString(message())
			)
		}
	}
}