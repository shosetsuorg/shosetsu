package com.github.doomsdayrs.apps.shosetsu.domain.repository.model

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.github.doomsdayrs.apps.shosetsu.common.utils.FormatterUtils
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtensionEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IFormatterRepository
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.ExtensionsDao
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
 */
class FormatterRepository(
		val formatterUtils: FormatterUtils,
		val extensionsDao: ExtensionsDao
) : IFormatterRepository {

	override val daoLiveData: LiveData<List<ExtensionEntity>> =
			extensionsDao.loadPoweredFormatters()

	override fun getCards(): List<FormatterCard> = formatterUtils.getAsCards()

	override fun subscribeRepository(
			owner: LifecycleOwner,
			observer: Observer<List<ExtensionUI>>
	) =
			subscribeDao(owner, Observer { observer.onChanged(it.map { l -> l.convertTo() }) })

	override fun loadData(): List<ExtensionUI> =
			loadDataSnap().map { it.convertTo() }

	override fun subscribeDao(
			owner: LifecycleOwner,
			observer: Observer<List<ExtensionEntity>>
	) =
			daoLiveData.observe(owner, observer)

	override fun loadDataSnap(): List<ExtensionEntity> =
			daoLiveData.value ?: arrayListOf()

}