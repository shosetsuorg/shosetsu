package app.shosetsu.android.domain.repository.impl

import app.shosetsu.android.common.GenericSQLiteException
import app.shosetsu.android.datasource.local.database.base.IDBNovelReaderSettingsDataSource
import app.shosetsu.common.domain.model.local.NovelReaderSettingEntity
import app.shosetsu.android.domain.repository.base.INovelReaderSettingsRepository
import kotlinx.coroutines.flow.Flow

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
 * 24 / 02 / 2021
 */
class NovelReaderSettingsRepository(
	private val database: IDBNovelReaderSettingsDataSource
) : INovelReaderSettingsRepository {

	@Throws(GenericSQLiteException::class)
	override suspend fun get(novelID: Int): NovelReaderSettingEntity? =
		database.get(novelID)

	override fun getFlow(novelID: Int): Flow<NovelReaderSettingEntity?> =
		database.getFlow(novelID)

	@Throws(GenericSQLiteException::class)
	override suspend fun insert(novelReaderSettingEntity: NovelReaderSettingEntity): Long =
		database.insert(novelReaderSettingEntity)

	@Throws(GenericSQLiteException::class)
	override suspend fun update(novelReaderSettingEntity: NovelReaderSettingEntity): Unit =
		database.update(novelReaderSettingEntity)
}