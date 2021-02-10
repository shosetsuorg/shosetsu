package app.shosetsu.android.common.ext

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat.Action
import androidx.core.app.NotificationCompat.Builder
import androidx.core.graphics.drawable.IconCompat

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

fun notificationBuilder(context: Context, channel: String): Builder = Builder(context, channel)


fun actionBuilder(icon: IconCompat?, title: CharSequence?, intent: PendingIntent?): Action.Builder =
	Action.Builder(icon, title, intent)