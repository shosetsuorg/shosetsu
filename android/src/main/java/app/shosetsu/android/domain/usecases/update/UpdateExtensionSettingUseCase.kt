package app.shosetsu.android.domain.usecases.update

import app.shosetsu.android.common.ext.generify
import app.shosetsu.android.common.ext.logI
import app.shosetsu.common.GenericSQLiteException
import app.shosetsu.common.IncompatibleExtensionException
import app.shosetsu.common.domain.repositories.base.IExtensionEntitiesRepository
import app.shosetsu.common.domain.repositories.base.IExtensionSettingsRepository
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository

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
	@Throws(GenericSQLiteException::class, IncompatibleExtensionException::class)
	private suspend fun update(extensionId: Int, settingId: Int, value: Any?) =
		extRepo.getInstalledExtension(extensionId)?.let { entity ->
			extEntitiesRepo.get(entity.generify()).let {
				(it.updateSetting(settingId, value))
			}
		}

	@Throws(GenericSQLiteException::class, IncompatibleExtensionException::class)
	suspend operator fun invoke(extensionId: Int, settingId: Int, value: Int) {
		logI("Updating setting($settingId) for extension($extensionId) with value $value")
		return extSettingsRepo.setInt(extensionId, settingId, value).let {
			update(extensionId, settingId, value)
		}
	}

	@Throws(GenericSQLiteException::class, IncompatibleExtensionException::class)
	suspend operator fun invoke(extensionId: Int, settingId: Int, value: String) {
		logI("Updating setting($settingId) for extension($extensionId) with value $value")
		return extSettingsRepo.setString(extensionId, settingId, value).let {
			update(extensionId, settingId, value)
		}
	}


	suspend operator fun invoke(extensionId: Int, settingId: Int, value: Boolean) {
		logI("Updating setting($settingId) for extension($extensionId) with value $value")
		return extSettingsRepo.setBoolean(extensionId, settingId, value).let {
			update(extensionId, settingId, value)
		}
	}

	suspend operator fun invoke(extensionId: Int, settingId: Int, value: Float) {
		logI("Updating setting($settingId) for extension($extensionId) with value $value")
		return extSettingsRepo.setFloat(extensionId, settingId, value).let {
			update(extensionId, settingId, value)
		}
	}
}