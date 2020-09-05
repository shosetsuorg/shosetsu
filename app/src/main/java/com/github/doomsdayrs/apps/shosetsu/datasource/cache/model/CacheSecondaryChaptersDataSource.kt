package com.github.doomsdayrs.apps.shosetsu.datasource.cache.model

import android.app.Application
import android.util.Log
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.emptyResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.errorResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.forEach
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.datasource.cache.base.ICacheSecondaryChaptersDataSource
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File

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
 * 17 / 08 / 2020
 */
class CacheSecondaryChaptersDataSource(
		private val application: Application,
) : ICacheSecondaryChaptersDataSource {
	@get:Synchronized
	private val cacheDir by lazy { application.cacheDir }

	@get:Synchronized
	private val chaptersCacheDir by lazy {
		File(cacheDir.path + "/chapters").also {
			if (!it.exists())
				it.mkdir()
		}
	}

	@get:Synchronized
	private val chaptersCacheInstructionFile by lazy {
		File(chaptersCacheDir.path + "/map.json").also {
			if (!it.exists()) {
				it.createNewFile()
				it.writeText("[]")
			}
		}
	}

	@get:Synchronized
	private val chaptersCacheInstruction: JSONArray by lazy {
		JSONArray(chaptersCacheInstructionFile.readText())
	}

	@get:Synchronized
	@set:Synchronized
	/**
	 * Helps prevent reruns
	 */
	private var running: Boolean = false

	@Synchronized
	/**
	 * Writes the instruct file
	 */
	private fun writeFile() {
		chaptersCacheInstructionFile.writeText(chaptersCacheInstruction.toString(1))
	}

	/**
	 * Reads the instruct
	 */
	private fun readFile() {
		chaptersCacheInstruction.apply array@{
			for (i in this@array.length() - 1 downTo 0) this@array.remove(i)
			JSONArray(chaptersCacheInstructionFile.readText()).forEach {
				this@array.put(it)
			}
		}
	}

	@Synchronized
	/**
	 * Simply creates a file object
	 */
	private fun createFile(id: Int) = File(chaptersCacheDir.path + "/$id.txt")

	/**
	 * Clears out [chaptersCacheInstruction] of its incorrect data
	 */
	@Throws(JSONException::class)
	@Suppress("RedundantSuspendModifier")
	@Synchronized
	private suspend fun launchCleanUp() {
		if (running) return
		running = true
		Log.i(logID(), "Cleaning up chapter file cache")

		// Filters out
		while (chaptersCacheInstruction.length() > 100) {
			val obj = chaptersCacheInstruction.getJSONObject(0)
			chaptersCacheInstruction.remove(0)
			val id = obj.getInt(CHAPTER_KEY)
			Log.d(logID(), "#### REMOVING $id FROM FILE CACHE DUE TO OVERFLOW ####")
			File(chaptersCacheDir.path + "/$id.txt").delete()
		}

		// Filters out expired data
		for (i in chaptersCacheInstruction.length() - 1 downTo 0) {
			val obj = chaptersCacheInstruction.getJSONObject(i)
			val time = obj.getLong(TIME_KEY)
			// If the time is less then 1 hours * CACHE_TIME (meaning its over lifespan)
			// Deletes the obj
			if (time < (System.currentTimeMillis() - (3600000 * CACHE_TIME))) {
				val id = obj.getInt(CHAPTER_KEY)
				Log.d(logID(), "#### REMOVING $id FROM FILE CACHE ####")
				File(chaptersCacheDir.path + "/$id.txt").delete()
				chaptersCacheInstruction.remove(i)
				continue
			}
		}
		writeFile()
		running = false
		Log.i(logID(), "Finished cleaning up")

	}

	@Throws(JSONException::class)
	@Synchronized
	override suspend fun saveChapterInCache(chapterID: Int, passage: String): HResult<*> {
		try {
			// Looks for the chapter if its already in the instruction set
			// If found, it updates the time and writes the new data
			for (i in 0 until chaptersCacheInstruction.length()) {
				val obj = chaptersCacheInstruction.getJSONObject(i)
				val id = obj.getInt(CHAPTER_KEY)
				if (id == chapterID) {
					createFile(chapterID).writeText(passage)
					obj.put(TIME_KEY, System.currentTimeMillis())
					chaptersCacheInstruction.put(i, obj)
					return successResult("")
				}
			}

			// Writes data to txt file then updates the chapterInstruction json

			createFile(chapterID).writeText(passage)
			chaptersCacheInstruction.put(JSONObject().apply {
				put(CHAPTER_KEY, chapterID)
				put(TIME_KEY, System.currentTimeMillis())
			})

			writeFile()

			launchIO { launchCleanUp() } // Launch cleanup separately
			return successResult("")
		} catch (e: JSONException) {
			return errorResult(e)
		}
	}

	@Synchronized
	override suspend fun loadChapterPassage(chapterID: Int): HResult<String> {
		launchIO { launchCleanUp() } // Launch cleanup separately

		return createFile(chapterID).takeIf { it.exists() }?.let { successResult(it.readText()) }
				?: emptyResult()
	}

	companion object {
		private const val CHAPTER_KEY = "chapterID"
		private const val TIME_KEY = "time"

		/** Time in hours */
		private const val CACHE_TIME = 1
	}
}