package com.github.doomsdayrs.apps.shosetsu.backend.services

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.extensionsDao
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.repositoryDao
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.scriptLibDao
import com.github.doomsdayrs.apps.shosetsu.providers.database.entities.ExtensionEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.entities.ExtensionLibraryEntity
import com.github.doomsdayrs.apps.shosetsu.variables.ext.forEachTyped
import com.github.doomsdayrs.apps.shosetsu.variables.ext.isServiceRunning
import com.github.doomsdayrs.apps.shosetsu.variables.ext.logID
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Notifications.CHANNEL_DOWNLOAD
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Notifications.ID_CHAPTER_DOWNLOAD
import needle.CancelableTask
import needle.Needle
import okio.IOException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup

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
class RepositoryService : Service() {
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
			} else Log.d(logID(), "Can't start, is running")
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
			Log.i(logID(), "Starting Update")
			if (Utilities.isOnline) {
				progressUpdate("Online, Loading repositories")
				val repos = repositoryDao
						.loadRepositories()

				for (repo in repos) {
					val repoName = repo.name
					val repoURL = repo.url
					val repoID = repo.id

					progressUpdate("Checking $repoName")
					// gets the latest list for the repo
					val formattersJSON: JSONObject
					try {
						formattersJSON = JSONObject(Jsoup.connect(
								"$repoURL/src/main/resources/index.json"
						).get().body().text())
					} catch (e: IOException) {
						Log.e(logID(), "Network error", e)
						continue
					} catch (e: JSONException) {
						Log.e(logID(), "JSON error", e)
						continue
					}

					// Updates libraries
					run {
						// Array of libraries
						val libJSONArray: JSONArray
						try {
							libJSONArray = formattersJSON
									.getJSONArray("libraries")
						} catch (e: JSONException) {
							Log.e(logID(), "Did not find libraries array", e)
							return@run
						}

						// Libraries in database
						val libEntities = scriptLibDao
								.loadLibByRepoID(repoID)

						// Libraries not installed or needs update
						val libsNotPresent = ArrayList<ExtensionLibraryEntity>()

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
								val position = libEntities.containsName(
										name
								)
								var install = false
								var extensionLibraryEntity: ExtensionLibraryEntity? = null
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
											extensionLibraryEntity ?: ExtensionLibraryEntity(
													scriptName = name,
													version = version,
													repoID = repoID
											)
									)

							}
						}

						// For each library not present, installs
						libsNotPresent.forEach {
							progressUpdate("Updating/Installing ${it.scriptName}")
							scriptLibDao.insertOrUpdateScriptLib(it)
							FormatterUtils.downloadLibrary(it, context)
						}
					}

					// Updates Script Info
					run {
						val scriptsArray: JSONArray
						try {
							scriptsArray = formattersJSON.getJSONArray("scripts")
						} catch (e: JSONException) {
							Log.e(logID(), "JSON error", e)
							return@run
						}
						scriptsArray.forEachTyped<JSONObject> { script ->
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

							if (extensionsDao.doesFormatterExist(formatterID)) {
								val formatterEntity = extensionsDao
										.loadFormatter(formatterID)
								formatterEntity.name = formatterName
								formatterEntity.imageURL = imageURL
								formatterEntity.md5 = md5
								formatterEntity.repositoryVersion = version
								extensionsDao.updateFormatter(formatterEntity)
							} else {
								extensionsDao.insertFormatter(ExtensionEntity(
										id = formatterID,
										repoID = repoID,
										name = formatterName,
										fileName = fileName,
										imageURL = imageURL,
										lang = lang,
										repositoryVersion = version,
										md5 = md5
								))
							}
						}
					}

				}
			} else {
				progressUpdate("Application is offline, Not updating")
			}
			Log.i(logID(), "Completed Update")
		}

	}

	private val notificationManager by lazy {
		(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
	}

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
		Log.d(logID(), "Canceling previous task")
		job?.cancel()
		Log.d(logID(), "Making new job")
		job = Job(this)
		Log.d(logID(), "Executing job")
		job?.let { Needle.onBackgroundThread().execute(it) }
				?: Log.e(logID(), "Job nullified before could be started")
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