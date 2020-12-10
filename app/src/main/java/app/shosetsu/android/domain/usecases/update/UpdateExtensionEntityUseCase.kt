package app.shosetsu.android.domain.usecases.update

import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.domain.model.local.ExtensionEntity

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
 * 14 / 07 / 2020
 */
class UpdateExtensionEntityUseCase(
		private val extensionRepository: IExtensionsRepository,
) {
	suspend operator fun invoke(extensionUI: ExtensionEntity): HResult<*> =
			extensionRepository.updateExtensionEntity(extensionUI)
}