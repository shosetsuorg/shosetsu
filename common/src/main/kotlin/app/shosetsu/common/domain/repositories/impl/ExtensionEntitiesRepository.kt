package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.FilePermissionException
import app.shosetsu.common.IncompatibleExtensionException
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.datasource.file.base.IFileExtensionDataSource
import app.shosetsu.common.datasource.file.base.IFileSettingsDataSource
import app.shosetsu.common.datasource.memory.base.IMemExtensionsDataSource
import app.shosetsu.common.domain.model.local.GenericExtensionEntity
import app.shosetsu.common.domain.repositories.base.IExtensionEntitiesRepository
import app.shosetsu.lib.Filter
import app.shosetsu.lib.IExtension
import java.io.IOException

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

	override suspend fun get(extensionEntity: GenericExtensionEntity): IExtension {
		return try {
			memorySource.loadExtensionFromMemory(extensionEntity.id)!!
		} catch (e: Exception) {
			val it = fileSource.loadExtension(extensionEntity)
			if (!it.exMetaData.libVersion.isCompatible())
				throw IncompatibleExtensionException(extensionEntity, it.exMetaData.libVersion)

			setSettings(it, it.settingsModel)
			memorySource.putExtensionInMemory(it)
			it
		}


	}

	override suspend fun uninstall(extensionEntity: GenericExtensionEntity) {
		memorySource.removeExtensionFromMemory(extensionEntity.id)
		fileSource.deleteExtension(extensionEntity)
	}

	@Throws(FilePermissionException::class, IOException::class)
	override suspend fun save(
		extensionEntity: GenericExtensionEntity,
		iExt: IExtension,
		extensionContent: ByteArray
	) {
		memorySource.putExtensionInMemory(iExt)

		fileSource.writeExtension(extensionEntity, extensionContent)
	}


	suspend fun getInt(extensionID: Int, settingID: Int, default: Int): Int =
		settingsSource.getInt("$extensionID", SettingKey.CustomInt("$settingID", default))

	suspend fun getString(
		extensionID: Int,
		settingID: Int,
		default: String
	): String =
		settingsSource.getString("$extensionID", SettingKey.CustomString("$settingID", default))

	suspend fun getBoolean(
		extensionID: Int,
		settingID: Int,
		default: Boolean
	): Boolean =
		settingsSource.getBoolean(
			"$extensionID",
			SettingKey.CustomBoolean("$settingID", default)
		)

	suspend fun getFloat(
		extensionID: Int,
		settingID: Int,
		default: Float
	): Float =
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