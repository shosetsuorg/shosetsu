package app.shosetsu.android.backend.workers.onetime

import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.Notifications.CHANNEL_REPOSITORY_UPDATE
import app.shosetsu.android.common.consts.Notifications.ID_REPOSITORY_UPDATE
import app.shosetsu.android.common.consts.WorkerTags.REPOSITORY_UPDATE_TAG
import app.shosetsu.android.common.ext.*
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.model.local.ExtLibEntity
import app.shosetsu.common.domain.model.local.ExtensionEntity
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.domain.repositories.base.*
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform
import app.shosetsu.lib.Novel
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
	private val extRepoRepo: IExtensionRepoRepository by instance()
	private val extensionLibrariesRepo: IExtensionLibrariesRepository by instance()

	/**
	 * Updates the libraries in the program
	 *
	 * @param repoExtLibList of the application
	 * @param repository Repo of the index
	 */
	private suspend fun updateLibraries(
		repoExtLibList: List<RepoLibrary>,
		repository: RepositoryEntity,
	): HResult<*> =
		extensionLibrariesRepo.loadExtLibByRepo(repository.id!!).transform(
			onError = {
				with(it) {
					notify("Failed to load ext libs of repo: $code : $message")
					logE("Failed to load ext libs of repo: $code : $message", exception)
				}
				it
			}
		) { extLibs ->
			/** Libraries not installed or needs update */
			val libsNotPresent = ArrayList<ExtLibEntity>()

			// Loops through the libraries from the remote repository
			val repoExtLibListSize = repoExtLibList.size
			repoExtLibList.forEachIndexed { index, (repoExtLibName, repoExtLibVersion) ->
				notify("Checking $repoExtLibName from ${repository.name}") {
					setSilent(true)
					setProgress(repoExtLibListSize, index + 1, false)
				}
				/**
				 * -1 if the lib is not installed
				 */
				val position = extLibs.containsName(repoExtLibName)
				logV("$repoExtLibName:$position")
				var install = false
				var extensionLibraryEntity: ExtLibEntity? = null
				var version = Version(0, 0, 0)

				if (position != -1) {
					//  Checks if an update need
					version = repoExtLibVersion
					extensionLibraryEntity = extLibs[position]

					// If the version compared to the repo version is different, reinstall
					if (version != extensionLibraryEntity.version)
						install = true
				} else {
					install = true
				}

				// If install is true, then it adds it to the list for later
				if (install)
					libsNotPresent.add(
						extensionLibraryEntity ?: ExtLibEntity(
							scriptName = repoExtLibName,
							version = version,
							repoID = repository.id!!
						)
					)
			}
			notify("Finished checking extensions from ${repository.name}") {
				setNotificationSilent()
				removeProgress()
			}

			// For each library not present, installs
			libsNotPresent.forEach {
				notify("Installing ${it.scriptName} from ${repository.name}") {
					setNotificationSilent()
					setProgress(1, 0, true)
				}
				extensionLibrariesRepo.installExtLibrary(repository.url, it)
			}

			notify("Completed extension library update for ${repository.name}") {
				setNotificationSilent()
				removeProgress()
			}
			successResult(0)
		}

	private suspend fun updateExtensions(repoList: List<RepoExtension>, repo: RepositoryEntity) {
		val presentExtensions = ArrayList<Int>() // Extensions from repo
		repoList.forEach { (id, name, fileName, imageURL, lang, version, _, md5, type) ->
			extRepo.insertOrUpdate(
				ExtensionEntity(
					id = id,
					repoID = repo.id!!,
					name = name,
					fileName = fileName,
					imageURL = imageURL,
					lang = lang,
					repositoryVersion = version,
					chapterType = Novel.ChapterType.STRING,
					md5 = md5,
					type = type
				)
			).handle {
				// If an update is ava, notify the user on a separate channel
				if (it > 0) {
					notify("$version update available", id + 3000) {
						setContentTitle(name)
						removeProgress()
						setNotOngoing()
					}
				}
			}
			presentExtensions.add(id)
		}

		// Loop through extensions from the repository, remove obsolete or warn about obsolete
		extRepo.getExtensionEntities(repo.id!!).handle { r ->
			r.filterNot { presentExtensions.contains(it.id) }.forEach {
				if (it.installed)
					extRepo.updateExtensionEntity(
						it.copy(
							repositoryVersion = Version(-9, -9, -9)
						)
					)
				else {
					logI("Removing Extension: $it")
					extRepo.removeExtension(it)
				}
			}
		}
	}

	override suspend fun doWork(): Result {
		logI("Starting Update")
		notify("Starting Repository Update") { setOngoing() }
		extRepoRepo.loadEnabledRepos().handle(
			onError = {
				notify("Failed to get repos")
				return Result.failure()
			}
		) { repos: List<RepositoryEntity> ->
			for (repo in repos) {
				logI("Updating $repo")
				// gets the latest list for the repo
				extRepoRepo.getRepoData(repo).handle(
					onError = {
						notify(
							"${it.code} : ${it.message}",
							notificationId = ID_REPOSITORY_UPDATE + 1 + (repo.id ?: 0)
						) {
							removeProgress()
							setContentTitle("${repo.name} failed to load")
							setNotOngoing()
						}
						logE(
							"${repo.name} failed to load ${it.code} : ${it.message}",
							it.exception
						)
						extRepoRepo.update(repo.copy(isEnabled = false))
					},
					onEmpty = {
						logE("Received no data for $repo")
					}
				) { repoIndex ->
					updateLibraries(repoIndex.libraries, repo)
					updateExtensions(repoIndex.extensions, repo)
				}
			}
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
			iSettingsRepository.getBooleanOrDefault(SettingKey.RepoUpdateOnMeteredConnection)

		private suspend fun updateOnLowStorage(): Boolean =
			iSettingsRepository.getBooleanOrDefault(SettingKey.RepoUpdateOnLowStorage)

		private suspend fun updateOnLowBattery(): Boolean =
			iSettingsRepository.getBooleanOrDefault(SettingKey.RepoUpdateOnLowBattery)

		private suspend fun updateOnlyIdle(): Boolean =
			iSettingsRepository.getBooleanOrDefault(SettingKey.RepoUpdateOnlyWhenIdle)

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override fun isRunning(): Boolean = try {
			getWorkerState() == WorkInfo.State.RUNNING
		} catch (e: Exception) {
			false
		}

		override fun getWorkerState(index: Int): WorkInfo.State =
			workerManager.getWorkInfosForUniqueWork(REPOSITORY_UPDATE_TAG).get()[index].state

		override val count: Int
			get() = workerManager.getWorkInfosForUniqueWork(REPOSITORY_UPDATE_TAG).get().size

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
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
									setRequiresDeviceIdle(updateOnlyIdle())
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