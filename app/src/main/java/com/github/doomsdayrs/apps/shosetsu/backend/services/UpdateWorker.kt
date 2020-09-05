package com.github.doomsdayrs.apps.shosetsu.backend.services

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.core.content.getSystemService
import androidx.work.*
import androidx.work.ExistingPeriodicWorkPolicy.REPLACE
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.common.consts.LogConstants
import com.github.doomsdayrs.apps.shosetsu.common.consts.Notifications.CHANNEL_UPDATE
import com.github.doomsdayrs.apps.shosetsu.common.consts.Notifications.ID_CHAPTER_UPDATE
import com.github.doomsdayrs.apps.shosetsu.common.consts.WorkerTags.UPDATE_WORK_ID
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.INovelsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.LoadNovelUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.toast.ToastErrorUseCase
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.util.concurrent.TimeUnit.HOURS
import androidx.work.PeriodicWorkRequestBuilder as PWRB

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
 * shosetsu
 * 07 / 02 / 2020
 *
 * <p>
 *     Handles update requests for the entire application
 * </p>
 */
class UpdateWorker(
		appContext: Context,
		params: WorkerParameters,
) : CoroutineWorker(appContext, params), KodeinAware {
	companion object {
		const val KEY_TARGET = "Target"
		const val KEY_CHAPTERS = "Novels"

		const val KEY_NOVELS = 0x00
		const val KEY_CATEGORY = 0x01
	}

	class UpdateWorkerManager(override val kodein: Kodein) : KodeinAware {
		val settings: ShosetsuSettings by instance()

		/**
		 * Returns the status of the service.
		 *
		 * @param context the application context.
		 * @return true if the service is running, false otherwise.
		 */
		private fun isRunning(
				context: Context,
				workerManager: WorkManager = WorkManager.getInstance(context),
		): Boolean = try {
			workerManager.getWorkInfosForUniqueWork(UPDATE_WORK_ID)
					.get()[0].state == WorkInfo.State.RUNNING
		} catch (e: Exception) {
			false
		}

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 *
		 * @param context the application context.
		 */
		fun start(
				context: Context,
				workerManager: WorkManager = WorkManager.getInstance(context),
		) {
			Log.i(logID(), LogConstants.SERVICE_NEW)
			workerManager.enqueueUniquePeriodicWork(
					UPDATE_WORK_ID,
					REPLACE,
					PWRB<UpdateWorker>(
							settings.updateCycle.toLong(),
							HOURS
					).setConstraints(
							Constraints.Builder().apply {
								setRequiredNetworkType(
										if (settings.updateOnMetered) {
											CONNECTED
										} else UNMETERED
								)
								setRequiresStorageNotLow(!settings.updateOnLowStorage)
								setRequiresBatteryNotLow(!settings.updateOnLowBattery)
								if (SDK_INT >= VERSION_CODES.M)
									setRequiresDeviceIdle(settings.updateOnlyIdle)
							}.build()
					)
							.build()
			)
			workerManager.getWorkInfosForUniqueWork(UPDATE_WORK_ID).get()[0].let {
				Log.d(logID(), "State ${it.state}")
			}
		}

		/**
		 * Stops the service.
		 *
		 * @param context the application context.
		 */
		fun stop(
				context: Context,
				workerManager: WorkManager = WorkManager.getInstance(context),
		): Any = workerManager.cancelUniqueWork(UPDATE_WORK_ID)
	}

	private val notificationManager by lazy { appContext.getSystemService<NotificationManager>()!! }

	private val progressNotification by lazy {
		if (SDK_INT >= VERSION_CODES.O) {
			Notification.Builder(appContext, CHANNEL_UPDATE)
		} else {
			// Suppressed due to lower API
			@Suppress("DEPRECATION")
			Notification.Builder(appContext)
		}
				.setSmallIcon(R.drawable.ic_system_update_alt_24dp)
				.setContentText("Update in progress")
				.setOnlyAlertOnce(true)
	}

	override val kodein: Kodein by closestKodein(appContext)
	private val iNovelsRepository by instance<INovelsRepository>()
	private val loadNovelUseCase by instance<LoadNovelUseCase>()
	private val settings by instance<ShosetsuSettings>()
	private val toastErrorUseCase by instance<ToastErrorUseCase>()

	override suspend fun doWork(): Result {
		Log.i(logID(), LogConstants.SERVICE_EXECUTE)
		val pr = progressNotification
		pr.setContentTitle(applicationContext.getString(R.string.update))
		pr.setOngoing(true)
		notificationManager.notify(ID_CHAPTER_UPDATE, pr.build())

		iNovelsRepository.getBookmarkedNovels().let { hNovels ->
			when (hNovels) {
				is HResult.Success -> {
					val novels = hNovels.data.let { list: List<NovelEntity> ->
						if (settings.onlyUpdateOngoing)
							list.filter { it.status == Novel.Status.PUBLISHING }
						else list
					}
					var progress = 0
					novels.forEach {
						pr.setContentText(applicationContext.getString(R.string.updating) + it.title)
						pr.setProgress(novels.size, progress, false)
						notificationManager.notify(ID_CHAPTER_UPDATE, pr.build())
						loadNovelUseCase(it, true).let { lR ->
							when (lR) {
								is HResult.Success -> Log.d(logID(), "Updated $lR")
								is HResult.Error -> toastErrorUseCase<UpdateWorker>(lR)
								else -> Log.e(logID(), "Impossible result")
							}
						}
						progress++
					}
					pr.setContentTitle(applicationContext.getString(R.string.update))
					pr.setContentText(applicationContext.getString(R.string.update_complete))
					pr.setOngoing(false)
					pr.setProgress(0, 0, false)
					notificationManager.notify(ID_CHAPTER_UPDATE, pr.build())

				}
				else -> {
					return Result.failure()
				}
			}
		}
		return Result.success()
	}
}