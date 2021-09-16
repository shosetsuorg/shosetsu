package app.shosetsu.android.backend.workers.onetime

import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.Notifications
import app.shosetsu.android.common.consts.Notifications.ID_EXPORT
import app.shosetsu.android.common.consts.WorkerTags.EXPORT_BACKUP_WORK_ID
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.repository.base.IBackupUriRepository
import app.shosetsu.common.domain.model.local.BackupEntity
import app.shosetsu.common.domain.repositories.base.IBackupRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.errorResult
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.R
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import java.io.FileOutputStream
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
 * 21 / 01 / 2021
 */
class ExportBackupWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(
	appContext,
	params
), DIAware, NotificationCapable {
	override val di: DI by closestDI(applicationContext)

	private val backupRepo by instance<IBackupRepository>()
	private val backupUriRepo by instance<IBackupUriRepository>()

	override val baseNotificationBuilder: NotificationCompat.Builder
		get() = notificationBuilder(applicationContext, Notifications.CHANNEL_BACKUP)
			.setSubText(getString(R.string.export_backup_notification_subtext))
			.setSmallIcon(R.drawable.restore)
			.setOnlyAlertOnce(true)
			.setOngoing(true)

	override val notificationManager: NotificationManagerCompat by notificationManager()
	override val notifyContext: Context = appContext
	override val defaultNotificationID: Int = ID_EXPORT

	/**
	 * Loads a backup via the [Uri] provided by Androids file selection
	 */
	private fun writeToUri(uri: Uri, backupEntity: BackupEntity): HResult<*> {
		val contentResolver = applicationContext.contentResolver ?: return errorResult(
			NullPointerException("Null contentResolver")
		)
		contentResolver.openFileDescriptor(uri, "w")?.use { descriptor ->
			FileOutputStream(descriptor.fileDescriptor).use {
				it.write(backupEntity.content)
			}
		} ?: return errorResult(NullPointerException("Did not have write permissions"))

		return successResult()
	}

	@Throws(IOException::class)
	override suspend fun doWork(): Result {
		logI("Starting restore")
		val backupName = inputData.getString(KEY_EXPORT_NAME)

		if (backupName == null) {
			logE("null backupName, Export Backup requires backupName")
			return Result.failure()
		}

		notify(R.string.export_backup_notification_content_starting)
		backupUriRepo.take().handle { uri ->
			backupRepo.loadBackup(backupName).handle {
				writeToUri(uri, it)
			}
		}

		notify(R.string.restore_notification_content_completed) {
			setNotOngoing()
		}
		logI("Completed restore")
		return Result.success()
	}

	/**
	 * Manager of [BackupWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {

		override val count: Int
			get() = workerManager.getWorkInfosForUniqueWork(EXPORT_BACKUP_WORK_ID).get().size

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override fun isRunning(): Boolean = try {
			// Is this running
			val a = (getWorkerState() == WorkInfo.State.RUNNING)

			// Don't run if update is being installed
			val b = !AppUpdateInstallWorker.Manager(context).isRunning()
			a && b
		} catch (e: Exception) {
			false
		}

		override fun getWorkerState(index: Int): WorkInfo.State =
			workerManager.getWorkInfosForUniqueWork(EXPORT_BACKUP_WORK_ID).get()[index].state

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 */
		override fun start(data: Data) {
			launchIO {
				logI(LogConstants.SERVICE_NEW)
				workerManager.enqueueUniqueWork(
					EXPORT_BACKUP_WORK_ID,
					ExistingWorkPolicy.REPLACE,
					OneTimeWorkRequestBuilder<ExportBackupWorker>(
					).setInputData(data).build()
				)
				logI(
					"Worker State ${
						workerManager.getWorkInfosForUniqueWork(EXPORT_BACKUP_WORK_ID)
							.await()[0].state
					}"
				)
			}
		}

		/**
		 * Stops the service.
		 */
		override fun stop(): Operation =
			workerManager.cancelUniqueWork(EXPORT_BACKUP_WORK_ID)
	}

	companion object {

		const val KEY_EXPORT_NAME = "BACKUP_NAME"
	}
}