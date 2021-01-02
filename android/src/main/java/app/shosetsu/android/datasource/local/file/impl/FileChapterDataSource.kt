package app.shosetsu.android.datasource.local.file.impl

import app.shosetsu.android.common.ext.logV
import app.shosetsu.common.providers.file.base.IFileSystemProvider
import app.shosetsu.common.datasource.file.base.IFileChapterDataSource
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.transform
import app.shosetsu.common.enums.ExternalFileDir.DOWNLOADS

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
		iFileSystemProvider.createExternalDirectory(DOWNLOADS, "chapters").handle(
			onError = {
				logV("Error on creation of directories $it")
			},
			onSuccess = {
				logV("Created required directories")
			}
		)
	}


	/** Makes path */
	private fun makePath(ce: ChapterEntity): String =
		"/chapters/${ce.extensionID}/${ce.novelID}/${ce.id}.txt"

	override suspend fun saveChapterPassageToStorage(
		chapterEntity: ChapterEntity,
		passage: String,
	): HResult<*> {
		val path = makePath(chapterEntity)
		return iFileSystemProvider.createExternalDirectory(
			DOWNLOADS,
			path.substringBeforeLast("/")
		).transform {
			iFileSystemProvider.writeExternalFile(
				DOWNLOADS,
				path,
				passage
			)
		}
	}

	override suspend fun loadChapterPassageFromStorage(chapterEntity: ChapterEntity): HResult<String> =
		iFileSystemProvider.readExternalFile(DOWNLOADS, makePath(chapterEntity))

	override suspend fun deleteChapter(chapterEntity: ChapterEntity): HResult<*> =
		iFileSystemProvider.deleteExternalFile(DOWNLOADS, makePath(chapterEntity))
}