package app.shosetsu.android.datasource.local.database.impl

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.domain.model.database.DBNovelReaderSettingEntity
import app.shosetsu.android.providers.database.dao.NovelReaderSettingsDao
import app.shosetsu.common.datasource.database.base.IDBNovelReaderSettingsDataSource
import app.shosetsu.common.domain.model.local.NovelReaderSettingEntity
import app.shosetsu.common.dto.HFlow
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.emptyResult
import app.shosetsu.common.dto.successResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
	override suspend fun get(novelID: Int): HResult<NovelReaderSettingEntity> = try {
		dao.get(novelID)?.let {
			successResult(it)
		} ?: emptyResult()
	} catch (e: Exception) {
		e.toHError()
	}

	@ExperimentalCoroutinesApi
	override fun getFlow(novelID: Int): HFlow<NovelReaderSettingEntity> = flow {
		try {
			emitAll(dao.getFlow(novelID).mapLatest { it?.convertTo() }
				.mapLatest { it?.let { successResult(it) } ?: emptyResult() })
		} catch (e: SQLiteException) {
			emit(e.toHError())
		}
	}


	override suspend fun insert(novelReaderSettingEntity: NovelReaderSettingEntity): HResult<*> =
		try {
			successResult(dao.insertAbort(novelReaderSettingEntity.toDB()))
		} catch (e: SQLiteException) {
			e.toHError()
		}

	override suspend fun update(novelReaderSettingEntity: NovelReaderSettingEntity): HResult<*> =
		try {
			successResult(dao.update(novelReaderSettingEntity.toDB()))
		} catch (e: SQLiteException) {
			e.toHError()
		}

	fun NovelReaderSettingEntity.toDB() =
		DBNovelReaderSettingEntity(
			novelID = novelID,
			paragraphIndentSize = paragraphIndentSize,
			paragraphSpacingSize = paragraphSpacingSize
		)
}