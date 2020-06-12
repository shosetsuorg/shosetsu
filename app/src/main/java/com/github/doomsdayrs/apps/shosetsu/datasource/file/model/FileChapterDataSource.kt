package com.github.doomsdayrs.apps.shosetsu.datasource.file.model

import android.content.Context
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys.ERROR_GENERAL
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys.ERROR_NOT_FOUND
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.errorResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.datasource.file.base.IFileChapterDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ChapterEntity
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
 * @param context Context of application
 */
class FileChapterDataSource(val context: Context) : IFileChapterDataSource {
	private val ap = context.getExternalFilesDir(null)!!.absolutePath

	/** Makes path */
	fun makePath(ce: ChapterEntity): String =
			"$ap/download/${ce.formatterID}/${ce.novelID}/${ce.id}.txt"

	override fun saveChapterPassageToStorage(chapterEntity: ChapterEntity, passage: String): Unit =
			File(makePath(chapterEntity)).writeText(passage)

	override fun loadChapterPassageFromStorage(chapterEntity: ChapterEntity): HResult<String> =
			try {
				successResult(File(makePath(chapterEntity)).readText())
			} catch (e: FileNotFoundException) {
				errorResult(ERROR_NOT_FOUND, e.message ?: "UNKNOWN MESSAGE")
			} catch (e: Exception) {
				errorResult(ERROR_GENERAL, e.message ?: "UNKNOWN MESSAGE")
			}
}