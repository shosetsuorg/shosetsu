package com.github.doomsdayrs.apps.shosetsu.viewmodel

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

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.github.doomsdayrs.apps.shosetsu.common.utils.FormatterUtils
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtensionEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.ExtensionsDao
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IExtensionsViewModel

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsViewModel(
		val extensionsDao: ExtensionsDao,
		val formatterUtils: FormatterUtils
) : ViewModel(), IExtensionsViewModel {
	override var liveData: LiveData<List<ExtensionEntity>> = extensionsDao.loadPoweredFormatters()

	override suspend fun loadData(): List<ExtensionEntity> = liveData.value ?: arrayListOf()

	override fun subscribeObserver(
			owner: LifecycleOwner,
			observer: Observer<List<ExtensionEntity>>
	) = liveData.observe(owner, observer);

	override fun reloadFormatters() {
		TODO("reloadFormatters")
	}

	override fun refreshRepository() {
		TODO("refreshRepository")
	}

	override fun installExtension(extensionEntity: ExtensionEntity) {
		extensionEntity.installed = true
		extensionEntity.enabled = true
		TODO("installExtension")
	}

	override fun uninstallExtension(extensionEntity: ExtensionEntity) {
		extensionEntity.installed = false
		extensionEntity.enabled = false
		TODO("uninstallExtension")
	}
}