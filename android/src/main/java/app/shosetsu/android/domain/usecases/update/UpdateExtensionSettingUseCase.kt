package app.shosetsu.android.domain.usecases.update

import app.shosetsu.android.common.ext.logI
import app.shosetsu.common.domain.repositories.base.IExtensionEntitiesRepository
import app.shosetsu.common.domain.repositories.base.IExtensionSettingsRepository
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform

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
 * 15 / 04 / 2021
 *
 * Updates an extensions setting
 * This also updates the setting for when the extension is loaded
 */
class UpdateExtensionSettingUseCase(
	private val extRepo: IExtensionsRepository,
	private val extEntitiesRepo: IExtensionEntitiesRepository,
	private val extSettingsRepo: IExtensionSettingsRepository
) {
	private suspend fun update(extensionId: Int, settingId: Int, value: Any?) =
		extRepo.getExtension(extensionId).transform { entity ->
			extEntitiesRepo.get(entity).transform {
				successResult(it.updateSetting(settingId, value))
			}
		}

	suspend operator fun invoke(extensionId: Int, settingId: Int, value: Int): HResult<Unit> {
		logI("Updating setting($settingId) for extension($extensionId) with value $value")
		return extSettingsRepo.setInt(extensionId, settingId, value).transform {
			update(extensionId, settingId, value)
		}
	}

	suspend operator fun invoke(extensionId: Int, settingId: Int, value: String): HResult<Unit> {
		logI("Updating setting($settingId) for extension($extensionId) with value $value")
		return extSettingsRepo.setString(extensionId, settingId, value).transform {
			update(extensionId, settingId, value)
		}
	}


	suspend operator fun invoke(extensionId: Int, settingId: Int, value: Boolean): HResult<Unit> {
		logI("Updating setting($settingId) for extension($extensionId) with value $value")
		return extSettingsRepo.setBoolean(extensionId, settingId, value).transform {
			update(extensionId, settingId, value)
		}
	}

	suspend operator fun invoke(extensionId: Int, settingId: Int, value: Float): HResult<Unit> {
		logI("Updating setting($settingId) for extension($extensionId) with value $value")
		return extSettingsRepo.setFloat(extensionId, settingId, value).transform {
			update(extensionId, settingId, value)
		}
	}
}