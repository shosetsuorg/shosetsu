package app.shosetsu.android.backend.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID
import androidx.core.app.NotificationManagerCompat
import app.shosetsu.android.backend.workers.onetime.DownloadWorker
import app.shosetsu.android.backend.workers.onetime.NovelUpdateWorker
import app.shosetsu.android.common.consts.ACTION_UPDATE_EXTENSION
import app.shosetsu.android.common.consts.EXTRA_UPDATE_EXTENSION_ID
import app.shosetsu.android.common.consts.EXTRA_UPDATE_REPO_ID
import app.shosetsu.android.common.ext.ACTION_REPORT_ERROR
import app.shosetsu.android.common.ext.EXTRA_EXCEPTION
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.usecases.RequestInstallExtensionUseCase
import org.acra.ACRA
import org.kodein.di.android.closestDI
import org.kodein.di.instance

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
 */

/**
 * Shosetsu
 *
 * @since 23 / 07 / 2021
 * @author Doomsdayrs
 */
class NotificationBroadcastReceiver : BroadcastReceiver() {

	override fun onReceive(context: Context?, intent: Intent?) {
		if (context == null) return
		if (intent == null) return
		val di by closestDI(context)
		val notificationManager by lazy { NotificationManagerCompat.from(context) }

		when (intent.action) {
			NovelUpdateWorker.ACTION_CANCEL_NOVEL_UPDATE -> {
				val manager by di.instance<NovelUpdateWorker.Manager>()
				manager.stop()
				notificationManager.cancel(
					intent.getIntExtra(
						EXTRA_NOTIFICATION_ID,
						-1
					)
				)
			}
			DownloadWorker.ACTION_CANCEL_CHAPTER_DOWNLOAD -> {
				val manager by di.instance<DownloadWorker.Manager>()
				manager.stop()
				notificationManager.cancel(
					intent.getIntExtra(
						EXTRA_NOTIFICATION_ID,
						-1
					)
				)
			}
			ACTION_UPDATE_EXTENSION -> {
				val manager by di.instance<RequestInstallExtensionUseCase>()
				val extensionId = intent.getIntExtra(
					EXTRA_UPDATE_EXTENSION_ID,
					-1
				)
				val repoId = intent.getIntExtra(
					EXTRA_UPDATE_REPO_ID,
					-1
				)
				if (extensionId == -1 || repoId == -1) return

				launchIO {
					notificationManager.cancel(extensionId + 3000)
					manager.invoke(extensionId, repoId)
				}
			}

			ACTION_REPORT_ERROR -> {
				ACRA.errorReporter
					.handleSilentException(intent.extras?.get(EXTRA_EXCEPTION) as? Throwable)
				notificationManager.cancel(
					intent.getIntExtra(
						EXTRA_NOTIFICATION_ID,
						-1
					)
				)
			}
		}
	}

}