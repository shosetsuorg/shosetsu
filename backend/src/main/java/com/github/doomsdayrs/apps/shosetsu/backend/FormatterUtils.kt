package com.github.doomsdayrs.apps.shosetsu.backend

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import app.shosetsu.lib.LuaFormatter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.shosetsuRoomDatabase
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.FormatterEntity
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.RepositoryEntity
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.ScriptLibEntity
import com.github.doomsdayrs.apps.shosetsu.variables.ext.logID
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Formatters
import okhttp3.OkHttpClient
import okhttp3.Request
import org.doomsdayrs.apps.shosetsulib.BuildConfig
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.sql.SQLException
import java.util.*


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
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 * TODO Turn this into a service
 */
object FormatterUtils {
	const val scriptDirectory = "/scripts/"
	const val libraryDirectory = "/libraries/"
	const val sourceFolder = "/src/"

	private const val githubURL = "https://raw.githubusercontent.com/shosetsuorg/extensions/"
	val githubBranch = if (BuildConfig.DEBUG) "v1.0.0-rewrite" else "master"


	lateinit var sourceJSON: JSONObject

	fun md5(s: String): String? {
		try {
			// Create MD5 Hash
			val digest = MessageDigest.getInstance("MD5")
			digest.update(s.toByteArray())
			val messageDigest = digest.digest()
			// Create Hex String
			val hexString = StringBuffer()
			for (i in messageDigest.indices)
				hexString.append(Integer.toHexString(0xFF and messageDigest[i].toInt()))
			return hexString.toString()
		} catch (e: NoSuchAlgorithmException) {
			Log.wtf(logID(), "How could an MD5 alg be missing", e)
		}
		return ""
	}

	@Throws(FileNotFoundException::class)
	fun getContent(file: File): String {
		val builder = StringBuilder()
		val br = BufferedReader(FileReader(file))
		var line = br.readLine()
		while (line != null) {
			builder.append(line).append("\n")
			line = br.readLine()
		}
		return builder.toString()
	}

	private fun splitVersion(version: String): Array<String> =
			version.split(".").toTypedArray()

	/**
	 * @return [Boolean] true if version difference
	 */
	fun compareVersions(ver1: String, ver2: String): Boolean {
		if (ver1 == ver2)
			return false

		val version1 = splitVersion(ver1)
		val version2 = splitVersion(ver2)

		if (version1.size != version2.size)
			return false

		version1.forEachIndexed { index, s ->
			if (version2[index] != s)
				return true
		}
		return false
	}

	@Throws(FileNotFoundException::class)
	fun getMetaData(file: File): JSONObject? {
		val br = BufferedReader(FileReader(file))
		val line: String? = br.readLine()
		br.close()
		return if (line != null) JSONObject(line.toString().replace("-- ", "")) else null
	}

	fun downloadScript(name: String, lang: String, activity: Activity, downloaded: () -> Unit, process: (LuaFormatter) -> Unit, completed: () -> Unit) {
		Log.d("DownloadScript", "Downloading:\t$githubURL$githubBranch/src/main/resources/src/$lang/$name.lua")
		val request: DownloadManager.Request = DownloadManager.Request(Uri.parse("$githubURL$githubBranch/src/main/resources/src/$lang/$name.lua"))

		request.setDescription("Installing $name")
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, "$name.lua")

		val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
		val downloadID = manager.enqueue(request)
		val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
			override fun onReceive(context: Context?, intent: Intent) {
				val intentID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
				if (downloadID == intentID && context != null) {
					context.toast("Installed: $name")
					var file = context.getExternalFilesDir(null)!!.absolutePath
					file = file.substring(0, file.indexOf("/Android"))
					val downloadedFile = File("$file/${Environment.DIRECTORY_DOCUMENTS}/$name.lua")
					val targetFile = File(activity.filesDir.absolutePath + sourceFolder + scriptDirectory + "/$name.lua")
					Log.d("Extension download", downloadedFile.absolutePath)
					Log.d("Extension download", targetFile.absolutePath)

					try {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Files.move(downloadedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING) else downloadedFile.renameTo(targetFile)
					} catch (e: IOException) {
						TODO("Add error handling here")
					}
					downloaded()

					try {
						val form = LuaFormatter(targetFile)
						process(form)
						Formatters.formatters.add(form)
						Formatters.formatters.sortedWith(compareBy { it.name })
						completed()
					} catch (e: Exception) {
						Log.e(logID(), "Download:\tException unhandled", e)
					}
				}
				activity.unregisterReceiver(this)
			}
		}
		activity.registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
	}

	@Throws(SQLException::class)
	@Deprecated("Uses inefficent old method")
	fun deleteScript(name: String, id: Int, pre: () -> Unit, whenFound: () -> Unit, complete: () -> Unit) {
		pre()
		var b = true
		var i = 0
		while (i < Formatters.formatters.size && b) {
			if (Formatters.formatters[i].formatterID == id) {
				Formatters.formatters.removeAt(i)
				val targetFile = File(Utilities.shoDir + scriptDirectory + sourceFolder + "/$name.lua")
				targetFile.delete()
				b = false
				whenFound()
			}
			i++
		}
		complete()
		Database.DatabaseFormatters.removeFormatterFromList(id)
	}

	fun deleteScript(formatterEntity: FormatterEntity, context: Context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Formatters.formatters.removeIf { it.formatterID == formatterEntity.formatterID }
		} else {
			Formatters.formatters.forEachIndexed loop@{ index, formatter ->
				if (formatter.formatterID == formatterEntity.formatterID) {
					Formatters.formatters.removeAt(index)
					return@loop
				}
			}
			File(context.filesDir.absolutePath + sourceFolder +
					scriptDirectory + "/${formatterEntity.fileName}.lua"
			).takeIf { it.exists() }?.delete()
		}

	}


	@Throws(FileNotFoundException::class, JSONException::class, SQLException::class)
	fun trustScript(file: File) {
		val name = file.nameWithoutExtension
		val meta = LuaFormatter(file).getMetaData()!!
		val md5 = md5(getContent(file))!!
		val id = meta.getInt("id")
		val repo = meta.getJSONObject("repo")

		shosetsuRoomDatabase.formatterDao().insertFormatter(
				FormatterEntity(
						formatterID = id,
						repositoryID = shosetsuRoomDatabase.repositoryDao()
								.createIfNotExist(RepositoryEntity(
										url = repo.getString("URL"),
										name = repo.getString("name")
								)),
						fileName = file.name,
						installed = true,
						internal = false,
						name = name,
						enabled = true
				)
		)
	}

	@Throws(IOException::class)
	@Deprecated("Uses old database standard")
	fun downloadLibrary(name: String, file: File) {
		val response = OkHttpClient().newCall(
				Request.Builder()
						.url("$githubURL$githubBranch/src/main/resources/lib/$name.lua")
						.build()
		).execute()
		file.writeText(response.body.toString())
	}

	fun downloadLibrary(
			scriptLibEntity: ScriptLibEntity,
			context: Context,
			file: File = File(
					context.filesDir.absolutePath + sourceFolder +
							libraryDirectory + scriptLibEntity.version
			)) {
		val repo = scriptLibEntity.repository
		val response = OkHttpClient().newCall(Request.Builder()
				.url("${repo.url}/src/main/resources/lib/${scriptLibEntity.scriptName}.lua")
				.build()
		).execute()
		file.writeText(response.body.toString())
	}

	interface CheckSumAction {
		fun fail()
		fun pass()
		fun noMeta()
	}

	/**
	 * Dynamic MD5 checking
	 */
	@Throws(JSONException::class, MissingResourceException::class)
	fun confirm(file: File, checkSumAction: CheckSumAction): Boolean {
		val meta = getMetaData(file)
		return if (meta != null) {
			// Checks MD5 sum
			var sum = shosetsuRoomDatabase.formatterDao()
					.loadFormatterMD5(meta.getInt("id"))
			if (sum.isEmpty()) {
				sum = sourceJSON.getJSONObject(file.nameWithoutExtension)
						.getString("md5")
			}
			val content = getContent(file)
			val fileSum = md5(content)

			Log.i("FormatterInit", "${file.name}:\tSum required:{$sum}\tSum found:\t{$fileSum}")

			if (sum == fileSum) {
				checkSumAction.pass()
				true
			} else {
				checkSumAction.fail()
				false
			}
		} else {
			checkSumAction.noMeta()
			false
		}
	}


}