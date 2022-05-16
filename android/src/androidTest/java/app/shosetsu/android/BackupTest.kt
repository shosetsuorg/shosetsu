package app.shosetsu.android

import android.content.Context
import android.util.Base64
import androidx.test.platform.app.InstrumentationRegistry
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.common.utils.backupJSON
import app.shosetsu.android.domain.model.local.BackupEntity
import app.shosetsu.android.domain.model.local.backup.*
import app.shosetsu.android.domain.repository.base.IBackupRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import kotlinx.serialization.encodeToString
import org.junit.Test
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.random.Random
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
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
class BackupTest : DIAware {

	private val context: Context by lazy {
		InstrumentationRegistry.getInstrumentation().targetContext
	}

	override val di: DI by closestDI(context)

	private val backupRepository by instance<IBackupRepository>()

	private val randomInt
		get() = Random.nextInt() % 100 + 10

	private val randomLong
		get() = Random.nextLong() % 10000

	private val randomString
		get() = randomLong.toString()

	private fun randomRepositories() = ArrayList<BackupRepositoryEntity>().apply {
		for (i in 0 until randomInt) {
			add(
				BackupRepositoryEntity(
					randomString, randomString
				)
			)
		}
	}

	private fun randomChapters() = ArrayList<BackupChapterEntity>().apply {
		for (i in 0 until randomInt) {
			add(
				BackupChapterEntity(
					randomString, randomString,
					Random.nextBoolean(),
					ReadingStatus.fromInt(Random.nextInt() % 3 + 1),
					randomInt.toDouble()
				)
			)
		}
	}

	private fun randomNovels() = ArrayList<BackupNovelEntity>().apply {
		for (i in 0 until randomInt) {
			add(
				BackupNovelEntity(
					randomString,
					randomString,
					randomString,
					randomChapters()
				)
			)
		}
	}

	private fun randomExtensions() = ArrayList<BackupExtensionEntity>().apply {
		for (i in 0 until randomInt) {
			add(
				BackupExtensionEntity(
					randomInt,
					randomNovels()
				)
			)
		}
	}

	private val backup by lazy {
		FleshedBackupEntity(
			repos = randomRepositories(),
			extensions = randomExtensions()
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
				println("Created backup randomly in ${it.duration.toDouble(DurationUnit.MILLISECONDS)}ms")
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

			val stringBackup = measureTimedValue { backupJSON.encodeToString(backup) }.also {
				println("Serialized backup in ${it.duration.toDouble(DurationUnit.MILLISECONDS)}ms")
			}.value

			val zippedBytes = measureTimedValue { gzip(stringBackup) }.also {
				println("Zipped backup in ${it.duration.toDouble(DurationUnit.MILLISECONDS)}ms")
			}.value

			val base64Bytes =
				measureTimedValue { Base64.encode(zippedBytes, Base64.DEFAULT) }.also {
					println("Base64 backup in ${it.duration.toDouble(DurationUnit.MILLISECONDS)}ms")
				}.value

			measureTimeMillis {
				requireNotNull(
					backupRepository.saveBackup(
						BackupEntity(
							base64Bytes
						)
					)
				)
			}.let {
				println("Saved in ${it}ms")
			}
		}.join()
	}


}