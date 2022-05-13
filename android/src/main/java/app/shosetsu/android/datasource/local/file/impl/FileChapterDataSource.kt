package app.shosetsu.android.datasource.local.file.impl

import app.shosetsu.android.common.FileNotFoundException
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.common.enums.ExternalFileDir.DOWNLOADS
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.datasource.file.base.IFileChapterDataSource
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.providers.file.base.IFileSystemProvider
import app.shosetsu.lib.Novel
import java.io.IOException

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
 * 12 / 05 / 2020
 */
class FileChapterDataSource(
	private val iFileSystemProvider: IFileSystemProvider
) : IFileChapterDataSource {
	init {
		logV("Creating required directories")

		try {
			iFileSystemProvider.createDirectory(DOWNLOADS, "chapters")
			logV("Created required directories")
		} catch (e: Exception) {
			logV("Error on creation of directories", e)
		}

	}

	/** Makes path */
	private fun makePath(ce: ChapterEntity, chapterType: Novel.ChapterType): String =
		"/chapters/${ce.extensionID}/${ce.novelID}/${ce.id}.${chapterType.fileExtension}"

	@Throws(FilePermissionException::class, IOException::class)
	override suspend fun save(
		chapterEntity: ChapterEntity,
		chapterType: Novel.ChapterType,
		passage: ByteArray,
	) {
		val path = makePath(chapterEntity, chapterType)
		iFileSystemProvider.createDirectory(
			DOWNLOADS,
			path.substringBeforeLast("/")
		)

		iFileSystemProvider.writeFile(
			DOWNLOADS,
			path,
			passage
		)
	}

	@Throws(FilePermissionException::class, FileNotFoundException::class)
	override suspend fun load(
		chapterEntity: ChapterEntity,
		chapterType: Novel.ChapterType,
	): ByteArray =
		iFileSystemProvider.readFile(DOWNLOADS, makePath(chapterEntity, chapterType))

	@Throws(FilePermissionException::class)
	override suspend fun delete(
		chapterEntity: ChapterEntity,
		chapterType: Novel.ChapterType
	) {
		iFileSystemProvider.deleteFile(DOWNLOADS, makePath(chapterEntity, chapterType))
	}
}