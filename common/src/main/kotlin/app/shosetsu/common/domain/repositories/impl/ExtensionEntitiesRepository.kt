package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.consts.ErrorKeys
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.datasource.file.base.IFileExtensionDataSource
import app.shosetsu.common.datasource.file.base.IFileSettingsDataSource
import app.shosetsu.common.datasource.memory.base.IMemExtensionsDataSource
import app.shosetsu.common.domain.model.local.ExtensionEntity
import app.shosetsu.common.domain.repositories.base.IExtensionEntitiesRepository
import app.shosetsu.common.dto.*
import app.shosetsu.lib.Filter
import app.shosetsu.lib.IExtension

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * Shosetsu
 *
 * @since 16 / 08 / 2021
 * @author Doomsdayrs
 */
class ExtensionEntitiesRepository(
	private val memorySource: IMemExtensionsDataSource,
	private val fileSource: IFileExtensionDataSource,
	private val settingsSource: IFileSettingsDataSource
) : IExtensionEntitiesRepository {
	override suspend fun get(extensionEntity: ExtensionEntity): HResult<IExtension> =
		memorySource.loadExtensionFromMemory(extensionEntity.id).catch {
			fileSource.loadExtension(extensionEntity).transform {
				if (!it.exMetaData.libVersion.isCompatible())
					return errorResult(ErrorKeys.ERROR_EXT_INCOMPATIBLE)
				setSettings(it, it.settingsModel)
				memorySource.putExtensionInMemory(it)
				successResult(it)
			}
		}

	override suspend fun uninstall(extensionEntity: ExtensionEntity): HResult<*> =
		memorySource.removeExtensionFromMemory(extensionEntity.id) ifSo {
			fileSource.deleteExtension(extensionEntity)
		}


	override suspend fun save(
		extensionEntity: ExtensionEntity,
		iExt: IExtension,
		extensionContent: ByteArray
	): HResult<*> =
		memorySource.putExtensionInMemory(iExt)
			.thenAlso(fileSource.writeExtension(extensionEntity, extensionContent))


	suspend fun getInt(extensionID: Int, settingID: Int, default: Int): HResult<Int> =
		settingsSource.getInt("$extensionID", SettingKey.CustomInt("$settingID", default))

	suspend fun getString(
		extensionID: Int,
		settingID: Int,
		default: String
	): HResult<String> =
		settingsSource.getString("$extensionID", SettingKey.CustomString("$settingID", default))

	suspend fun getBoolean(
		extensionID: Int,
		settingID: Int,
		default: Boolean
	): HResult<Boolean> =
		settingsSource.getBoolean(
			"$extensionID",
			SettingKey.CustomBoolean("$settingID", default)
		)

	suspend fun getFloat(
		extensionID: Int,
		settingID: Int,
		default: Float
	): HResult<Float> =
		settingsSource.getFloat("$extensionID", SettingKey.CustomFloat("$settingID", default))

	private suspend fun setSettings(extension: IExtension, filters: Array<out Filter<out Any?>>) {
		filters.forEach { filter ->
			when (filter) {
				is Filter.Text -> {
					extension.updateSetting(
						filter.id,
						getString(extension.formatterID, filter.id, filter.state)
					)
				}
				is Filter.Switch -> {
					extension.updateSetting(
						filter.id,
						getBoolean(extension.formatterID, filter.id, filter.state)
					)
				}
				is Filter.Checkbox -> {
					extension.updateSetting(
						filter.id,
						getBoolean(extension.formatterID, filter.id, filter.state)
					)
				}
				is Filter.TriState -> {
					extension.updateSetting(
						filter.id,
						getInt(extension.formatterID, filter.id, filter.state)
					)
				}
				is Filter.Dropdown -> {
					extension.updateSetting(
						filter.id,
						getInt(extension.formatterID, filter.id, filter.state)
					)
				}
				is Filter.RadioGroup -> {
					extension.updateSetting(
						filter.id,
						getInt(extension.formatterID, filter.id, filter.state)
					)
				}
				is Filter.List -> {
					setSettings(extension, filter.filters)
				}
				is Filter.Group<*> -> {
					setSettings(extension, filter.filters)
				}
				else -> {
				}
			}
		}
	}
}