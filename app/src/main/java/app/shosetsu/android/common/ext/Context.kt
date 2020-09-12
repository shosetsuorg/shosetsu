package app.shosetsu.android.common.ext

import android.Manifest.permission.*
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.widget.Toast.*
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import app.shosetsu.android.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.R

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
 * 07 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 * <p>
 *     I have to admit to copying tachiyomi ;-;
 * </p>
 */


/**
 * Display a toast in this context.
 *
 * @param resource the text resource.
 * @param duration the duration of the toast. Defaults to short.
 */
fun Context.toast(@StringRes resource: Int, duration: Int = LENGTH_SHORT) {
	makeText(this, resource, duration).show()
}

fun Context.toast(string: String, duration: Int = LENGTH_SHORT) {
	makeText(this, string, duration).show()
}

fun Context.checkActivitySelfPermission(@NonNull permission: String): Int =
		ActivityCompat.checkSelfPermission(this, permission)

/**
 * Property to get the notification manager from the context.
 */
val Context.notificationManager: NotificationManager
	get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

fun Context.requestPerms() {
	if (
			checkActivitySelfPermission(WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED
			||
			checkActivitySelfPermission(WAKE_LOCK) != PERMISSION_GRANTED
	) ActivityCompat.requestPermissions(
			this as Activity,
			arrayOf(
					READ_EXTERNAL_STORAGE,
					WRITE_EXTERNAL_STORAGE,
					WAKE_LOCK),
			1)
}

/**
 * Regret message if a feature isn't re-introduced yet
 */
fun Context.regret(): Unit = toast(R.string.regret, duration = LENGTH_LONG)

@Deprecated("Move to [Settings]")
fun Context.calculateColumnCount(columnWidthDp: Float, settings: ShosetsuSettings): Int {
// For example columnWidthdp=180
	val c = if (resources.configuration.orientation == 1)
		settings.columnsInNovelsViewP
	else settings.columnsInNovelsViewH

	val displayMetrics = resources.displayMetrics
	val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density

	return if (c <= 0) (screenWidthDp / columnWidthDp + 0.5).toInt()
	else (screenWidthDp / (screenWidthDp / c) + 0.5).toInt()
}