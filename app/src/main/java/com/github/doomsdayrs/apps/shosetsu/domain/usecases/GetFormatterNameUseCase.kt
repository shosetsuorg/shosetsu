package com.github.doomsdayrs.apps.shosetsu.domain.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult

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
 * 18 / 05 / 2020
 */
class GetFormatterNameUseCase(
		private val getFormatterUseCase: GetFormatterUseCase
) : ((@kotlin.ParameterName("formatterID") Int) -> LiveData<HResult<String>>) {
	override fun invoke(formatterID: Int): LiveData<HResult<String>> {
		return liveData<HResult<String>> {
			emit(loading())
			emitSource(getFormatterUseCase(formatterID).map {
				when (it) {
					is HResult.Success -> successResult(it.data.name)
					is HResult.Error -> it
					is HResult.Empty -> it
					is HResult.Loading -> it
				}
			})
		}
	}
}