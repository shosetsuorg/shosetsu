package app.shosetsu.android.backend.workers.onetime

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.work.*
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.consts.APK_MIME
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.Notifications.CHANNEL_APP_UPDATE
import app.shosetsu.android.common.consts.Notifications.ID_APP_UPDATE_INSTALL
import app.shosetsu.android.common.consts.WorkerTags.APP_UPDATE_INSTALL_WORK_ID
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.common.domain.repositories.base.IAppUpdatesRepository
import app.shosetsu.common.dto.handle
import com.github.doomsdayrs.apps.shosetsu.R
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import java.io.File

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
 * shosetsu
 * 20 / 12 / 2020
 */
class AppUpdateInstallWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(
	appContext,
	params
), DIAware, NotificationCapable {
	override val di: DI by closestDI(appContext)
	private val updateRepo by instance<IAppUpdatesRepository>()
	override val notificationManager: NotificationManagerCompat by notificationManager()

	override val notifyContext: Context
		get() = applicationContext


	override val defaultNotificationID: Int = ID_APP_UPDATE_INSTALL

	private val reportExceptionUseCase by instance<ReportExceptionUseCase>()

	override val baseNotificationBuilder: NotificationCompat.Builder
		get() = notificationBuilder(applicationContext, CHANNEL_APP_UPDATE)
			.setSubText(applicationContext.getString(R.string.notification_app_update_install_title))
			.setSmallIcon(R.drawable.app_update)
			.setProgress(0, 0, true)


	override suspend fun doWork(): Result {
		notify(R.string.notification_app_update_loading) {
			setOngoing()
		}

		// Load up the app update from repo
		updateRepo.loadAppUpdate().handle(
			onError = {
				notify("Exception occurred\n ${it.message}") {
					setNotOngoing()
					removeProgress()
				}
				reportExceptionUseCase(it)
				return Result.failure()
			},
			onEmpty = {
				notify("Empty result, Recieved empty return, Was there even an update?") {
					setNotOngoing()
					removeProgress()
				}
				return Result.failure()
			}
		) { update ->

			notify(R.string.notification_app_update_downloading)

			// download the app update and get the path to the installed file
			updateRepo.downloadAppUpdate(update).handle(
				onError = {
					reportExceptionUseCase(it)

					notify("Exception occurred \n ${it.message} ") {
						setOngoing(false)
						setProgress(0, 0, false)
					}

					return Result.failure()
				},
				onEmpty = {

					notify("Empty result, Received empty return, Did the download fail?") {
						setNotOngoing()
						removeProgress()
					}
					return Result.failure()
				}
			) { path ->
				val uri = File(path).getUriCompat(applicationContext)
				notify(R.string.notification_app_update_install) {
					setNotOngoing()
					removeProgress()
					addAction(
						actionBuilder(
							IconCompat.createWithResource(
								applicationContext,
								R.drawable.app_update
							),
							applicationContext.getString(R.string.install),
							installApkPendingActivity(applicationContext, uri)
						).build()
					)
				}

			}
		}
		return Result.success()
	}

	/**
	 * Returns [PendingIntent] that prompts user with apk install intent
	 *
	 * @param context context
	 * @param uri uri of apk that is installed
	 */
	private fun installApkPendingActivity(context: Context, uri: Uri): PendingIntent {
		val intent = Intent(Intent.ACTION_VIEW).apply {
			setDataAndType(uri, APK_MIME)
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
		}
		return PendingIntent.getActivity(context, 0, intent, 0)
	}

	class Manager(context: Context) : CoroutineWorkerManager(context) {
		override fun isRunning(): Boolean = try {
			getWorkerState() == WorkInfo.State.RUNNING
		} catch (e: Exception) {
			false
		}

		override fun getWorkerState(index: Int): WorkInfo.State =
			workerManager.getWorkInfosForUniqueWork(APP_UPDATE_INSTALL_WORK_ID).get()[index].state

		override val count: Int
			get() = workerManager.getWorkInfosForUniqueWork(APP_UPDATE_INSTALL_WORK_ID).get().size

		override fun start(data: Data) {
			launchIO {
				logI(LogConstants.SERVICE_NEW)
				workerManager.enqueueUniqueWork(
					APP_UPDATE_INSTALL_WORK_ID,
					ExistingWorkPolicy.KEEP,
					OneTimeWorkRequestBuilder<AppUpdateInstallWorker>().build()
				)
				logI(
					"Worker State ${
						workerManager.getWorkInfosForUniqueWork(APP_UPDATE_INSTALL_WORK_ID)
							.await()[0].state
					}"
				)
			}
		}

		override fun stop(): Operation =
			workerManager.cancelUniqueWork(APP_UPDATE_INSTALL_WORK_ID)

	}
}