package com.github.doomsdayrs.apps.shosetsu.datasource.local.base

import androidx.lifecycle.LiveData
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ChapterEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ReaderChapterEntity

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
 * 04 / 05 / 2020
 */
interface ILocalChaptersDataSource {
	/**
	 * Get the chapters of a novel
	 */
	fun loadChapters(novelID: Int): LiveData<HResult<List<ChapterEntity>>>

	fun loadReaderChapters(novelID: Int): LiveData<HResult<List<ReaderChapterEntity>>>

	suspend fun handleChapters(novelEntity: NovelEntity, list: List<Novel.Chapter>)

	suspend fun updateChapter(chapterEntity: ChapterEntity)

	suspend fun updateReaderChapter(readerChapterEntity: ReaderChapterEntity)
}