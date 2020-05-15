package com.github.doomsdayrs.apps.shosetsu.domain.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult

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
 * 15 / 05 / 2020
 */
class NovelBackgroundAddUseCase(
		val novelLoadUseCase: NovelLoadUseCase,
		val bookMarkNovelIDUseCase: BookMarkNovelIDUseCase
) : ((@ParameterName("novelID") Int) -> LiveData<HResult<*>>) {
	override fun invoke(novelID: Int): LiveData<HResult<*>> = liveData {
		emitSource(novelLoadUseCase(novelID, false).map {
			if (it is HResult.Success) bookMarkNovelIDUseCase(novelID); it
		})
	}
}