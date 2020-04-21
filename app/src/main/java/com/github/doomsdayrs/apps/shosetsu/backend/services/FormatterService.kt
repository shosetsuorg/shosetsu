package com.github.doomsdayrs.apps.shosetsu.backend.services
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

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.IBinder
import android.util.Log
import app.shosetsu.lib.LuaFormatter
import app.shosetsu.lib.ShosetsuLib
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils.compareVersions
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils.confirm
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils.libraryDirectory
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils.scriptDirectory
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils.sourceFolder
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.shosetsuRoomDatabase
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities.ScriptLibEntity
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.ExtensionsController
import com.github.doomsdayrs.apps.shosetsu.ui.susScript.SusScriptDialog
import com.github.doomsdayrs.apps.shosetsu.variables.ext.*
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Formatters
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.luaj.vm2.LuaError
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


/**
 * shosetsu
 * 04 / 03 / 2020
 *
 * @author github.com/doomsdayrs
 */
object FormatterService  {
	class FormatterInit(val activity: Activity) : AsyncTask<Void, Void, Void>() {
		private val unknownFormatters = ArrayList<File>()

		override fun doInBackground(vararg params: Void?): Void? {
			unknownFormatters.addAll(formatterInitTask(activity) {})
			return null
		}

		override fun onPostExecute(result: Void?) {
			formatterInitPost(unknownFormatters, activity) {}
		}

	}
	/**
	 * Loads a new JSON file to be used
	 */
	class RefreshJSON(val context: Context, private val extensionsFragment: ExtensionsController) : AsyncTask<Void, Void, Void>() {
		override fun doInBackground(vararg params: Void?): Void? {
			RepositoryService.task(context) {}
			return null
		}

		override fun onPostExecute(result: Void?) {
			context.toast(com.github.doomsdayrs.apps.shosetsu.R.string.updated_extensions_list)
			extensionsFragment.setData()
			extensionsFragment.adapter?.notifyDataSetChanged()
		}
	}

	fun update(context: Context, progressUpdate: (m: String) -> Unit) {
		if (Utilities.isOnline) {
			progressUpdate("Online, Loading repositories")
			val repos = shosetsuRoomDatabase.repositoryDao()
					.loadRepositories()

			for (repo in repos) {
				val name = repo.name
				val url = repo.url
				val repoID = repo.id

				progressUpdate("Checking $name")
				try {
					// gets the latest list for the repo
					val formattersJSON = JSONObject(Jsoup.connect(
							"$url/src/main/resources/formatters.json"
					).get().body().text())

					run {
						// Array of libraries
						val libJSONArray = formattersJSON.getJSONArray("libraries")

						// Libraries in database
						val libEntities = shosetsuRoomDatabase.scriptLibDao()
								.loadLibByRepoID(repoID)

						// Libraries not installed or needs update
						val libsNotPresent = ArrayList<ScriptLibEntity>()

						// Loops through the json array of libraries
						for (index in 0 until libJSONArray.length()) {
							(libJSONArray[index] as JSONObject).let {
								val name = it.getString("name")
								val position = libEntities.containsName(it.getString("name"))
								var install = false
								var scriptLibEntity: ScriptLibEntity? = null

								if (position != -1) {
									//  Checks if an update need
									val version = it.getString("version")
									scriptLibEntity = libEntities[position]
									if (compareVersions(version, scriptLibEntity.version))
										install = true
								} else {
									install = true
								}

								// If install is true, then it adds it to the notPresent
								if (install)
									libsNotPresent.add(
											scriptLibEntity ?: ScriptLibEntity(
													scriptName = name,
													version = it.getString("version"),
													repositoryID = repoID
											)
									)

							}
						}

						// For each library not present, installs
						libsNotPresent.forEach {
							progressUpdate("Updating/Installing ${it.scriptName}")
							shosetsuRoomDatabase.scriptLibDao().insertOrUpdateScriptLib(it)
							FormatterUtils.downloadLibrary(it, context)
						}
					}


				} catch (e: IOException) {
					progressUpdate("Failed to download")
				} catch (e: JSONException) {
					Log.e(logID(), "JSON error", e)
				}
			}
		} else {
			progressUpdate("Application is offline, Not updating")
		}
	}

	fun formatterInitTask(activity: Activity, progressUpdate: (m: String) -> Unit): ArrayList<File> {
		val unknownFormatters = ArrayList<File>()

		progressUpdate("Initializing extensions")

		// Source files
		run {
			val sourceFile = File(sourceFilePath)
			if (sourceFile.exists() && sourceFile.isFile) {
				try {
					loadSourceFile()
				} catch (e: Exception) {
					Log.e(logID(), "I had an exception reading the json?? nani??, ill just..")
				}
				progressUpdate("Sources found and loaded")
			} else {
				progressUpdate("Sources not found, Downloading..")
				Log.i("FormatterInit", "Downloading formatterJSON")
				try {
					sourceFile.createNewFile()
				} catch (e: IOException) {
					Log.wtf("Could not even create a new file, Aborting program", e)
				}
				update(sourceFile, progressUpdate)
			}
		}

		// Auto Download all source material
		run {
			progressUpdate("Checking libraries")

			val libraries: JSONArray = try {
				sourceJSON.getJSONArray("libraries")
			} catch (e: Error) {
				Log.w("FormatterService", sourceJSON.toString(4))
				throw e
			}


			for (index in 0 until libraries.length()) {
				val libraryJSON: JSONObject = libraries.getJSONObject(index)
				val name = libraryJSON.getString("name")

				progressUpdate("Checking library: $name")

				val libraryFile = File(activity.filesDir.absolutePath + sourceFolder + libraryDirectory + "$name.lua")
				if (libraryFile.exists()) {
					progressUpdate("Library $name found, Checking for update")
					val meta = libraryFile.getMeta()
					if (compareVersions(meta.getString("version"), libraryJSON.getString("version"))) {
						progressUpdate("Library $name update found, updating...")
						Log.i("FormatterInit", "Installing library:\t$name")
						if (Utilities.isOnline) {
							TODO("download")
						} else progressUpdate("Is offline, Cannot update")
					}
				} else {
					progressUpdate("Library $name not found, installing...")
					if (Utilities.isOnline) {
						TODO("download")
					} else progressUpdate("Is offline, Cannot install")
				}

				progressUpdate("Moving on..")
			}
		}

		ShosetsuLib.libLoader = { name ->
			Log.i("LibraryLoaderSync", "Loading:\t$name")
			val libraryFile = File(activity.filesDir.absolutePath + sourceFolder + libraryDirectory + "$name.lua")
			if (!libraryFile.exists()) Log.e("LibraryLoaderSync", "FAIL")
			Log.d("LibraryLoaderSync", libraryFile.absolutePath)

			val script = JsePlatform.standardGlobals()
			script.load(ShosetsuLib())
			val l = try {
				script.load(libraryFile.readText())!!
			} catch (e: Error) {
				throw e
			}
			l.call()
		}

		progressUpdate("Loading extensions")
		// Load the private scripts
		run {
			val path = activity.filesDir.absolutePath + sourceFolder + scriptDirectory
			val directory = File(path)
			if (directory.isDirectory && directory.exists()) {
				val sources = directory.listFiles()
				val jsonArray = Settings.disabledFormatters
				if (sources != null) {
					for (source in sources) {
						progressUpdate("${source.name} found")
						if (!Utilities.isFormatterDisabled(jsonArray, source.nameWithoutExtension)) {
							progressUpdate("${source.name} added")
							try {
								val l = LuaFormatter(source)
								if (Formatters.getByID(l.formatterID) == Formatters.unknown)
									Formatters.formatters.add(l)
							} catch (e: Exception) {
								when (e) {
									is LuaError -> Log.e("FormatterInit", "LuaFormatter had an issue!${e.smallMessage()}")
									else -> Log.e("FormatterInit", "LuaFormatter may be missing something!")
								}
								Log.e("FormatterInit", "We won't accept broken ones :D, Bai bai!")
								source.delete()
							}
						} else {
							progressUpdate("${source.name} ignored")
						}
					}
				} else {
					progressUpdate("No extensions found")
					Log.e("FormatterInit", "Sources file returned null")
				}
			} else {
				progressUpdate("Extension folder not found, Creating")
				directory.mkdirs()
			}
		}

		progressUpdate("Loading custom scripts")
		run {
			val path = Utilities.shoDir + scriptDirectory
			// Check if script MD5 matches DB
			val directory = File(path)
			if (directory.isDirectory && directory.exists()) {
				val sources = directory.listFiles()
				val jsonArray = Settings.disabledFormatters
				if (sources != null) {
					progressUpdate("Loading custom scripts")
					for (source in sources) {
						try {
							confirm(source, object : FormatterUtils.CheckSumAction {
								override fun fail() {
									Log.i("FormatterInit", "${source.name}:\tSum does not match, Adding")
									unknownFormatters.add(source)
								}

								override fun pass() {
									if (!Utilities.isFormatterDisabled(jsonArray, source.nameWithoutExtension)) {
										val l = LuaFormatter(source)
										if (Formatters.getByID(l.formatterID) == Formatters.unknown)
											Formatters.formatters.add(l)
									}
								}

								override fun noMeta() {
									Log.i("FormatterInit", "${source.name}:\tNo meta found, Adding")
									unknownFormatters.add(source)
								}

							})
						} catch (e: JSONException) {
							TODO("Add error handling here")
						} catch (e: MissingResourceException) {
							TODO("Add error handling here")
						}
					}
				} else {
					progressUpdate("No custom scripts found")
					Log.e("FormatterInit", "External Sources file returned null")
				}
			} else {
				progressUpdate("Custom script folder doesn't exist, Creating")
				directory.mkdirs()
			}
		}
		for (unknownFormatter in unknownFormatters) {
			Log.e("FormatterInit", "Unknown Script:\t${unknownFormatter.name}")
		}
		Formatters.formatters.sortedWith(compareBy { it.name })
		progressUpdate("Completed load")
		return unknownFormatters
	}

	fun formatterInitPost(unknownFormatters: ArrayList<File>, activity: Activity, finalAction: () -> Unit) {
		if (unknownFormatters.size > 0) {
			SusScriptDialog(activity, unknownFormatters).execute(finalAction)
		}
	}
}