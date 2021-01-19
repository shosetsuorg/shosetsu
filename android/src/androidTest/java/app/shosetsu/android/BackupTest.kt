package app.shosetsu.android

import android.content.Context
import android.util.Base64
import androidx.test.platform.app.InstrumentationRegistry
import app.shosetsu.common.domain.model.local.BackupEntity
import app.shosetsu.common.domain.repositories.base.IBackupRepository
import app.shosetsu.common.dto.unwrap
import app.shosetsu.common.enums.ReadingStatus
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.internal.toHexString
import org.junit.Test
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.random.Random
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

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
 * 18 / 01 / 2021
 */
class BackupTest : KodeinAware {

	private val context: Context by lazy {
		InstrumentationRegistry.getInstrumentation().targetContext
	}

	override val kodein: Kodein by closestKodein(context)

	private val backupRepository by instance<IBackupRepository>()

	private val randomInt
		get() = Random.nextInt() % 100 + 10

	private val randomLong
		get() = Random.nextLong() % 10000

	private val randomString
		get() = randomLong.toHexString()

	private fun randomRepositories() = ArrayList<SimpleRepositoryEntity>().apply {
		for (i in 0 until randomInt) {
			add(
				SimpleRepositoryEntity(
					randomString, randomString
				)
			)
		}
	}

	private fun randomChapters() = ArrayList<SimpleChapterEntity>().apply {
		for (i in 0 until randomInt) {
			add(
				SimpleChapterEntity(
					randomString, randomString,
					Random.nextBoolean(),
					ReadingStatus.fromInt(Random.nextInt() % 3 + 1),
					randomInt
				)
			)
		}
	}

	private fun randomNovels() = ArrayList<SimpleNovelEntity>().apply {
		for (i in 0 until randomInt) {
			add(
				SimpleNovelEntity(
					randomString,
					randomString,
					randomString,
					randomChapters()
				)
			)
		}
	}

	private fun randomExtensions() = ArrayList<SimpleExtensionEntity>().apply {
		for (i in 0 until randomInt) {
			add(
				SimpleExtensionEntity(
					randomInt,
					randomNovels()
				)
			)
		}
	}

	private val backup by lazy {
		FleshedBackupEntity(
			randomRepositories(),
			randomExtensions()
		)
	}


	@Throws(IOException::class)
	fun gzip(content: String): ByteArray {
		val bos = ByteArrayOutputStream()
		GZIPOutputStream(bos).bufferedWriter().use { it.write(content) }
		return bos.toByteArray()
	}

	@Throws(IOException::class)
	fun ungzip(content: ByteArray): String =
		GZIPInputStream(content.inputStream()).bufferedReader().use { it.readText() }

	@ExperimentalTime
	@Test
	fun test() {
		GlobalScope.future {
			measureTimedValue { backup }.also {
				println("Created backup randomly in ${it.duration.inMilliseconds}ms")
			}.value.let {
				measureTimeMillis {
					println("Repository count ${it.repos.size}")
					println("Extensions count ${it.extensions.size}")
					println(
						"Novels count ${
							it.extensions.foldRight(0) { entity, ac ->
								ac + entity.novels.size
							}
						}"
					)
					println(
						"Chapters count ${
							it.extensions.foldRight(0) { extension, nc ->
								nc + extension.novels.foldRight(0) { novel, cc ->
									cc + novel.chapters.size
								}
							}
						}"
					)
				}.let {
					println("Printed count in ${it}ms")
				}
			}

			val stringBackup = measureTimedValue { Json {}.encodeToString(backup) }.also {
				println("Serialized backup in ${it.duration.inMilliseconds}ms")
			}.value

			val zippedBytes = measureTimedValue { gzip(stringBackup) }.also {
				println("Zipped backup in ${it.duration.inMilliseconds}ms")
			}.value

			val base64Bytes =
				measureTimedValue { Base64.encodeToString(zippedBytes, Base64.DEFAULT) }.also {
					println("Base64 backup in ${it.duration.inMilliseconds}ms")
				}.value

			measureTimeMillis {
				requireNotNull(
					backupRepository.saveBackup(
						BackupEntity(
							base64Bytes
						)
					).unwrap()
				)
			}.let {
				println("Saved in ${it}ms")
			}
		}.join()
	}

	/**
	 * @param repos that must be added
	 * @param extensions is a tree to lower redundant data duplication
	 */
	@Serializable
	private data class FleshedBackupEntity(
		val repos: List<SimpleRepositoryEntity>,
		val extensions: List<SimpleExtensionEntity>,
	)

	@Serializable
	private data class SimpleRepositoryEntity(
		val url: String,
		val name: String,
	)

	/**
	 * Each extension that needs to be installed
	 * @param novels novels to add after word
	 */
	@Serializable
	private data class SimpleExtensionEntity(
		val id: Int,
		val novels: List<SimpleNovelEntity>,
	)

	@Serializable
	private data class SimpleNovelEntity(
		val url: String,
		val name: String,
		val imageURL: String,
		val chapters: List<SimpleChapterEntity>,
	)

	/**
	 * @param rS ReadingStatus
	 * @param rP Reading position
	 */
	@Serializable
	private data class SimpleChapterEntity(
		val url: String,
		val name: String,
		val bookmarked: Boolean,
		val rS: ReadingStatus,
		val rP: Int
	)
}