package com.github.doomsdayrs.apps.shosetsu.variables

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat

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

/**
 * Property to get the notification manager from the context.
 */
val Context.notificationManager: NotificationManager
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

fun Context.requestPerms() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED)
        ActivityCompat.requestPermissions(
                this as Activity,
                arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.WAKE_LOCK),
                1)
}

fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
    val className = serviceClass.name
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    @Suppress("DEPRECATION")
    return manager.getRunningServices(Integer.MAX_VALUE).any { className == it.service.className }
}
