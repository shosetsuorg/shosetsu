package app.shosetsu.android.datasource.local.database.impl

import android.database.sqlite.SQLiteException
import app.shosetsu.android.domain.model.database.DBNovelReaderSettingEntity
import app.shosetsu.android.providers.database.dao.NovelReaderSettingsDao
import app.shosetsu.common.GenericSQLiteException
import app.shosetsu.common.datasource.database.base.IDBNovelReaderSettingsDataSource
import app.shosetsu.common.domain.model.local.NovelReaderSettingEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * 26 / 02 / 2021
 */
class DBNovelReaderSettingsDataSource(
	private val dao: NovelReaderSettingsDao
) : IDBNovelReaderSettingsDataSource {
	override suspend fun get(novelID: Int): NovelReaderSettingEntity? = try {
		dao.get(novelID)
	} catch (e: SQLiteException) {
		throw GenericSQLiteException(e)
	}

	@ExperimentalCoroutinesApi
	override fun getFlow(novelID: Int): Flow<NovelReaderSettingEntity?> = flow {
		try {
			emitAll(dao.getFlow(novelID).mapLatest { it?.convertTo() })
		} catch (e: SQLiteException) {
			throw GenericSQLiteException(e)
		}
	}

	override suspend fun insert(novelReaderSettingEntity: NovelReaderSettingEntity): Long =
		try {
			(dao.insertAbort(novelReaderSettingEntity.toDB()))
		} catch (e: SQLiteException) {
			throw GenericSQLiteException(e)
		}

	override suspend fun update(novelReaderSettingEntity: NovelReaderSettingEntity): Unit =
		try {
			(dao.update(novelReaderSettingEntity.toDB()))
		} catch (e: SQLiteException) {
			throw GenericSQLiteException(e)
		}

	fun NovelReaderSettingEntity.toDB() =
		DBNovelReaderSettingEntity(
			novelID = novelID,
			paragraphIndentSize = paragraphIndentSize,
			paragraphSpacingSize = paragraphSpacingSize
		)
}