package com.github.doomsdayrs.apps.shosetsu.viewmodel.base

import androidx.lifecycle.LiveData
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.URLImageTitle
import com.github.doomsdayrs.apps.shosetsu.ui.updates.UpdateController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.UpdateUI
import java.util.*

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
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface IUpdatesViewModel {
	fun createControllers(): ArrayList<UpdateController>
	fun getTimeBetweenDates(date: Long, date2: Long): LiveData<HResult<List<UpdateUI>>>
	fun getURLImageTitle(novelID: Int): URLImageTitle
}