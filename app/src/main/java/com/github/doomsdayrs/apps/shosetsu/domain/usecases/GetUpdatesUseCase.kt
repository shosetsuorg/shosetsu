package com.github.doomsdayrs.apps.shosetsu.domain.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.mapTo
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IUpdatesRepository
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.UpdateUI

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
 * 13 / 05 / 2020
 */
class GetUpdatesUseCase(
		private val updatesRepository: IUpdatesRepository,
) : (() -> LiveData<HResult<List<UpdateUI>>>) {
	override fun invoke(): LiveData<HResult<List<UpdateUI>>> = liveData {
		emitSource(updatesRepository.getCompleteUpdates().map {
			it.let {
				when (it) {
					is HResult.Success -> successResult(it.data.mapTo())
					is HResult.Loading -> it
					is HResult.Error -> it
					is HResult.Empty -> it
				}
			}
		})
	}
}