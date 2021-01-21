package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.domain.model.local.NovelSettingEntity
import app.shosetsu.common.domain.repositories.base.INovelSettingsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.emptyResult
import app.shosetsu.common.dto.successResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

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
class TempNovelSettingsRepository : INovelSettingsRepository {
	val values = HashMap<Int, MutableStateFlow<HResult<NovelSettingEntity>>>()


	override suspend fun getNovelSettings(novelID: Int): HResult<NovelSettingEntity> =
		values[novelID]?.value ?: emptyResult().also {
			insertMissing(novelID)
		}


	private fun insertMissing(novelID: Int) {
		values[novelID] = MutableStateFlow(emptyResult())
	}

	override fun getNovelSettingsFlow(novelID: Int): Flow<HResult<NovelSettingEntity>> =
		values[novelID] ?: insertMissing(novelID).let { values[novelID]!! }

	override suspend fun updateNovelSettings(novelSettingEntity: NovelSettingEntity): HResult<*> =
		if (values.containsKey(novelSettingEntity.novelID)) {
			successResult(
				values[novelSettingEntity.novelID]?.emit(
					successResult(
						novelSettingEntity
					)
				)
			)
		} else {
			insertMissing(novelSettingEntity.novelID)
			emptyResult()
		}
}