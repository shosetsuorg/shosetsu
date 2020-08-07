package com.github.doomsdayrs.apps.shosetsu.providers.database.dao

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.*
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
	fun loadBookmarkedNovels(): List<NovelEntity>

	@Query("SELECT * FROM novels WHERE bookmarked = 1")
	fun loadListBookmarkedNovels(): LiveData<List<NovelEntity>>

	@Query("SELECT * FROM novels WHERE id = :novelID LIMIT 1")
	fun loadNovel(novelID: Int): NovelEntity

	@Query("SELECT * FROM novels WHERE id = :novelID LIMIT 1")
	fun loadNovelLive(novelID: Int): LiveData<NovelEntity>

	@Query("SELECT url,imageURL,title FROM novels WHERE id = :novelID LIMIT 1")
	fun loadURLImageTitle(novelID: Int): URLImageTitle

	@Query("SELECT id,title,imageURL FROM novels")
	fun loadIDImageTitle(): LiveData<List<IDTitleImage>>

	@Query("SELECT novels.id, novels.title, novels.imageURL, novels.bookmarked,  ( " +
			"SELECT count(*) FROM chapters WHERE novelID = novels.id AND readingStatus != 2 " +
			") as unread FROM novels WHERE novels.bookmarked = 1")
	fun loadBookmarkedNovelsCount(): LiveData<List<BookmarkedNovelEntity>>

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

	@Query("SELECT * FROM novels WHERE _rowid_ = :rowID LIMIT 1")
	fun loadNovel(rowID: Long): NovelEntity

	@Query("SELECT id,title,imageURL,bookmarked FROM novels WHERE _rowid_ = :rowID LIMIT 1")
	fun loadIDTitleImageBook(rowID: Long): IDTitleImageBook


	@Query("SELECT id,title,imageURL,bookmarked FROM novels WHERE id = :id LIMIT 1")
	fun loadIDTitleImageBook(id: Int): IDTitleImageBook


	@Query("SELECT COUNT(*),id FROM novels WHERE url = :novelURL")
	fun loadCountByURL(novelURL: String): CountIDTuple


	fun hasNovel(novelURL: String): BooleanChapterIDTuple {
		val n = loadCountByURL(novelURL)
		return BooleanChapterIDTuple(n.count > 0, n.id)
	}

	@Transaction
	suspend fun insertNovelReturnCard(novelEntity: NovelEntity): IDTitleImageBook {
		Log.d(logID(), "insertNovelReturnCard $novelEntity")
		val has = hasNovel(novelEntity.url)
		return if (has.boolean) {
			loadIDTitleImageBook(has.id)
		} else {
			val rowID = insertAbort(novelEntity)
			Log.d(logID(), "insertNovelReturnCard got rowID $rowID")
			loadIDTitleImageBook(rowID)
		}
	}

	@Transaction
	suspend fun insertAndReturn(novelEntity: NovelEntity): NovelEntity =
			loadNovel(insertIgnore(novelEntity))

	@Query("UPDATE novels SET bookmarked = :bookmarked WHERE id = :novelID")
	suspend fun setNovelBookmark(novelID: Int, bookmarked: Int)

	@Transaction
	suspend fun updateBookmarked(list: List<BookmarkedNovelEntity>) {
		list.forEach { bookMarked ->
			blockingUpdate(loadNovel(bookMarked.id).copy(
					bookmarked = bookMarked.bookmarked
			))
		}
	}

	//@Query("SELECT * FROM novels WHERE id = :novelID LIMIT 1")
	//fun loadNovelWithChapters(novelID: Int): NovelEntityWithChapters
}