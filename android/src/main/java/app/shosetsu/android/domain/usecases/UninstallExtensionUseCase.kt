package app.shosetsu.android.domain.usecases

import app.shosetsu.android.common.GenericSQLiteException
import app.shosetsu.android.common.ext.generify
import app.shosetsu.android.domain.model.local.InstalledExtensionEntity
import app.shosetsu.android.domain.repository.base.IExtensionEntitiesRepository
import app.shosetsu.android.domain.repository.base.IExtensionsRepository

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
 * shosetsu
 * 14 / 08 / 2020
 */
class UninstallExtensionUseCase(
	private val extensionRepository: IExtensionsRepository,
	private val extensionEntitiesRepository: IExtensionEntitiesRepository
) {
	@Throws(GenericSQLiteException::class)
	suspend operator fun invoke(extensionEntity: InstalledExtensionEntity) {
		extensionEntitiesRepository.uninstall(extensionEntity.generify())
		extensionRepository.uninstall(extensionEntity)
	}

}