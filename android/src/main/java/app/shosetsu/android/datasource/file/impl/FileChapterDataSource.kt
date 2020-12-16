package app.shosetsu.android.datasource.file.impl

import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.providers.file.base.IFileSystemProvider
import app.shosetsu.common.consts.ErrorKeys.ERROR_NOT_FOUND
import app.shosetsu.common.datasource.file.base.IFileChapterDataSource
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.dto.*
import app.shosetsu.common.enums.InternalFileDir.FILES
import java.io.File
import java.io.FileNotFoundException

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
		iFileSystemProvider.createInternalDirectory(FILES, "/download/").handle(
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
		"/download/${ce.extensionID}/${ce.novelID}/${ce.id}.txt"

	override suspend fun saveChapterPassageToStorage(
		chapterEntity: ChapterEntity,
		passage: String,
	): HResult<*> {
		val path = makePath(chapterEntity)
		return iFileSystemProvider.createInternalDirectory(
			FILES,
			path.substringBeforeLast("/")
		).transform {
			iFileSystemProvider.writeInternalFile(
				FILES,
				path,
				passage
			)
		}
	}


	override suspend fun loadChapterPassageFromStorage(chapterEntity: ChapterEntity): HResult<String> =
		try {
			iFileSystemProvider.readInternalFile(FILES, makePath(chapterEntity))
		} catch (e: FileNotFoundException) {
			emptyResult()
		} catch (e: Exception) {
			e.toHError()
		}

	override suspend fun deleteChapter(chapterEntity: ChapterEntity): HResult<*> {
		File(makePath(chapterEntity)).takeIf { it.exists() }?.delete()
			?: return errorResult(ERROR_NOT_FOUND, "Chapter not found")
		return successResult("")
	}
}