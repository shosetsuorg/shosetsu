package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.datasource.database.base.IDBNovelSettingsDataSource
import app.shosetsu.common.domain.model.local.NovelSettingEntity
import app.shosetsu.common.domain.repositories.base.INovelSettingsRepository
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
 * 20 / 01 / 2021
 */
class NovelSettingsRepository(
	private val idbNovelSettingsDataSource: IDBNovelSettingsDataSource
) : INovelSettingsRepository {
	override suspend fun getNovelSettings(novelID: Int): HResult<NovelSettingEntity> =
		idbNovelSettingsDataSource.getNovelSettings(novelID)


	override fun getNovelSettingsFlow(novelID: Int): Flow<HResult<NovelSettingEntity>> =
		idbNovelSettingsDataSource.getNovelSettingsFlow(novelID)

	override suspend fun updateNovelSettings(novelSettingEntity: NovelSettingEntity): HResult<*> =
		idbNovelSettingsDataSource.updateNovelSettings(novelSettingEntity)
}