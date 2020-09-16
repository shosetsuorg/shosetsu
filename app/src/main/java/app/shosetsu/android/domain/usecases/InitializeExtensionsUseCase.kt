package app.shosetsu.android.domain.usecases

import android.util.Log
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.HResult.Success
import app.shosetsu.android.common.ext.containsName
import app.shosetsu.android.common.ext.forEachTyped
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.common.utils.FormatterUtils
import app.shosetsu.android.domain.model.local.ExtLibEntity
import app.shosetsu.android.domain.model.local.ExtensionEntity
import app.shosetsu.android.domain.model.local.RepositoryEntity
import app.shosetsu.android.domain.repository.base.IExtLibRepository
import app.shosetsu.android.domain.repository.base.IExtRepoRepository
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

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
	suspend fun invoke(progressUpdate: (String) -> Unit) {
		Log.i(logID(), "Starting Update")
		if (isOnlineUseCase()) {
			progressUpdate("Online, Loading repositories")
			val repos: HResult<List<RepositoryEntity>> = extRepoRepo.loadRepositories()
			if (repos is Success)
				for (repo in repos.data) {
					val repoName = repo.name

					progressUpdate("Checking $repoName")
					// gets the latest list for the repo
					extRepoRepo.loadRepoDataJSON(repo)
							.takeIf { it is Success }
							?.let { (it as Success).data }
							?.let { indexJSON ->
								updateLibraries(indexJSON, repo, progressUpdate)
								updateScript(indexJSON, repo)
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
	 * @param indexJSON of the application
	 * @param repo Repo of the index
	 * @param progressUpdate Upstream reporting
	 */
	private suspend fun updateLibraries(
			indexJSON: JSONObject,
			repo: RepositoryEntity,
			progressUpdate: (String) -> Unit,
	) {
		// Updates libraries

		// Array of libraries
		val libJSONArray: JSONArray
		try {
			libJSONArray = indexJSON
					.getJSONArray("libraries")
		} catch (e: JSONException) {
			Log.e(logID(), "Did not find libraries array", e)
			return
		}

		// Libraries in database
		extLibRepo.loadExtLibByRepo(repo)
				.takeIf { it is Success }?.let { (it as Success).data }
				?.let { libEntities ->
					// Libraries not installed or needs update
					val libsNotPresent = ArrayList<ExtLibEntity>()

					// Loops through the json array of libraries
					for (index in 0 until libJSONArray.length()) {
						(libJSONArray[index] as JSONObject).let letFunction@{
							val name: String
							try {
								name = it.getString("name")
							} catch (e: JSONException) {
								Log.e(logID(), "No name found", e)
								return@letFunction
							}
							val position = libEntities.containsName(name)

							var install = false
							var extensionLibraryEntity: ExtLibEntity? = null
							var version = ""

							if (position != -1) {
								//  Checks if an update need
								try {
									version = it.getString("version")
								} catch (e: JSONException) {
									Log.e(logID(), "Error ", e)
									return@letFunction
								}
								extensionLibraryEntity = libEntities[position]
								if (FormatterUtils.compareVersions(
												version,
												extensionLibraryEntity.version
										))
									install = true
							} else {
								install = true
							}

							// If install is true, then it adds it to the notPresent
							if (install)
								libsNotPresent.add(
										extensionLibraryEntity ?: ExtLibEntity(
												scriptName = name,
												version = version,
												repoID = repo.id!!
										)
								)

						}
					}

					// For each library not present, installs
					libsNotPresent.forEach {
						progressUpdate("Updating/Installing ${it.scriptName}")
						extLibRepo.installExtLibrary(repo, it)
					}
				}
	}

	private suspend fun updateScript(indexJSON: JSONObject, repo: RepositoryEntity) {
		// Updates Script Info
		val scriptsArray: JSONArray
		try {
			scriptsArray = indexJSON.getJSONArray("scripts")
		} catch (e: JSONException) {
			Log.e(logID(), "JSON error", e)
			return
		}
		val presentExtensions = ArrayList<Int>() // Extensions from repo
		scriptsArray.forEachTyped { script: JSONObject ->
			val formatterID: Int
			val formatterName: String
			val fileName: String
			val imageURL: String
			val lang: String
			val version: String
			val md5: String

			try {
				formatterID = script.getInt("id")
			} catch (e: JSONException) {
				Log.e(logID(), "Error getting id", e)
				return@forEachTyped
			}
			presentExtensions.add(formatterID)
			try {
				formatterName = script.getString("name")
			} catch (e: JSONException) {
				Log.e(logID(), "Error getting name", e)
				return@forEachTyped
			}
			try {
				fileName = script.getString("fileName")
			} catch (e: JSONException) {
				Log.e(logID(), "Error getting fileName", e)
				return@forEachTyped
			}
			try {
				imageURL = script.getString("imageURL")
			} catch (e: JSONException) {
				Log.e(logID(), "Error getting imageURL", e)
				return@forEachTyped
			}
			try {
				lang = script.getString("lang")
			} catch (e: JSONException) {
				Log.e(logID(), "Error getting lang", e)
				return@forEachTyped
			}
			try {
				version = script.getString("version")
			} catch (e: JSONException) {
				Log.e(logID(), "Error getting version", e)
				return@forEachTyped
			}
			try {
				md5 = script.getString("md5")
			} catch (e: JSONException) {
				Log.e(logID(), "Error getting md5", e)
				return@forEachTyped
			}
			extRepo.insertOrUpdate(ExtensionEntity(
					id = formatterID,
					repoID = repo.id!!,
					name = formatterName,
					fileName = fileName,
					imageURL = imageURL,
					lang = lang,
					repositoryVersion = version,
					md5 = md5
			))
		}
		extRepo.getExtensions(repo.id!!).let { r ->
			if (r is Success) {
				r.data.filterNot { presentExtensions.contains(it.id) }.forEach {
					if (it.installed)
						extRepo.updateExtension(it.copy(
								repositoryVersion = "-9.-9.-9"
						))
					else extRepo.removeExtension(it)
				}
			}
		}
	}
}