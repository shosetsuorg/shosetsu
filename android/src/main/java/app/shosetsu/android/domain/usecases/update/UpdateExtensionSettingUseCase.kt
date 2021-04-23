package app.shosetsu.android.domain.usecases.update

import app.shosetsu.common.domain.repositories.base.IExtensionSettingsRepository
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.dto.handle

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
	private val extSettingsRepo: IExtensionSettingsRepository
) {
	private suspend fun update(extensionId: Int, settingId: Int, value: Any?) {
		extRepo.getIExtension(extensionId).handle {
			it.updateSetting(settingId, value)
		}
	}

	suspend operator fun invoke(extensionId: Int, settingId: Int, value: Int) {
		update(extensionId, settingId, value)
		extSettingsRepo.setInt(extensionId, settingId, value)
	}

	suspend operator fun invoke(extensionId: Int, settingId: Int, value: String) {
		update(extensionId, settingId, value)
		extSettingsRepo.setString(extensionId, settingId, value)
	}

	suspend operator fun invoke(extensionId: Int, settingId: Int, value: Boolean) {
		update(extensionId, settingId, value)
		extSettingsRepo.setBoolean(extensionId, settingId, value)
	}

	suspend operator fun invoke(extensionId: Int, settingId: Int, value: Float) {
		update(extensionId, settingId, value)
		extSettingsRepo.setFloat(extensionId, settingId, value)
	}
}