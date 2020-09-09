package com.github.doomsdayrs.apps.shosetsu.domain.usecases.load

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IExtensionsRepository
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.ExtensionConfigUI

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
class LoadExtensionConfigUseCase(
		private val iExtensionsRepository: IExtensionsRepository,
) {
	suspend operator fun invoke(): LiveData<HResult<List<ExtensionConfigUI>>> =
			liveData<HResult<List<ExtensionConfigUI>>> {
				emit(loading())
				emitSource(iExtensionsRepository.getExtensions().map { it ->
					when (it) {
						is HResult.Success -> {
							successResult(
									it.data.map {
										ExtensionConfigUI(
												it.id,
												it.repoID,
												it.name,
												it.fileName,
												it.imageURL ?: "",
												it.lang,
												it.enabled,
												it.installed,
												it.installedVersion,
												it.repositoryVersion,
												it.md5
										)
									}
							)
						}
						is HResult.Loading -> it
						is HResult.Error -> it
						is HResult.Empty -> it
					}
				})
			}
}