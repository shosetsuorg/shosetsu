package com.github.doomsdayrs.apps.shosetsu.common.consts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.github.doomsdayrs.apps.shosetsu.common.ext.notificationManager

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
 */
object Notifications {
	const val CHANNEL_UPDATE: String = "shosetsu_updater"
	const val ID_CHAPTER_UPDATE: Int = 1917

	const val CHANNEL_DOWNLOAD: String = "shosetsu_download"
	const val ID_CHAPTER_DOWNLOAD: Int = 1949

	fun createChannels(context: Context) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
		val channels = listOf(
				NotificationChannel(
						CHANNEL_UPDATE,
						"Shosetsu Update",
						NotificationManager.IMPORTANCE_LOW
				),
				NotificationChannel(
						CHANNEL_DOWNLOAD,
						"Shosetsu Download",
						NotificationManager.IMPORTANCE_LOW
				)
		)
		context.notificationManager.createNotificationChannels(channels)
	}

}