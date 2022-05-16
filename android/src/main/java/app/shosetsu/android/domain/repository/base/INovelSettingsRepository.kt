package app.shosetsu.android.domain.repository.base

import android.database.sqlite.SQLiteException
import app.shosetsu.android.domain.model.local.NovelSettingEntity
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
 * 27 / 12 / 2020
 *
 * Each novels may have their own specific preferences that users may like
 * Such as sort types
 */
interface INovelSettingsRepository {

	/**
	 * Loads up the settings for a novel
	 * Should generate settings if none present
	 */
	@Throws(SQLiteException::class)
	suspend fun get(novelID: Int): NovelSettingEntity?


	/**
	 * Gets the settings for a novel, but in a flow that will be updated with any changes
	 */
	fun getFlow(novelID: Int): Flow<NovelSettingEntity?>


	/**
	 * Updates the settings for novels
	 */
	@Throws(SQLiteException::class)
	suspend fun update(novelSettingEntity: NovelSettingEntity)

	/**
	 * Inserts a new setting
	 */
	@Throws(SQLiteException::class)
	suspend fun insert(novelSettingEntity: NovelSettingEntity): Long
}