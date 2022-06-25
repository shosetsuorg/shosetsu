package app.shosetsu.android.datasource.local.database.impl

import androidx.paging.PagingSource
import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.datasource.local.database.base.IDBNovelsDataSource
import app.shosetsu.android.domain.model.local.LibraryNovelEntity
import app.shosetsu.android.domain.model.local.NovelEntity
import app.shosetsu.android.domain.model.local.StrippedBookmarkedNovelEntity
import app.shosetsu.android.domain.model.local.StrippedNovelEntity
import app.shosetsu.android.dto.convertList
import app.shosetsu.android.providers.database.dao.NovelsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
class DBNovelsDataSource(
	private val novelsDao: NovelsDao,
) : IDBNovelsDataSource {
	override suspend fun loadBookmarkedNovels(): List<NovelEntity> =
		novelsDao.loadBookmarkedNovels().convertList()

	override fun loadBookmarkedNovelsFlow(
	): Flow<List<LibraryNovelEntity>> = novelsDao.loadBookmarkedNovelsFlow()

	override suspend fun getNovel(novelID: Int): NovelEntity? =
		novelsDao.getNovel(novelID)?.convertTo()

	override suspend fun getNovelFlow(novelID: Int): Flow<NovelEntity?> =
		novelsDao.getNovelFlow(novelID).map { it?.convertTo() }

	override suspend fun update(novelEntity: NovelEntity): Unit =
		(novelsDao.update(novelEntity.toDB()))

	override suspend fun update(
		list: List<LibraryNovelEntity>
	): Unit =
		(novelsDao.update(list))

	override suspend fun insertReturnStripped(
		novelEntity: NovelEntity,
	): StrippedNovelEntity? =
		novelsDao.insertReturnStripped(novelEntity.toDB())?.convertTo()

	override suspend fun insert(novelEntity: NovelEntity): Long =
		(novelsDao.insertAbort(novelEntity.toDB()))

	override suspend fun clearUnBookmarkedNovels(): Unit =
		(novelsDao.clearUnBookmarkedNovels())

	override fun loadNovels(): List<NovelEntity> =
		(novelsDao.loadNovels().convertList())

	override fun searchBookmarked(query: String): PagingSource<Int, StrippedBookmarkedNovelEntity> =
			novelsDao.searchBookmarked(query)
}