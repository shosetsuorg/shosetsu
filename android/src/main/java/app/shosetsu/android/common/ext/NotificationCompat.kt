package app.shosetsu.android.common.ext

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat.*
import androidx.core.graphics.drawable.IconCompat
import app.shosetsu.android.R
import app.shosetsu.android.backend.receivers.NotificationBroadcastReceiver

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

fun Builder.setOngoing() = setOngoing(true)

fun Builder.setNotOngoing() = setOngoing(false)

fun Builder.removeProgress() =
	setProgress(0, 0, false)

const val ACTION_REPORT_ERROR: String = "action_report_error"
const val EXTRA_EXCEPTION: String = "action_report_error"

/**
 * @param context Context to use
 * @param notificationId Id of notification to dismiss after reported
 * @param throwable Exception to report
 */
fun Builder.addReportErrorAction(context: Context, notificationId: Int, throwable: Throwable) {
	val intent = Intent(
		context,
		NotificationBroadcastReceiver::class.java
	).apply {
		action = ACTION_REPORT_ERROR
		putExtra(EXTRA_EXCEPTION, throwable)
		putExtra(EXTRA_NOTIFICATION_ID, notificationId)
	}

	addAction(
		actionBuilder(
			IconCompat.createWithResource(context, R.drawable.error_outline),
			context.getString(R.string.report_bug),
			PendingIntent.getBroadcast(
				context,
				0,
				intent,
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
			)
		).build()
	)
}

fun notificationBuilder(context: Context, channel: String): Builder = Builder(context, channel)


fun actionBuilder(icon: IconCompat?, title: CharSequence?, intent: PendingIntent?): Action.Builder =
	Action.Builder(icon, title, intent)