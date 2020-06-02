package com.github.doomsdayrs.apps.shosetsu.domain.usecases

import android.util.Log
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.errorResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IChaptersRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IExtensionsRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.INovelsRepository

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
 *
 * takes a novelID & parameters, then loads it's data to storage
 */
class LoadNovelUseCase(
		private val nR: INovelsRepository,
		private val eR: IExtensionsRepository,
		private val cR: IChaptersRepository
) {
	suspend operator fun invoke(novelID: Int, loadChapters: Boolean): HResult<*> =
			when (val nResult = nR.loadNovel(novelID)) {
				is HResult.Success -> {
					val novel = nResult.data
					when (val fR = eR.loadFormatter(novel.formatterID)) {
						is HResult.Success -> {
							val ext = fR.data
							when (val pR = nR.retrieveNovelInfo(ext, novel, loadChapters)) {
								is HResult.Success -> {
									val page = pR.data
									Log.d(logID(), "Loaded novel info $page")
									nR.updateNovelData(novel, page)
									if (loadChapters)
										cR.handleChapters(novel, page.chapters)
									successResult(true)
								}
								is HResult.Error -> pR
								else -> errorResult(ErrorKeys.ERROR_GENERAL, "Unknown failure")
							}
						}
						is HResult.Error -> fR
						else -> errorResult(ErrorKeys.ERROR_GENERAL, "Unknown failure")
					}
				}
				is HResult.Error -> nResult
				else -> errorResult(ErrorKeys.ERROR_GENERAL, "Unknown failure")
			}
}