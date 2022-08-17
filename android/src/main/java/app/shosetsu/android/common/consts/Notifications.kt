package app.shosetsu.android.common.consts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import app.shosetsu.android.R
import app.shosetsu.android.common.ext.notificationManager

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
	const val CHANNEL_BACKUP: String = "shosetsu_backup"
	const val ID_BACKUP: Int = 1959
	const val ID_RESTORE: Int = 1940
	const val ID_EXPORT: Int = 1941

	const val CHANNEL_UPDATE: String = "shosetsu_updater"
	const val ID_CHAPTER_UPDATE: Int = 1917

	const val CHANNEL_DOWNLOAD: String = "shosetsu_download"
	const val ID_CHAPTER_DOWNLOAD: Int = 1949
	const val ID_EXTENSION_DOWNLOAD: Int = 1948

	const val CHANNEL_REPOSITORY_UPDATE = "shosetsu_repository_update"
	const val ID_REPOSITORY_UPDATE = 1953

	const val CHANNEL_APP_UPDATE: String = "shosetsu_app_update"
	const val ID_APP_UPDATE: Int = 1991
	const val ID_APP_UPDATE_INSTALL: Int = 1944


	fun createChannels(context: Context) {
		// Ignore if is a lower android version
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

		with(context) {
			val channels = listOf(
				notificationChannel(
					CHANNEL_UPDATE,
					R.string.notification_channel_name_novel_update,
					NotificationManager.IMPORTANCE_HIGH
				),
				notificationChannel(
					CHANNEL_DOWNLOAD,
					R.string.notification_channel_name_download,
					NotificationManager.IMPORTANCE_LOW
				),
				notificationChannel(
					CHANNEL_APP_UPDATE,
					R.string.notification_channel_name_app_update,
					NotificationManager.IMPORTANCE_HIGH
				),
				notificationChannel(
					CHANNEL_BACKUP,
					R.string.notification_channel_name_backup,
					NotificationManager.IMPORTANCE_LOW
				),
				notificationChannel(
					CHANNEL_REPOSITORY_UPDATE,
					R.string.notification_channel_name_repository_update,
					NotificationManager.IMPORTANCE_DEFAULT
				)
			)
			notificationManager.createNotificationChannels(channels)
		}
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private fun Context.notificationChannel(
		id: String,
		@StringRes name: Int,
		importance: Int
	) = NotificationChannel(
		id,
		getString(name),
		importance
	)

}