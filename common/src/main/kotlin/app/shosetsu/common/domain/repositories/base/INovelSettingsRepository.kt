package app.shosetsu.common.domain.repositories.base

import app.shosetsu.common.domain.model.local.NovelSettingEntity
import app.shosetsu.common.dto.HResult
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
	 * @return
	 * [HResult.Success] when settings are either found or created
	 *
	 * [HResult.Error] Something went wrong
	 *
	 * [HResult.Empty] If no novel settings were found
	 */
	suspend fun get(novelID: Int): HResult<NovelSettingEntity>


	/**
	 * Gets the settings for a novel, but in a flow that will be updated with any changes
	 *
	 * @see get
	 */
	fun getFlow(novelID: Int): Flow<HResult<NovelSettingEntity>>


	/**
	 * Updates the settings for novels
	 */
	suspend fun update(novelSettingEntity: NovelSettingEntity): HResult<*>

	/**
	 * Inserts a new setting
	 */
	suspend fun insert(novelSettingEntity: NovelSettingEntity): HResult<*>
}