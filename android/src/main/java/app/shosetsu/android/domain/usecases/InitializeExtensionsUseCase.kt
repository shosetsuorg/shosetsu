package app.shosetsu.android.domain.usecases

import android.util.Log
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.HResult.Error
import app.shosetsu.common.dto.HResult.Success
import app.shosetsu.common.dto.handle
import app.shosetsu.common.domain.model.local.ExtLibEntity
import app.shosetsu.common.domain.model.local.ExtensionEntity
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.domain.repositories.base.IExtLibRepository
import app.shosetsu.common.domain.repositories.base.IExtRepoRepository
import app.shosetsu.lib.Version
import app.shosetsu.lib.json.RepoExtension
import app.shosetsu.lib.json.RepoLibrary

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
 * 13 / 05 / 2020
 * <p>
 *     Initializes formatters, libraries, and repositories
 * </p>
 */
class InitializeExtensionsUseCase(
        private val extRepo: IExtensionsRepository,
        private val extRepoRepo: IExtRepoRepository,
        private val extLibRepo: IExtLibRepository,
        private var isOnlineUseCase: IsOnlineUseCase,
) {
    suspend operator fun invoke(progressUpdate: (String) -> Unit) {
        Log.i(logID(), "Starting Update")
        if (isOnlineUseCase()) {
            progressUpdate("Online, Loading repositories")
            val repos: HResult<List<RepositoryEntity>> = extRepoRepo.loadRepositories()
            if (repos is Success)
                for (repo in repos.data) {
                    val repoName = repo.name

                    progressUpdate("Checking $repoName")
                    // gets the latest list for the repo
                    val result = extRepoRepo.loadRepoData(repo)
                    result.takeIf { it is Success }
                            ?.let { (it as Success).data }
                            ?.let { repoIndex ->
                                updateLibraries(repoIndex.libraries, repo, progressUpdate)
                                updateScript(repoIndex.extensions, repo)
                            }
                            ?: result.takeIf { it is Error }
                                    ?.let { (it as Error) }
                                    ?.let {
                                        logE(
                                                "Exception! ${it.code} : ${it.message}",
                                                it.error
                                        )
                                    }

                }
            else progressUpdate("Failed to get repos")
        } else {
            progressUpdate("Application is offline, Not updating")
        }
        Log.i(logID(), "Completed Update")
    }

    /**
     * Updates the libraries in the program
     *
     * @param repoList of the application
     * @param repo Repo of the index
     * @param progressUpdate Upstream reporting
     */
    private suspend fun updateLibraries(
            repoList: List<RepoLibrary>,
            repo: RepositoryEntity,
            progressUpdate: (String) -> Unit,
    ) {
        // Libraries in database
        val repoResult = extLibRepo.loadExtLibByRepo(repo)
        repoResult.takeIf { it is Success }?.let { (it as Success).data }
                ?.let { libEntities ->
                    // Libraries not installed or needs update
                    val libsNotPresent = ArrayList<ExtLibEntity>()

                    // Loops through the json array of libraries
                    for ((repoName, repoVersion) in repoList) {
                        val position = libEntities.containsName(repoName)
                        logV("$repoName:$position")
                        var install = false
                        var extensionLibraryEntity: ExtLibEntity? = null
                        var version = Version(0, 0, 0)

                        if (position != -1) {
                            //  Checks if an update need
                            version = repoVersion
                            extensionLibraryEntity = libEntities[position]
                            if (version != extensionLibraryEntity.version)
                                install = true
                        } else {
                            install = true
                        }

                        // If install is true, then it adds it to the notPresent
                        if (install)
                            libsNotPresent.add(
                                    extensionLibraryEntity ?: ExtLibEntity(
                                            scriptName = repoName,
                                            version = version,
                                            repoID = repo.id
                                    )
                            )

                    }

                    // For each library not present, installs
                    libsNotPresent.forEach {
                        progressUpdate("Updating/Installing ${it.scriptName}")
                        extLibRepo.installExtLibrary(repo, it)
                    }
                }
                ?: repoResult.takeIf { it is Error }?.let { it as Error }?.let { (code, message, error) ->
                    logE("Error with lib! $code : $message", error)
                }
    }

    private suspend fun updateScript(repoList: List<RepoExtension>, repo: RepositoryEntity) {
        val presentExtensions = ArrayList<Int>() // Extensions from repo
        repoList.forEach { (id, name, fileName, imageURL, lang, _, libVersion, md5) ->
            extRepo.insertOrUpdate(ExtensionEntity(
                    id = id,
                    repoID = repo.id,
                    name = name,
                    fileName = fileName,
                    imageURL = imageURL,
                    lang = lang,
                    repositoryVersion = libVersion,
                    md5 = md5
            ))
            presentExtensions.add(id)
        }
        extRepo.getExtensionEntities(repo.id).handle { r ->
            r.filterNot { presentExtensions.contains(it.id) }.forEach {
                if (it.installed)
                    extRepo.updateExtensionEntity(it.copy(
                            repositoryVersion = Version(-9, -9, -9)
                    ))
                else {
                    logI("Removing Extension: $it")
                    extRepo.removeExtension(it)
                }
            }
        }
    }
}