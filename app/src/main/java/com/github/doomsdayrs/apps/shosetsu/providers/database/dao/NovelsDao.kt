package com.github.doomsdayrs.apps.shosetsu.providers.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDTitleImage
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.URLImageTitle
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.base.BaseDao

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
 * ====================================================================
 */

/**
 * shosetsu
 * 23 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@Dao
interface NovelsDao : BaseDao<NovelEntity> {
	@Query("SELECT * FROM novels")
	fun loadNovels(): LiveData<List<NovelEntity>>

	@Query("SELECT * FROM novels WHERE bookmarked = 1")
	fun loadBookmarkedNovels(): LiveData<List<NovelEntity>>

	@Query("SELECT * FROM novels WHERE id = :novelID LIMIT 1")
	fun loadNovel(novelID: Int): NovelEntity

	@Query("SELECT url,imageURL,title FROM novels WHERE id = :novelID LIMIT 1")
	fun loadURLImageTitle(novelID: Int): URLImageTitle

	@Query("SELECT id,title,imageURL FROM novels")
	fun loadIDImageTitle(): LiveData<List<IDTitleImage>>

	@Query("SELECT id FROM novels")
	fun loadBookmarkedIDs(): List<Int>

	@Transaction
	suspend fun unBookmarkNovels(selectedNovels: List<Int>, entities: List<NovelEntity>) {
		selectedNovels.forEach { targetID ->
			entities.find { it.id == targetID }?.let { novelEntity ->
				novelEntity.bookmarked = false
				suspendedUpdate(novelEntity)
			}
		}
	}

	//@Query("SELECT * FROM novels WHERE id = :novelID LIMIT 1")
	//fun loadNovelWithChapters(novelID: Int): NovelEntityWithChapters
}