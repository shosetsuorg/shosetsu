package com.github.doomsdayrs.apps.shosetsu.domain.repository.base

import androidx.lifecycle.LiveData
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ChapterEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity

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
 * 30 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface IChaptersRepository {
	/**
	 * Loads the chapter
	 * First checks if it is in cache
	 * Then checks the file system
	 * Then loads the chapter from the internet
	 */
	suspend fun loadChapterPassage(
			formatter: Formatter,
			chapterEntity: ChapterEntity
	): HResult<String>

	/**
	 * Save the chapter to memory
	 */
	suspend fun saveChapterPassageToMemory(chapterEntity: ChapterEntity, passage: String)

	/**
	 * Save the chapter to storage
	 */
	suspend fun saveChapterPassageToStorage(chapterEntity: ChapterEntity, passage: String)

	/**
	 * Loads count of unread chapters
	 */
	suspend fun loadChapterUnreadCount(novelID: Int): LiveData<HResult<Int>>


	/**
	 * Handles chapters for ze novel
	 */
	suspend fun handleChapters(novelEntity: NovelEntity, list: List<Novel.Chapter>)
}