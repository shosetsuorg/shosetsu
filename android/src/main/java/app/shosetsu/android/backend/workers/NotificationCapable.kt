package app.shosetsu.android.backend.workers

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.StringRes

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
	val notification: Notification.Builder
	val notificationManager: NotificationManager
	val notifyContext: Context
	val notificationId: Int
	fun notify(@StringRes messageId: Int, action: Notification.Builder.() -> Unit = {}) =
		notify(notifyContext.getText(messageId), action)

	fun notify(message: CharSequence, action: Notification.Builder.() -> Unit = {}) {
		notificationManager.notify(
			notificationId,
			notification.apply {
				setContentText(message)
				action()
			}.build()
		)
	}
}