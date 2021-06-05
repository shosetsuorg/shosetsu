package app.shosetsu.android.datasource.local.file.impl

import app.shosetsu.android.common.ext.logV
import app.shosetsu.common.datasource.file.base.IFileChapterDataSource
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.transform
import app.shosetsu.common.enums.ExternalFileDir.DOWNLOADS
import app.shosetsu.common.providers.file.base.IFileSystemProvider
import app.shosetsu.lib.Novel

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
		iFileSystemProvider.createDirectory(DOWNLOADS, "chapters").handle(
			onError = {
				logV("Error on creation of directories $it")
			},
			onSuccess = {
				logV("Created required directories")
			}
		)
	}

	/** Makes path */
	private fun makePath(ce: ChapterEntity, chapterType: Novel.ChapterType): String =
		"/chapters/${ce.extensionID}/${ce.novelID}/${ce.id}.${chapterType.fileExtension}"

	override suspend fun save(
		chapterEntity: ChapterEntity,
		chapterType: Novel.ChapterType,
		passage: ByteArray,
	): HResult<*> {
		val path = makePath(chapterEntity, chapterType)
		return iFileSystemProvider.createDirectory(
			DOWNLOADS,
			path.substringBeforeLast("/")
		).transform {
			iFileSystemProvider.writeFile(
				DOWNLOADS,
				path,
				passage
			)
		}
	}

	override suspend fun load(
		chapterEntity: ChapterEntity,
		chapterType: Novel.ChapterType,
	): HResult<ByteArray> =
		iFileSystemProvider.readFile(DOWNLOADS, makePath(chapterEntity, chapterType))

	override suspend fun delete(
		chapterEntity: ChapterEntity,
		chapterType: Novel.ChapterType
	): HResult<*> =
		iFileSystemProvider.deleteFile(DOWNLOADS, makePath(chapterEntity, chapterType))

}