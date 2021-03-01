package app.shosetsu.android.datasource.local.file.impl

import app.shosetsu.android.common.ext.logV
import app.shosetsu.common.consts.ErrorKeys
import app.shosetsu.common.datasource.file.base.IFileChapterDataSource
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.dto.*
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
	private fun makePath(ce: ChapterEntity): String =
		"/chapters/${ce.extensionID}/${ce.novelID}/${ce.id}.txt"

	override suspend fun saveChapterPassageToStorage(
		chapterEntity: ChapterEntity,
		chapterType: Novel.ChapterType,
		passage: String,
	): HResult<*> {
		val path = makePath(chapterEntity)
		return iFileSystemProvider.createDirectory(
			DOWNLOADS,
			path.substringBeforeLast("/")
		).transform {
			iFileSystemProvider.writeFile(
				DOWNLOADS,
				path,
				"${chapterType.key}\n$passage"
			)
		}
	}

	override suspend fun loadChapterPassageFromStorage(
		chapterEntity: ChapterEntity,
		chapterType: Novel.ChapterType,
	): HResult<String> =
		iFileSystemProvider.readFile(DOWNLOADS, makePath(chapterEntity)).transform { passage ->
			// This block of code uses a sequence to be as performance efficient as possible
			passage.lineSequence().firstOrNull()?.let { firstLine ->
				firstLine.toIntOrNull()?.let {
					if (it != chapterType.key)
						return@transform mismatchedChapterType
				} ?: return@transform mismatchedChapterType
			} ?: return@transform emptyResult()

			successResult(passage.replaceFirst("${chapterType.key}\n", ""))
		}

	override suspend fun deleteChapter(chapterEntity: ChapterEntity): HResult<*> =
		iFileSystemProvider.deleteFile(DOWNLOADS, makePath(chapterEntity))

	companion object {
		private val mismatchedChapterType by lazy {
			errorResult(
				ErrorKeys.MISMATCHED_CHAPTER_TYPE,
				"File chapter not of expected type"
			)
		}

	}
}