package app.shosetsu.android.backend.workers

import android.app.NotificationManager
import android.content.Context
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationManagerCompat

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * 09 / 02 / 2021
 */
interface NotificationCapable {
	/**
	 * Base that [notify] constructs off of, set this with shared content
	 */
	val baseNotificationBuilder: Builder

	/**
	 * NotificationManager used by [notify]
	 * @see NotificationManager
	 */
	val notificationManager: NotificationManagerCompat

	/**
	 * Context used by [notify] to get string res
	 */
	val notifyContext: Context

	val defaultNotificationID: Int

	fun notify(
		@StringRes messageId: Int,
		notificationId: Int = this.defaultNotificationID,
		action: Builder.() -> Unit = {}
	) = notify(notifyContext.getText(messageId), notificationId, action)

	fun notify(
		contentText: CharSequence,
		notificationId: Int = this.defaultNotificationID,
		action: Builder.() -> Unit = {}
	) {
		notificationManager.notify(
			notificationId,
			baseNotificationBuilder.apply {
				setContentText(contentText)
				action()
			}.build()
		)
	}
}