package app.shosetsu.android.datasource.local.database.impl

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.domain.model.database.DBNovelSettingsEntity
import app.shosetsu.android.providers.database.dao.NovelSettingsDao
import app.shosetsu.common.datasource.database.base.IDBNovelSettingsDataSource
import app.shosetsu.common.domain.model.local.NovelSettingEntity
import app.shosetsu.common.dto.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
 * 01 / 01 / 2021
 */
class DBNovelSettingsDataSource(
	private val dao: NovelSettingsDao
) : IDBNovelSettingsDataSource {

	@ExperimentalCoroutinesApi
	override fun getNovelSettingsFlow(novelID: Int): Flow<HResult<NovelSettingEntity>> =
		dao.getFlow(novelID).mapLatestTo().mapLatestToSuccess()

	override suspend fun updateNovelSettings(novelSettingEntity: NovelSettingEntity): HResult<*> =
		try {
			successResult(dao.update(novelSettingEntity.toDB()))
		} catch (e: SQLiteException) {
			errorResult(e)
		}

	override suspend fun getNovelSettings(novelID: Int): HResult<NovelSettingEntity> = try {
		dao.get(novelID).convert()
	} catch (e: SQLiteException) {
		errorResult(e)
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

