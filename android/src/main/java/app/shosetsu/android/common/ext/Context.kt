package app.shosetsu.android.common.ext

import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Resources
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat

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
	try {
		makeText(this, resource, duration).show()
	} catch (e: Resources.NotFoundException) {
		logE("NotFoundException", e)
	}
}

fun Context.toast(string: String, duration: Int = LENGTH_SHORT) {
	makeText(this, string, duration).show()
}

fun Context.checkActivitySelfPermission(@NonNull permission: String): Int =
	ActivityCompat.checkSelfPermission(this, permission)

/**
 * Property to get the notification manager from the context.
 */
val Context.notificationManager: NotificationManagerCompat
	get() = NotificationManagerCompat.from(this)

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
			WAKE_LOCK
		),
		1
	)
}