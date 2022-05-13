package app.shosetsu.android.datasource.local.memory.impl

import app.shosetsu.android.common.consts.MEMORY_EXPIRE_EXTENSION_TIME
import app.shosetsu.android.common.consts.MEMORY_MAX_EXTENSIONS
import app.shosetsu.android.datasource.local.memory.base.IMemExtensionsDataSource
import app.shosetsu.lib.IExtension

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
 * shosetsu
 * 19 / 11 / 2020
 */
class GenericMemExtensionDataSource : IMemExtensionsDataSource,
	AbstractMemoryDataSource<Int, IExtension>() {

	override val maxSize = MEMORY_MAX_EXTENSIONS
	override val expireTime = MEMORY_EXPIRE_EXTENSION_TIME * 1000 * 60 * 60

	override fun loadExtensionFromMemory(extensionID: Int): IExtension? =
		get(extensionID)

	override fun putExtensionInMemory(iExtension: IExtension) =
		put(iExtension.formatterID, iExtension)

	override fun removeExtensionFromMemory(extensionID: Int) =
		remove(extensionID)

}