package com.github.doomsdayrs.apps.shosetsu.backend

import android.content.Context
import android.os.Build
import android.os.Build.VERSION_CODES
import android.util.Log
import app.shosetsu.lib.LuaFormatter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.shosetsuRoomDatabase
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities.FormatterEntity
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities.RepositoryEntity
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities.ScriptLibEntity
import com.github.doomsdayrs.apps.shosetsu.variables.ext.getMeta
import com.github.doomsdayrs.apps.shosetsu.variables.ext.md5
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Formatters
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import java.io.File
import java.io.FileNotFoundException
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
	interface CheckSumAction {
		fun fail()
		fun pass()
		fun noMeta()
	}

	const val scriptDirectory = "/scripts/"
	const val libraryDirectory = "/libraries/"
	const val sourceFolder = "/src/"

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

	fun deleteScript(formatterEntity: FormatterEntity, context: Context) {
		if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
			Formatters.formatters.removeIf { it.formatterID == formatterEntity.formatterID }
		} else {
			Formatters.formatters.forEachIndexed loop@{ index, formatter ->
				if (formatter.formatterID == formatterEntity.formatterID) {
					Formatters.formatters.removeAt(index)
					return@loop
				}
			}
		}
		File(context.filesDir.absolutePath + sourceFolder +
				scriptDirectory + "/${formatterEntity.fileName}.lua"
		).takeIf { it.exists() }?.delete()

	}

	@Throws(FileNotFoundException::class, JSONException::class, SQLException::class)
	fun trustScript(file: File) {
		val name = file.nameWithoutExtension
		val meta = LuaFormatter(file).getMetaData()!!
		val md5 = file.readText().md5()!!
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
						name = name,
						enabled = true
				)
		)
	}

	// Room
	fun downloadLibrary(
			scriptLibEntity: ScriptLibEntity,
			context: Context,
			file: File = File(
					context.filesDir.absolutePath + sourceFolder +
							libraryDirectory + scriptLibEntity.version
			),
			repo: RepositoryEntity = shosetsuRoomDatabase.repositoryDao().loadRepositoryFromID(
					scriptLibEntity.repositoryID
			)) {

		val response = OkHttpClient().newCall(Request.Builder()
				.url("${repo.url}/src/main/resources/lib/${scriptLibEntity.scriptName}.lua")
				.build()
		).execute()

		if (file.parentFile?.exists() != true)
			file.parentFile!!.mkdirs()

		file.writeText(response.body!!.string())
	}


	/**
	 * Dynamic MD5 checking
	 */
	@Throws(JSONException::class, MissingResourceException::class)
	fun confirm(file: File, checkSumAction: CheckSumAction): Boolean {
		val meta = file.getMeta()

		// Checks MD5 sum
		val sum = shosetsuRoomDatabase.formatterDao()
				.loadFormatterMD5(meta.getInt("id"))

		require(sum.isNotEmpty())

		val fileSum = file.readText().md5()

		Log.i("FormatterInit", "${file.name}:\tSum required:{$sum}\tSum found:\t{$fileSum}")

		return if (sum == fileSum) {
			checkSumAction.pass()
			true
		} else {
			checkSumAction.fail()
			false
		}
	}


}