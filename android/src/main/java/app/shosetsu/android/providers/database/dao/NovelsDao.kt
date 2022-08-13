package app.shosetsu.android.providers.database.dao

import android.database.sqlite.SQLiteException
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import app.shosetsu.android.domain.model.database.DBNovelEntity
import app.shosetsu.android.domain.model.database.DBStrippedNovelEntity
import app.shosetsu.android.domain.model.local.LibraryNovelEntity
import app.shosetsu.android.domain.model.local.StrippedBookmarkedNovelEntity
import app.shosetsu.android.providers.database.dao.base.BaseDao
import kotlinx.coroutines.flow.Flow

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
interface NovelsDao : BaseDao<DBNovelEntity> {
	@Throws(SQLiteException::class)
	@Query("SELECT * FROM novels WHERE bookmarked = 1")
	fun loadBookmarkedNovels(): List<DBNovelEntity>

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM novels WHERE bookmarked = 1")
	fun loadBookMarkedNovelsFlow(): Flow<List<DBNovelEntity>>

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM novels WHERE id = :novelID LIMIT 1")
	fun getNovel(novelID: Int): DBNovelEntity?

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM novels WHERE id = :novelID LIMIT 1")
	fun getNovelFlow(novelID: Int): Flow<DBNovelEntity?>

	@Throws(SQLiteException::class)
	@Query(
		"""
		SELECT M.*, COALESCE(MC.categoryID, 0) AS category
		FROM (
			SELECT novels.id,
				novels.title,
				novels.imageURL,
				novels.bookmarked,
				(
					SELECT count(*)
					FROM chapters
					WHERE novelID = novels.id
					AND readingStatus != 2
				) as unread,
				novels.genres,
				novels.authors,
				novels.artists,
				novels.tags,
				novels.status
			  FROM novels
			  WHERE novels.bookmarked = 1
		) AS M
		LEFT JOIN (
			SELECT *
			FROM novel_categories
		) AS MC
		ON M.id = MC.novelID
		"""
	)
	fun loadBookmarkedNovelsFlow(): Flow<List<LibraryNovelEntity>>

	@Throws(SQLiteException::class)
	@Query("SELECT id,title,imageURL,bookmarked FROM novels WHERE _rowid_ = :rowID LIMIT 1")
	fun loadDBStrippedNovelEntityViaRow(rowID: Long): DBStrippedNovelEntity?

	@Throws(SQLiteException::class)
	@Query("SELECT id,title,imageURL,bookmarked FROM novels WHERE id = :id LIMIT 1")
	fun loadDBStrippedNovelEntity(id: Int): DBStrippedNovelEntity?

	@Query("SELECT id FROM novels WHERE url = :novelURL AND formatterID = :extensionID LIMIT 1")
	fun loadNovelID(novelURL: String, extensionID: Int): Int?

	@Transaction
	@Throws(SQLiteException::class)
	suspend fun insertReturnStripped(entity: DBNovelEntity): DBStrippedNovelEntity? =
		loadNovelID(entity.url, entity.extensionID)?.let { id ->
			loadDBStrippedNovelEntity(id)
		} ?: loadDBStrippedNovelEntityViaRow(insertAbort(entity))

	@Transaction
	@Throws(SQLiteException::class)
	suspend fun update(list: List<LibraryNovelEntity>) {
		list.forEach { bookMarked ->
			getNovel(bookMarked.id)?.copy(
				bookmarked = bookMarked.bookmarked
			)?.let {
				update(it)
			}
		}
	}

	@Throws(SQLiteException::class)
	@Query("DELETE FROM novels WHERE bookmarked = 0")
	fun clearUnBookmarkedNovels()

	@Query("SELECT * FROM novels")
	fun loadNovels(): List<DBNovelEntity>

	@Query("SELECT id, title, imageURL FROM novels WHERE title like '%'||:query||'%' AND novels.bookmarked = 1")
	fun searchBookmarked(query: String): PagingSource<Int, StrippedBookmarkedNovelEntity>

	//@Query("SELECT * FROM novels WHERE id = :novelID LIMIT 1")
	//fun loadNovelWithChapters(novelID: Int): DBNovelWithChapters
}