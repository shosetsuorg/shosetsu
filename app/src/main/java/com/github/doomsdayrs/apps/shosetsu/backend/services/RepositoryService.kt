package com.github.doomsdayrs.apps.shosetsu.backend.services

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.getSystemService
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.consts.LogConstants.SERVICE_CANCEL_PREVIOUS
import com.github.doomsdayrs.apps.shosetsu.common.consts.LogConstants.SERVICE_EXECUTE
import com.github.doomsdayrs.apps.shosetsu.common.consts.LogConstants.SERVICE_NEW
import com.github.doomsdayrs.apps.shosetsu.common.consts.LogConstants.SERVICE_NULLIFIED
import com.github.doomsdayrs.apps.shosetsu.common.consts.LogConstants.SERVICE_REJECT_RUNNING
import com.github.doomsdayrs.apps.shosetsu.common.consts.Notifications.CHANNEL_DOWNLOAD
import com.github.doomsdayrs.apps.shosetsu.common.consts.Notifications.ID_CHAPTER_DOWNLOAD
import com.github.doomsdayrs.apps.shosetsu.common.ext.isServiceRunning
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import needle.CancelableTask
import needle.Needle
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

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
 * 08 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 */
class RepositoryService : Service(), KodeinAware {
	companion object {

		/**
		 * Returns the status of the service.
		 *
		 * @param context the application context.
		 * @return true if the service is running, false otherwise.
		 */
		private fun isRunning(context: Context): Boolean =
				context.isServiceRunning(RepositoryService::class.java)

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 *
		 * @param context the application context.
		 */
		fun start(context: Context) {
			if (!isRunning(context)) {
				val intent = Intent(context, RepositoryService::class.java)
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
					context.startService(intent)
				} else {
					context.startForegroundService(intent)
				}
			} else Log.d(logID(), SERVICE_REJECT_RUNNING)
		}

		/**
		 * Stops the service.
		 *
		 * @param context the application context.
		 */
		fun stop(context: Context) {
			context.stopService(Intent(context, RepositoryService::class.java))
		}

		fun task(context: Context, progressUpdate: (String) -> Unit) {

		}

	}

	override val kodein: Kodein by closestKodein()

	private val notificationManager by lazy { getSystemService<NotificationManager>()!! }

	private val progressNotification by lazy {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Notification.Builder(this, CHANNEL_DOWNLOAD)
		} else {
			// Suppressed due to lower API
			@Suppress("DEPRECATION")
			Notification.Builder(this)
		}
				.setSmallIcon(R.drawable.ic_update_24dp)
				.setContentTitle(getString(R.string.app_name))
				.setContentText("Updating Repository")
				.setOnlyAlertOnce(true)
	}

	private var job: Job? = null

	override fun onDestroy() {
		job?.cancel()
		super.onDestroy()
	}

	override fun onCreate() {
		startForeground(ID_CHAPTER_DOWNLOAD, progressNotification.build())
		super.onCreate()
	}

	override fun onBind(intent: Intent?): IBinder? {
		return null
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		Log.d(logID(), SERVICE_CANCEL_PREVIOUS)
		job?.cancel()
		Log.d(logID(), SERVICE_NEW)
		job = Job(this)
		Log.d(logID(), SERVICE_EXECUTE)
		job?.let { Needle.onBackgroundThread().execute(it) } ?: Log.e(logID(), SERVICE_NULLIFIED)
		return super.onStartCommand(intent, flags, startId)
	}

	internal class Job(private val service: RepositoryService) : CancelableTask() {

		private fun sendMessage(action: String, data: Map<String, String?> = mapOf()) {
			val i = Intent()
			i.action = action

			for ((key, value) in data)
				i.putExtra(key, value)

			service.sendBroadcast(i)
		}

		fun progressUpdate(string: String) {

		}

		override fun doWork() {
			task(service) { progressUpdate(it) }
		}
	}
}