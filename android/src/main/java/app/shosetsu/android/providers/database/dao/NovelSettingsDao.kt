package app.shosetsu.android.providers.database.dao

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Query
import app.shosetsu.android.domain.model.database.DBNovelSettingsEntity
import app.shosetsu.android.providers.database.dao.base.BaseDao
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
 * 02 / 01 / 2021
 */
@Dao
interface NovelSettingsDao : BaseDao<DBNovelSettingsEntity> {

	@Query("SELECT * FROM novel_settings WHERE novelID == :novelID LIMIT 1")
	@Throws(SQLiteException::class)
	fun getFlow(novelID: Int): Flow<DBNovelSettingsEntity?>

	@Query("SELECT * FROM novel_settings WHERE novelID == :novelID LIMIT 1")
	@Throws(SQLiteException::class)
	suspend fun get(novelID: Int): DBNovelSettingsEntity?
}