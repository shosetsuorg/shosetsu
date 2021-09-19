package app.shosetsu.android.common.ext

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.work.ListenableWorker

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
 * 31 / 07 / 2020
 */

/** @see [toast] */
fun ListenableWorker.toast(
	length: Int = Toast.LENGTH_SHORT,
	message: () -> String,
) {
	launchUI {
		applicationContext.toast(message(), length)
	}
}

fun ListenableWorker.toast(
	message: String,
	length: Int = Toast.LENGTH_SHORT,
) {
	launchUI {
		applicationContext.toast(message, length)
	}
}

/** @see [toast] */
fun ListenableWorker.toast(
	@StringRes message: Int,
	length: Int = Toast.LENGTH_SHORT,
) {
	launchUI {
		applicationContext.toast(message, length)
	}
}