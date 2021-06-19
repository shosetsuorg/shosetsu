package app.shosetsu.android.domain.usecases.toast

import android.app.Application
import android.widget.Toast
import app.shosetsu.android.common.ext.toast

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
@Deprecated("Do not use")
class StringToastUseCase(
	private val application: Application,
) {

	operator fun invoke(duration: Int = Toast.LENGTH_SHORT, message: () -> String) {
		app.shosetsu.android.common.ext.launchUI {
			application.toast(
				duration = duration,
				string = message()
			)
		}
	}
}