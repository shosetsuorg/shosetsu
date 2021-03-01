package app.shosetsu.common.datasource.file.impl

import app.shosetsu.common.datasource.file.base.IFileCachedChapterDataSource
import app.shosetsu.common.dto.*
import app.shosetsu.common.enums.InternalFileDir.CACHE
import app.shosetsu.common.providers.file.base.IFileSystemProvider
import app.shosetsu.lib.Novel
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * 20 / 01 / 2021
 */
class QueuedFileCacheChapterDataSource(
	private val iFileSystemProvider: IFileSystemProvider
) : IFileCachedChapterDataSource {

	private val mapHandler by lazy { MapHandler() }

	override suspend fun saveChapterInCache(
		chapterID: Int,
		chapterType: Novel.ChapterType,
		passage: String
	): HResult<*> {
		val job = CacheJob.Write(chapterID, passage)
		mapHandler.enqueue(job)
		return successResult(mapHandler.waitFor(job))
	}

	override suspend fun loadChapterPassage(
		chapterID: Int,
		chapterType: Novel.ChapterType
	): HResult<String> {
		val job = CacheJob.Read(chapterID)
		mapHandler.enqueue(job)
		return mapHandler.waitFor(job)
	}


	private inner class MapHandler {
		private var workerJob: Job? = null

		init {
			iFileSystemProvider.createDirectory(CACHE, chaptersCacheDir)
		}

		/**
		 * Simply creates a file object
		 */
		private fun createFilePath(id: Int): String = "$chaptersCacheDir/$id.txt"


		/**
		 * ChapterID to Time
		 */
		private val cacheMap: HashMap<Int, Long> by lazy {
			iFileSystemProvider.readFile(
				CACHE,
				mapFile
			).unwrap()?.let { Json { }.decodeFromString(it) } ?: hashMapOf()
		}

		/**
		 * Jobs that must be done
		 */
		private val jobsToComplete = ArrayList<CacheJob>()

		/**
		 * The job to the result
		 */
		private val completedJobs = HashMap<CacheJob, HResult<String>>()

		fun enqueue(cacheJob: CacheJob) {
			jobsToComplete.add(cacheJob)
			if (workerJob == null) start()
		}

		private fun launchCleanUp() {

			// Filters out when over sized
			while (cacheMap.size > 100) {
				val first = cacheMap.entries.first()
				val chapterID = first.key
				cacheMap.remove(chapterID)
				iFileSystemProvider.deleteFile(CACHE, createFilePath(chapterID))
			}

			// Filters out expired data
			for (i in cacheMap.entries.size - 1 downTo 0) {
				val entries = cacheMap.entries
				val last = entries.last()
				val chapterID = last.key
				val time = last.value

				// If the time is less then 1 hours * CACHE_TIME (meaning its over lifespan)
				// Deletes the obj
				if (time < (System.currentTimeMillis() - (3600000 * CACHE_TIME))) {
					iFileSystemProvider.deleteFile(CACHE, createFilePath(chapterID))
					cacheMap.remove(chapterID)
				}
			}
			writeMapFile()
		}

		/**
		 * Writes the instruct file
		 */
		private fun writeMapFile() {
			iFileSystemProvider.writeFile(
				CACHE,
				mapFile,
				Json { }.encodeToString(cacheMap)
			)
		}

		private fun start() {
			workerJob = GlobalScope.launch(context = Dispatchers.IO) {
				// Goes through all the jobs
				println("What am i doing?")
				while (jobsToComplete.isNotEmpty()) {
					val jobToComplete = jobsToComplete.first()
					jobsToComplete.removeFirst()

					println("Doing job $jobToComplete")

					completedJobs[jobToComplete] = when (jobToComplete) {
						is CacheJob.Read -> {
							iFileSystemProvider.readFile(
								CACHE,
								createFilePath(jobToComplete.chapterID),
							)
						}
						is CacheJob.Write -> {
							iFileSystemProvider.writeFile(
								CACHE,
								createFilePath(jobToComplete.chapterID),
								jobToComplete.data
							).transform {
								cacheMap[jobToComplete.chapterID] = System.currentTimeMillis()
								successResult("")
							}.also { launchCleanUp() }
						}
					}
					println("Finished job $jobToComplete")
					println("$completedJobs")
				}
				workerJob = null
			}
		}

		suspend fun waitFor(cacheJob: CacheJob): HResult<String> {
			while (!completedJobs.contains(cacheJob)) {
				delay(1)
			}

			return completedJobs[cacheJob].also { completedJobs.remove(cacheJob) }
				?: emptyResult()
		}
	}

	private sealed class CacheJob(val chapterID: Int) {
		class Write(chapterID: Int, val data: String) : CacheJob(chapterID)
		class Read(chapterID: Int) : CacheJob(chapterID)
	}

	companion object {
		private const val chaptersCacheDir = "/q-cachedChapters/"
		private const val mapFile = "$chaptersCacheDir/map.json"

		/** Time in hours */
		private const val CACHE_TIME = 1
	}
}