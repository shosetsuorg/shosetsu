package app.shosetsu.android.domain.usecases.update

import app.shosetsu.common.domain.model.local.NovelSettingEntity
import app.shosetsu.common.domain.repositories.base.INovelSettingsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.view.uimodel.NovelSettingUI

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
 * 30 / 12 / 2020
 */
class UpdateNovelSettingUseCase(
	private val novelSettingsRepository: INovelSettingsRepository
) {
	suspend operator fun invoke(novelSettingUI: NovelSettingUI): HResult<*> =
		invoke(novelSettingUI.convertTo())

	suspend operator fun invoke(novelSettingEntity: NovelSettingEntity): HResult<*> =
		novelSettingsRepository.update(novelSettingEntity)
}