package com.github.doomsdayrs.apps.shosetsu.backend

import android.content.Context
import android.util.Log
import app.shosetsu.lib.LuaFormatter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.extensionsDao
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.repositoryDao
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities.ExtensionEntity
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities.ExtensionLibraryEntity
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities.RepositoryEntity
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
	const val repoFolderStruct = "/src/main/resources/"

	private fun Context.ap() = filesDir.absolutePath

	fun makeLibraryFile(context: Context, le: ExtensionLibraryEntity): File {
		return makeLibraryFile(context, le.scriptName)
	}

	fun makeLibraryFile(context: Context, scriptName: String): File {
		val f = File("${context.ap()}$sourceFolder$libraryDirectory${scriptName}.lua")
		f.parentFile?.let { if (!it.exists()) it.mkdirs() }
		return f
	}

	fun makeFormatterFile(context: Context, fe: ExtensionEntity): File {
		return makeFormatterFile(context, fe.fileName)
	}

	fun makeFormatterFile(context: Context, fileName: String): File {
		val f = File("${context.ap()}$sourceFolder$scriptDirectory${fileName}.lua")
		f.parentFile?.let { if (!it.exists()) it.mkdirs() }
		return f
	}

	fun makeLibraryURL(repo: RepositoryEntity, le: ExtensionLibraryEntity): String =
			"${repo.url}$repoFolderStruct/lib/${le.scriptName}.lua"

	fun makeFormatterURL(repo: RepositoryEntity, fe: ExtensionEntity): String =
			"${repo.url}$repoFolderStruct/src/${fe.lang}/${fe.fileName}.lua"

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


	@Throws(FileNotFoundException::class, JSONException::class, SQLException::class)
	fun trustScript(file: File) {
		val name = file.nameWithoutExtension
		val meta = LuaFormatter(file).getMetaData()!!
		val md5 = file.readText().md5()!!
		val id = meta.getInt("id")
		val repo = meta.getJSONObject("repo")

		extensionsDao.insertFormatter(
				ExtensionEntity(
						id = id,
						repoID = repositoryDao
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


	/**
	 * Dynamic MD5 checking
	 */
	@Throws(JSONException::class, MissingResourceException::class)
	fun confirm(file: File, checkSumAction: CheckSumAction): Boolean {
		val meta = file.getMeta()

		// Checks MD5 sum
		val sum = extensionsDao
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

	/**
	 * A quick way to get a response
	 */
	fun quickResponse(url: String) =
			OkHttpClient().newCall(Request.Builder()
					.url(url)
					.build()
			).execute()

	/**
	 * Installs the library
	 */
	fun downloadLibrary(
			extensionLibraryEntity: ExtensionLibraryEntity,
			context: Context,
			file: File = makeLibraryFile(context, extensionLibraryEntity),
			repo: RepositoryEntity = repositoryDao.loadRepositoryFromID(
					extensionLibraryEntity.repoID
			)
	): Boolean {
		return quickResponse(makeLibraryURL(repo, extensionLibraryEntity)).body?.let {
			file.writeText(it.string())
			true
		} ?: false
	}

	/**
	 * Installs the extension in question
	 */
	fun installExtension(
			extensionEntity: ExtensionEntity,
			context: Context,
			file: File = makeFormatterFile(context, extensionEntity),
			repo: RepositoryEntity = repositoryDao.loadRepositoryFromID(
					extensionEntity.repoID
			)
	): Boolean {
		return quickResponse(makeFormatterURL(repo, extensionEntity)).body?.let {
			file.writeText(it.string())
			Formatters.addFormatter(LuaFormatter(file))
			true
		} ?: false
	}

	fun deleteFormatter(extensionEntity: ExtensionEntity, context: Context) {
		Formatters.removeByID(extensionEntity.id)
		makeFormatterFile(context, extensionEntity).takeIf { it.exists() }?.delete()
	}

}