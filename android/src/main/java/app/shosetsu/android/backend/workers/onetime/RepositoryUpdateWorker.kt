package app.shosetsu.android.backend.workers.onetime

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import app.shosetsu.android.backend.receivers.NotificationBroadcastReceiver
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.consts.ACTION_UPDATE_EXTENSION
import app.shosetsu.android.common.consts.EXTRA_UPDATE_EXTENSION_ID
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.Notifications.CHANNEL_REPOSITORY_UPDATE
import app.shosetsu.android.common.consts.Notifications.ID_REPOSITORY_UPDATE
import app.shosetsu.android.common.consts.WorkerTags.REPOSITORY_UPDATE_TAG
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.usecases.RemoveExtensionEntityUseCase
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.model.local.ExtLibEntity
import app.shosetsu.common.domain.model.local.GenericExtensionEntity
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.android.domain.repository.base.IExtensionLibrariesRepository
import app.shosetsu.android.domain.repository.base.IExtensionRepoRepository
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.lib.Version
import app.shosetsu.lib.json.RepoExtension
import app.shosetsu.lib.json.RepoLibrary
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.delay
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

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
 * 18 / 05 / 2021
 *
 * This worker handles updating the repositories for shosetsu
 */
class RepositoryUpdateWorker(
	appContext: Context,
	params: WorkerParameters,
) : CoroutineWorker(appContext, params), DIAware, NotificationCapable {

	private val extRepo: IExtensionsRepository by instance()
	private val removeExtension: RemoveExtensionEntityUseCase by instance()
	private val extRepoRepo: IExtensionRepoRepository by instance()
	private val extensionLibrariesRepo: IExtensionLibrariesRepository by instance()

	private val iSettingsRepository by instance<ISettingsRepository>()

	private suspend fun disableOnFail(): Boolean =
		iSettingsRepository.getBoolean(SettingKey.RepoUpdateDisableOnFail)

	/**
	 * Updates the libraries in the program
	 *
	 * @param repoExtLibList of the application
	 * @param repository Repo of the index
	 */
	private suspend fun updateLibraries(
		repoExtLibList: List<RepoLibrary>,
		repository: RepositoryEntity,
	) {
		val databaseLibs = try {
			extensionLibrariesRepo.loadExtLibByRepo(repository.id)
		} catch (e: Exception) {
			with(e) {
				notify("Failed to load ext libs of repo: : $message")
				logE("Failed to load ext libs of repo: $message", e)
			}
			return
		}

		/** Libraries not installed or needs update */
		val libsNotPresent = ArrayList<ExtLibEntity>()

		// Loops through the libraries from the remote repository
		val repoExtLibListSize = repoExtLibList.size

		repoExtLibList.forEachIndexed { index, (repoLibName, repoLibVersion) ->
			notify("Checking $repoLibName from ${repository.name}") {
				setSilent(true)
				setProgress(repoExtLibListSize, index + 1, false)
			}

			val isInstalled = databaseLibs.any { it.scriptName == repoLibName }

			var install = false
			var extensionLibraryEntity: ExtLibEntity? = null
			var repoVersion = Version(0, 0, 0)

			if (isInstalled) {
				//  Checks if an update need
				repoVersion = repoLibVersion
				extensionLibraryEntity =
					databaseLibs.find { it.scriptName == repoLibName }!!

				// If the version compared to the repo version is different, reinstall
				if (repoVersion > extensionLibraryEntity.version) {
					logI("$repoLibName has update $repoVersion available, updating")
					install = true
				} else {
					extRepoRepo.getRepo(repository.id)?.let { repo ->
						if (!repo.isEnabled) {
							logI("${repo.name} is disabled, downgrading $repoLibName")
							install = true
						}
					}
				}
			} else {
				install = true
			}

			// If install is true, then it adds it to the list for later
			if (install)
				libsNotPresent.add(
					extensionLibraryEntity ?: ExtLibEntity(
						scriptName = repoLibName,
						version = repoVersion,
						repoID = repository.id
					)
				)
		}
		notify("Finished checking extensions from ${repository.name}") {
			setSilent(true)
			removeProgress()
		}

		// For each library not present, installs
		libsNotPresent.forEach {
			notify("Installing ${it.scriptName} from ${repository.name}") {
				setSilent(true)
				setProgress(1, 0, true)
			}
			extensionLibrariesRepo.installExtLibrary(repository.url, it)
		}

		notify("Completed extension library update for ${repository.name}") {
			setSilent(true)
			removeProgress()
		}
	}

	private suspend inline fun handlePresentExtensions(
		list: List<GenericExtensionEntity>,
		presentExtensions: List<Int>
	) {
		list.filterNot { presentExtensions.contains(it.id) }.forEach {
			// for each extension that is not present in the present in the repository
			if (extRepo.isExtensionInstalled(it)) {
				extRepo.updateRepositoryExtension(
					it.copy(
						version = Version(-9, -9, -9)
					)
				)
			} else {
				logI("Removing Extension: $it")
				removeExtension(it)
			}
		}
	}

	/**
	 * TODO move to another worker / stage of this worker
	 */
	private fun NotificationCompat.Builder.addUpdate(extensionId: Int) {
		val intent = Intent(
			applicationContext,
			NotificationBroadcastReceiver::class.java
		).apply {
			action = ACTION_UPDATE_EXTENSION
			putExtra(EXTRA_UPDATE_EXTENSION_ID, extensionId)
		}
		addAction(
			R.drawable.update,
			getString(R.string.update),
			PendingIntent.getBroadcast(
				applicationContext,
				0,
				intent,
				if (SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
			)
		)
	}

	/**
	 * Handle updating an extension
	 */
	private suspend fun updateExtension(repo: RepositoryEntity, repoExt: RepoExtension) {
		val extensionEntity = extRepo.getExtension(repo.id, repoExt.id)
		if (extensionEntity == null) {
			extRepo.insert(
				GenericExtensionEntity(
					id = repoExt.id,
					repoID = repo.id,
					name = repoExt.name,
					fileName = repoExt.fileName,
					imageURL = repoExt.imageURL,
					lang = repoExt.lang,
					version = repoExt.version,
					md5 = repoExt.md5,
					type = repoExt.type
				).also {
					logI("Inserting new extension, $it")
				}
			)
		} else {
			extRepo.updateRepositoryExtension(
				extensionEntity.copy(
					name = repoExt.name,
					fileName = repoExt.fileName,
					imageURL = repoExt.imageURL,
					lang = repoExt.lang,
					version = repoExt.version,
					md5 = repoExt.md5,
					type = repoExt.type
				)
			)
		}
	}

	/**
	 * Updates database with [repoList]
	 *
	 * @return list of extension ids that are present in this repository
	 */
	private suspend fun updateExtensions(
		repoList: List<RepoExtension>,
		repo: RepositoryEntity
	): List<Int> {
		val presentExtensions = ArrayList<Int>() // Extensions from repo

		repoList.forEach { repoExtension ->
			updateExtension(repo, repoExtension)
			presentExtensions.add(repoExtension.id)
		}

		// Loop through extensions from the repository, remove obsolete or warn about obsolete
		handlePresentExtensions(extRepo.getRepositoryExtensions(repo.id), presentExtensions)

		return presentExtensions
	}

	override suspend fun doWork(): Result {
		logI("Starting Update")
		notify("Starting Repository Update") { setOngoing() }
		extRepoRepo.loadEnabledRepos().let { repos: List<RepositoryEntity> ->
			var presentExtensions = ArrayList<Int>()
			for (repo in repos) {
				logI("Updating $repo")
				// gets the latest list for the repo
				val repoIndex = try {
					extRepoRepo.getRepoData(repo)
				} catch (e: Exception) {//TODO specify
					notify(
						": ${e.message}",
						notificationId = ID_REPOSITORY_UPDATE + 1 + repo.id
					) {
						removeProgress()
						setContentTitle("${repo.name} failed to load")
						setNotOngoing()
						addReportErrorAction(
							applicationContext,
							ID_REPOSITORY_UPDATE + 1 + repo.id,
							e
						)
					}
					logE(
						"${repo.name} failed to load : ${e.message}",
						e
					)
					if (disableOnFail()) {
						logI("Disabling repository: $repo")
						extRepoRepo.update(repo.copy(isEnabled = false))
					}
					return@let
				}

				updateLibraries(repoIndex.libraries, repo)

				val result = updateExtensions(repoIndex.extensions, repo)
				presentExtensions.addAll(result)
				presentExtensions = ArrayList(presentExtensions.distinct())
			}

			handlePresentExtensions(extRepo.loadRepositoryExtensions(), presentExtensions)
		}
		notify("Completed") { setNotOngoing() }
		delay(1000)
		notificationManager.cancel(defaultNotificationID)
		logI("Completed Repository Update")
		return Result.success()
	}

	override val di: DI by closestDI(appContext)

	override val baseNotificationBuilder: NotificationCompat.Builder
		get() = notificationBuilder(applicationContext, CHANNEL_REPOSITORY_UPDATE)
			.setSmallIcon(R.drawable.download)
			.setContentTitle("Repository Update")
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setOngoing(true)

	override val notificationManager: NotificationManagerCompat by notificationManager()

	override val notifyContext: Context
		get() = applicationContext

	override val defaultNotificationID: Int
		get() = ID_REPOSITORY_UPDATE

	class Manager(context: Context) : CoroutineWorkerManager(context) {
		private val iSettingsRepository by instance<ISettingsRepository>()

		private suspend fun updateOnMetered(): Boolean =
			iSettingsRepository.getBoolean(SettingKey.RepoUpdateOnMeteredConnection)

		private suspend fun updateOnLowStorage(): Boolean =
			iSettingsRepository.getBoolean(SettingKey.RepoUpdateOnLowStorage)

		private suspend fun updateOnLowBattery(): Boolean =
			iSettingsRepository.getBoolean(SettingKey.RepoUpdateOnLowBattery)

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override suspend fun isRunning(): Boolean = try {
			getWorkerState() == WorkInfo.State.RUNNING
		} catch (e: Exception) {
			false
		}

		override suspend fun getWorkerState(index: Int): WorkInfo.State =
			getWorkerInfoList()[index].state

		override suspend fun getWorkerInfoList(): List<WorkInfo> =
			workerManager.getWorkInfosForUniqueWork(REPOSITORY_UPDATE_TAG).await()

		override suspend fun getCount(): Int =
			getWorkerInfoList().size

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 */
		override fun start(data: Data) {
			launchIO {
				logI(LogConstants.SERVICE_NEW)
				workerManager.enqueueUniqueWork(
					REPOSITORY_UPDATE_TAG,
					ExistingWorkPolicy.REPLACE,
					OneTimeWorkRequestBuilder<RepositoryUpdateWorker>().setInputData(data)
						.setConstraints(
							Constraints.Builder().apply {
								setRequiredNetworkType(
									if (updateOnMetered()) {
										NetworkType.CONNECTED
									} else NetworkType.UNMETERED
								)
								setRequiresStorageNotLow(!updateOnLowStorage())
								setRequiresBatteryNotLow(!updateOnLowBattery())
							}.build()
						).build()
				)
				logI(
					"Worker State ${
						workerManager.getWorkInfosForUniqueWork(REPOSITORY_UPDATE_TAG)
							.await()[0].state
					}"
				)
			}
		}

		/**
		 * Stops the service.
		 */
		override fun stop(): Operation =
			workerManager.cancelUniqueWork(REPOSITORY_UPDATE_TAG)
	}

}