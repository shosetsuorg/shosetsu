package app.shosetsu.android.datasource.local.database.impl

import android.database.sqlite.SQLiteException
import app.shosetsu.android.datasource.local.database.base.IDBNovelSettingsDataSource
import app.shosetsu.android.domain.model.database.DBNovelSettingsEntity
import app.shosetsu.android.domain.model.local.NovelSettingEntity
import app.shosetsu.android.providers.database.dao.NovelSettingsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

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
 * 01 / 01 / 2021
 */
class DBNovelSettingsDataSource(
	private val dao: NovelSettingsDao
) : IDBNovelSettingsDataSource {

	override fun getFlow(novelID: Int): Flow<NovelSettingEntity?> = flow {
		try {
			emitAll(dao.getFlow(novelID).map { it?.convertTo() })
		} catch (e: SQLiteException) {
			throw e
		}
	}

	override suspend fun update(novelSettingEntity: NovelSettingEntity): Unit =
		try {
			(dao.update(novelSettingEntity.toDB()))
		} catch (e: SQLiteException) {
			throw e
		}

	override suspend fun get(novelID: Int): NovelSettingEntity? = try {
		dao.get(novelID)?.convertTo()
	} catch (e: SQLiteException) {
		throw e
	}

	override suspend fun insert(novelSettingEntity: NovelSettingEntity): Long =
		try {
			(dao.insertAbort(novelSettingEntity.toDB()))
		} catch (e: SQLiteException) {
			throw e
		}

	private fun NovelSettingEntity.toDB(): DBNovelSettingsEntity =
		DBNovelSettingsEntity(
			novelID,
			sortType,
			showOnlyReadingStatusOf,
			showOnlyBookmarked,
			showOnlyDownloaded,
			reverseOrder
		)
}

