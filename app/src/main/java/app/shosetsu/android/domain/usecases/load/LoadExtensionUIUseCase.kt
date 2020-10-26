package app.shosetsu.android.domain.usecases.load

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.loading
import app.shosetsu.android.common.dto.mapTo
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.android.view.uimodels.model.ExtensionUI

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
 * 04 / 07 / 2020
 */
class LoadExtensionUIUseCase(
		private val iExtensionsRepository: IExtensionsRepository,
) {
	operator fun invoke(id: Int): LiveData<HResult<ExtensionUI>> =
			liveData<HResult<ExtensionUI>> {
				emit(loading())
				if (id != -1)
					emitSource(iExtensionsRepository.getExtensionEntityLive(id).map { it.mapTo() })
			}
}