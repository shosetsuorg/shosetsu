package app.shosetsu.android.backend.workers.onetime

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.work.*
import app.shosetsu.android.R
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.EmptyResponseBodyException
import app.shosetsu.android.common.FileNotFoundException
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.common.MissingFeatureException
import app.shosetsu.android.common.consts.APK_MIME
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.Notifications.CHANNEL_APP_UPDATE
import app.shosetsu.android.common.consts.Notifications.ID_APP_UPDATE_INSTALL
import app.shosetsu.android.common.consts.WorkerTags.APP_UPDATE_INSTALL_WORK_ID
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.repository.base.IAppUpdatesRepository
import app.shosetsu.lib.exceptions.HTTPException
import org.acra.ACRA
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import java.io.File
import java.io.IOException

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
		val update = try {
			updateRepo.loadAppUpdate()
		} catch (e: FileNotFoundException) {
			notify("Update file is missing\n ${e.message}") {
				setNotOngoing()
				removeProgress()
			}
			ACRA.errorReporter.handleSilentException(e)
			return Result.failure()
		} catch (e: Exception) { // TODO specific
			notify("Exception occurred\n ${e.message}") {
				setNotOngoing()
				removeProgress()
			}
			ACRA.errorReporter.handleException(e)
			return Result.failure()
		}


		notify(R.string.notification_app_update_downloading)

		// download the app update and get the path to the installed file
		val path = try {
			updateRepo.downloadAppUpdate(update)
		} catch (e: FilePermissionException) {
			notify("How does the app lack the ability to download its apk\n ${e.message} ") {
				setNotOngoing()
				removeProgress()
				addReportErrorAction(applicationContext, defaultNotificationID, e)
			}

			return Result.failure()
		} catch (e: FileNotFoundException) {
			notify("How does the app lack the ability to download its apk\n ${e.message} ") {
				setNotOngoing()
				removeProgress()
				addReportErrorAction(applicationContext, defaultNotificationID, e)
			}

			return Result.failure()
		} catch (e: MissingFeatureException) {
			notify("This version of the app cannot self update") {
				setNotOngoing()
				removeProgress()
			}
			return Result.failure()
		} catch (e: EmptyResponseBodyException) {
			notify("Failed to get update content from the internet") {
				setNotOngoing()
				removeProgress()
				addReportErrorAction(applicationContext, defaultNotificationID, e)
			}
			return Result.failure()
		} catch (e: IOException) {
			notify("IO exception occurred \n ${e.message} ") {
				setNotOngoing()
				removeProgress()
			}

			return Result.failure()
		} catch (e: HTTPException) {
			notify("Failed due to HTTP code :${e.code}") {
				setNotOngoing()
				removeProgress()
			}
			return Result.failure()
		} catch (e: Exception) {
			notify("Exception occurred \n ${e.message} ") {
				setNotOngoing()
				removeProgress()
				addReportErrorAction(applicationContext, defaultNotificationID, e)
			}

			return Result.failure()
		}

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
		return PendingIntent.getActivity(
			context,
			0,
			intent,
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
		)
	}

	class Manager(context: Context) : CoroutineWorkerManager(context) {
		override suspend fun isRunning(): Boolean = try {
			getWorkerState() == WorkInfo.State.RUNNING
		} catch (e: Exception) {
			false
		}

		override suspend fun getWorkerState(index: Int): WorkInfo.State =
			getWorkerInfoList()[index].state

		override suspend fun getWorkerInfoList(): List<WorkInfo> =
			workerManager.getWorkInfosForUniqueWork(APP_UPDATE_INSTALL_WORK_ID).await()

		override suspend fun getCount(): Int =
			getWorkerInfoList().size

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