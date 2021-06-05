package app.shosetsu.android.datasource.local.file.impl

import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.common.datasource.file.base.IFileCachedChapterDataSource
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transmogrify
import app.shosetsu.common.enums.InternalFileDir.CACHE
import app.shosetsu.common.providers.file.base.IFileSystemProvider
import app.shosetsu.lib.Novel
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
 * 17 / 08 / 2020
 */
class FileCachedChapterDataSource(
	private val iFileSystemProvider: IFileSystemProvider
) : IFileCachedChapterDataSource {

	init {
		logV("Creating required directories")
		iFileSystemProvider.createDirectory(CACHE, chaptersCacheDir).handle(
			onError = {
				logV("Error on creation of directories $it")
			},
			onSuccess = {
				logV("Created required directories")
			}
		)
	}

	@get:Synchronized
	private val chaptersCacheInstruction: JSONArray by lazy {
		iFileSystemProvider.readFile(
			CACHE,
			mapFile
		).transmogrify(
			onError = {
				logE("Error on reading cache chapters index, Writing empty one")
				iFileSystemProvider.writeFile(
					CACHE,
					mapFile,
					JSONArray().toString().toByteArray()
				)
				null
			}
		) {
			try {
				JSONArray(it.decodeToString())
			} catch (e: Exception) {
				JSONArray()
			}
		} ?: JSONArray()
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
		iFileSystemProvider.writeFile(
			CACHE,
			mapFile,
			chaptersCacheInstruction.toString(1).toByteArray()
		)
	}

	@Synchronized
	/**
	 * Simply creates a file object
	 */
	private fun createFilePath(id: Int, chapterType: Novel.ChapterType): String =
		"$chaptersCacheDir/$id.${chapterType.fileExtension}"

	/**
	 * Clears out [chaptersCacheInstruction] of its incorrect data
	 */
	@Throws(JSONException::class)
	@Suppress("RedundantSuspendModifier")
	@Synchronized
	private suspend fun launchCleanUp() {
		if (running) return
		running = true
		//Log.i(logID(), "Cleaning up chapter file cache")

		// Filters out
		while (chaptersCacheInstruction.length() > 100) {
			val obj = chaptersCacheInstruction.getJSONObject(0)
			chaptersCacheInstruction.remove(0)
			val id = obj.getInt(CHAPTER_KEY)
			//	logD("#### REMOVING $id FROM FILE CACHE DUE TO OVERFLOW ####")
			iFileSystemProvider.deleteFile(CACHE, "$chaptersCacheDir/$id.txt")
		}

		// Filters out expired data
		for (i in chaptersCacheInstruction.length() - 1 downTo 0) {
			val obj = chaptersCacheInstruction.getJSONObject(i)
			val time = obj.getLong(TIME_KEY)
			// If the time is less then 1 hours * CACHE_TIME (meaning its over lifespan)
			// Deletes the obj
			if (time < (System.currentTimeMillis() - (3600000 * CACHE_TIME))) {
				val id = obj.getInt(CHAPTER_KEY)
				//		Log.d(logID(), "#### REMOVING $id FROM FILE CACHE ####")
				iFileSystemProvider.deleteFile(CACHE, "$chaptersCacheDir/$id.txt")
				chaptersCacheInstruction.remove(i)
				continue
			}
		}
		writeFile()
		running = false
		//Log.i(logID(), "Finished cleaning up")
	}

	@Throws(JSONException::class)
	@Synchronized
	override suspend fun saveChapterInCache(
		chapterID: Int,
		chapterType: Novel.ChapterType,
		passage: ByteArray
	): HResult<*> {
		try {
			// Looks for the chapter if its already in the instruction set
			// If found, it updates the time and writes the new data
			for (index in 0 until chaptersCacheInstruction.length()) {
				val obj = chaptersCacheInstruction.getJSONObject(index)
				val id = obj.getInt(CHAPTER_KEY)
				if (id == chapterID) {
					iFileSystemProvider.writeFile(
						CACHE,
						createFilePath(chapterID, chapterType),
						passage
					)
					obj.put(TIME_KEY, System.currentTimeMillis())
					chaptersCacheInstruction.put(index, obj)
					return successResult("")
				}
			}

			// Writes data to txt file then updates the chapterInstruction json

			iFileSystemProvider.writeFile(
				CACHE,
				createFilePath(chapterID, chapterType),
				passage
			)
			chaptersCacheInstruction.put(JSONObject().apply {
				put(CHAPTER_KEY, chapterID)
				put(TIME_KEY, System.currentTimeMillis())
			})

			writeFile()

			launchIO { launchCleanUp() } // Launch cleanup separately
			return successResult("")
		} catch (e: Exception) {
			return e.toHError()
		}
	}

	@Synchronized
	override suspend fun loadChapterPassage(
		chapterID: Int,
		chapterType: Novel.ChapterType
	): HResult<ByteArray> {
		launchIO { launchCleanUp() } // Launch cleanup separately
		return iFileSystemProvider.readFile(CACHE, createFilePath(chapterID, chapterType))
	}

	companion object {


		const val chaptersCacheDir = "/cachedChapters/"
		const val mapFile = "$chaptersCacheDir/map.json"

		private const val CHAPTER_KEY = "chapterID"
		private const val TIME_KEY = "time"

		/** Time in hours */
		private const val CACHE_TIME = 1
	}
}