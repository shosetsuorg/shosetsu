package app.shosetsu.android.datasource.local.database.impl

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.domain.model.database.DBNovelSettingsEntity
import app.shosetsu.android.providers.database.dao.NovelSettingsDao
import app.shosetsu.common.datasource.database.base.IDBNovelSettingsDataSource
import app.shosetsu.common.domain.model.local.NovelSettingEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.emptyResult
import app.shosetsu.common.dto.successResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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

	@ExperimentalCoroutinesApi
	override fun getFlow(novelID: Int): Flow<HResult<NovelSettingEntity>> =
		dao.getFlow(novelID).map {
			it?.let { successResult(it.convertTo()) } ?: emptyResult()
		}.catch { throwable ->
			(throwable as? Exception)?.toHError()?.let { emit(it) }
		}


	override suspend fun update(novelSettingEntity: NovelSettingEntity): HResult<*> =
		try {
			successResult(dao.update(novelSettingEntity.toDB()))
		} catch (e: SQLiteException) {
			errorResult(e)
		}

	override suspend fun get(novelID: Int): HResult<NovelSettingEntity> = try {
		successResult(dao.get(novelID).convertTo())
	} catch (e: SQLiteException) {
		errorResult(e)
	}

	override suspend fun insert(novelSettingEntity: NovelSettingEntity): HResult<*> =
		try {
			successResult(dao.insertAbort(novelSettingEntity.toDB()))
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

