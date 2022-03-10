package app.shosetsu.common.domain.repositories.base

import app.shosetsu.common.FilePermissionException
import app.shosetsu.common.IncompatibleExtensionException
import app.shosetsu.common.domain.model.local.GenericExtensionEntity
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
 *
 * This repository will be the source of truth of the extensions [IExtension] self
 */
interface IExtensionEntitiesRepository {
	/**
	 * Gets an [IExtension] via it's [extensionEntity]
	 */
	@Throws(IncompatibleExtensionException::class)
	suspend fun get(extensionEntity: GenericExtensionEntity): IExtension

	suspend fun uninstall(extensionEntity: GenericExtensionEntity)

	@Throws(FilePermissionException::class, IOException::class)
	suspend fun save(
		extensionEntity: GenericExtensionEntity,
		iExt: IExtension,
		extensionContent: ByteArray
	)
}