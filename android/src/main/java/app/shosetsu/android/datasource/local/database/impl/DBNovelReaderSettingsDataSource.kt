package app.shosetsu.android.datasource.local.database.impl

import app.shosetsu.android.datasource.local.database.base.IDBNovelReaderSettingsDataSource
import app.shosetsu.android.domain.model.database.DBNovelReaderSettingEntity
import app.shosetsu.android.domain.model.local.NovelReaderSettingEntity
import app.shosetsu.android.providers.database.dao.NovelReaderSettingsDao
import kotlinx.coroutines.flow.Flow
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
	override suspend fun get(novelID: Int): NovelReaderSettingEntity? =
		dao.get(novelID)

	override fun getFlow(novelID: Int): Flow<NovelReaderSettingEntity?> =
		dao.getFlow(novelID).mapLatest { it?.convertTo() }


	override suspend fun insert(novelReaderSettingEntity: NovelReaderSettingEntity): Long =
		(dao.insertAbort(novelReaderSettingEntity.toDB()))


	override suspend fun update(novelReaderSettingEntity: NovelReaderSettingEntity): Unit =
		(dao.update(novelReaderSettingEntity.toDB()))


	fun NovelReaderSettingEntity.toDB() =
		DBNovelReaderSettingEntity(
			novelID = novelID,
			paragraphIndentSize = paragraphIndentSize,
			paragraphSpacingSize = paragraphSpacingSize
		)
}