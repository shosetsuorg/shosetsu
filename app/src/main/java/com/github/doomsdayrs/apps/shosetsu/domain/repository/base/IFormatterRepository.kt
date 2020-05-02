package com.github.doomsdayrs.apps.shosetsu.domain.repository.base

import androidx.lifecycle.LiveData
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtensionEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.base.SubscribeLiveData
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.base.SubscribeRepository
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.FormatterCard
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ExtensionUI

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
 * 30 / 04 / 2020
 * Repository for formatters
 */
interface IFormatterRepository :
		SubscribeRepository<List<ExtensionUI>>, SubscribeLiveData<List<ExtensionEntity>> {
	fun getCards(): LiveData<HResult<List<FormatterCard>>>
}