package com.github.doomsdayrs.apps.shosetsu.backend.services

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import app.shosetsu.lib.LuaFormatter
import app.shosetsu.lib.ShosetsuLib
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils.compareVersions
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils.confirm
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils.downloadLibrary
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils.getContent
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils.getMetaData
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils.githubBranch
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils.libraryDirectory
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils.scriptDirectory
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils.sourceFolder
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils.sourceJSON
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.ExtensionsController
import com.github.doomsdayrs.apps.shosetsu.ui.susScript.SusScriptDialog
import com.github.doomsdayrs.apps.shosetsu.variables.ext.logID
import com.github.doomsdayrs.apps.shosetsu.variables.ext.smallMessage
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast
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
 * 04 / 03 / 2020
 *
 * @author github.com/doomsdayrs
 */
object FormatterService {
	lateinit var sourceFilePath: String

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

	fun initalizeValues(context: Context) {
		sourceFilePath = context.filesDir.absolutePath + "/formatters.json"
	}

	@Throws(JSONException::class)
	fun loadSourceFile() {
		sourceJSON = JSONObject(getContent(File(sourceFilePath)))
	}

	fun downloadSourceFile(sourceFile: File = File(sourceFilePath), progressUpdate: (m: String) -> Unit) {
		if (Utilities.isOnline) {
			var json = "{}"
			try {
				val doc = Jsoup.connect("https://raw.githubusercontent.com/Doomsdayrs/shosetsu-extensions/$githubBranch/src/main/resources/formatters.json").get()
				if (doc != null) {
					progressUpdate("Sources downloaded, Writing..")
					sourceFile.writeText(doc.body().text())
					progressUpdate("Sources loaded")
				}
				sourceJSON = JSONObject(json)
			} catch (e: IOException) {
				progressUpdate("Failed to download")
			}
		} else {
			progressUpdate("Application is offline, Using placeholder")
			Log.e("FormatterInit", "Is Offline, Cannot load data, Using stud")
			sourceJSON.put("libraries", JSONArray())
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
				downloadSourceFile(sourceFile, progressUpdate)
			}
		}

		// Auto Download all source material
		run {
			progressUpdate("Checking libraries")
			val libraries: JSONArray = sourceJSON.getJSONArray("libraries")
			for (index in 0 until libraries.length()) {
				val libraryJSON: JSONObject = libraries.getJSONObject(index)
				val name = libraryJSON.getString("name")

				progressUpdate("Checking library: $name")

				val libraryFile = File(activity.filesDir.absolutePath + sourceFolder + libraryDirectory + "$name.lua")
				if (libraryFile.exists()) {
					progressUpdate("Library $name found, Checking for update")
					val meta = getMetaData(libraryFile)!!
					if (compareVersions(meta.getString("version"), libraryJSON.getString("version"))) {
						progressUpdate("Library $name update found, updating...")
						Log.i("FormatterInit", "Installing library:\t$name")
						if (Utilities.isOnline)
							downloadLibrary(name, libraryFile)
						else progressUpdate("Is offline, Cannot update")
					}
				} else {
					progressUpdate("Library $name not found, installing...")
					if (Utilities.isOnline)
						downloadLibrary(name, libraryFile)
					else progressUpdate("Is offline, Cannot install")
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

	/**
	 * Loads a new JSON file to be used
	 */
	class RefreshJSON(val context: Context, private val extensionsFragment: ExtensionsController) : AsyncTask<Void, Void, Void>() {
		override fun doInBackground(vararg params: Void?): Void? {
			val sourceFile = File(context.filesDir.absolutePath + "/formatters.json")
			if (Utilities.isOnline) {
				Log.d(logID(), githubBranch)
				try {
					Jsoup.connect("https://raw.githubusercontent.com/Doomsdayrs/shosetsu-extensions/$githubBranch/src/main/resources/formatters.json").get()?.let {
						sourceFile.writeText( it.body().text())
					}
				} catch (e: IOException) {
					context.toast(e.message ?: "Unknown error")
				}
			} else {
				Log.e("FormatterInit", "IsOffline, Cannot load data, Using stud")
			}
			return null
		}

		override fun onPostExecute(result: Void?) {
			context.toast(com.github.doomsdayrs.apps.shosetsu.R.string.updated_extensions_list)
			extensionsFragment.setData()
			extensionsFragment.adapter?.notifyDataSetChanged()
		}
	}

}