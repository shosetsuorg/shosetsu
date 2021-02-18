package app.shosetsu.android.domain.usecases.get

import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.utils.uifactory.NovelSettingConversionFactory
import app.shosetsu.common.domain.model.local.NovelSettingEntity
import app.shosetsu.common.domain.repositories.base.INovelSettingsRepository
import app.shosetsu.common.domain.repositories.base.INovelsRepository
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.emptyResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform
import app.shosetsu.common.view.uimodel.NovelSettingUI
import kotlinx.coroutines.flow.Flow
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
 * 29 / 12 / 2020
 *
 * Gets a novel setting flow, but will create a
 */
class GetNovelSettingFlowUseCase(
	private val novelSettingsRepository: INovelSettingsRepository,
	private val iSettingsRepository: ISettingsRepository,
	private val iNovelsRepository: INovelsRepository
) {
	operator fun invoke(novelID: Int): Flow<HResult<NovelSettingUI>> =
		novelSettingsRepository.getFlow(novelID).map { settingsResult ->
			settingsResult.transform(
				onEmpty = {
					launchIO {
						novelSettingsRepository.insert(NovelSettingEntity(novelID))
					}
					emptyResult()
				}
			) {
				successResult(NovelSettingConversionFactory(it).convertTo())
			}
		}
}