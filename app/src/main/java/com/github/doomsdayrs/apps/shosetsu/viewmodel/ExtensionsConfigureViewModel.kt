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
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.common.utils.FormatterUtils
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtensionEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.ExtensionsDao
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IExtensionsConfigureViewModel

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsConfigureViewModel(
		private val extensionsDao: ExtensionsDao,
		private val formatterUtils: FormatterUtils
)
	: ViewModel(), IExtensionsConfigureViewModel {
	override val liveData: LiveData<List<ExtensionEntity>> by lazy { extensionsDao.loadFormatters() }

	override fun loadData(): List<ExtensionEntity> = liveData.value ?: arrayListOf()

	override fun disableExtension(
			extensionEntity: ExtensionEntity,
			callback: (ExtensionEntity) -> Unit
	) {
		TODO("disableExtension")
	}

	override fun enableExtension(
			extensionEntity: ExtensionEntity,
			callback: (ExtensionEntity) -> Unit
	) {
		TODO("enableExtension")
	}

	override fun loadFormatterIfEnabled(extensionEntity: ExtensionEntity): Formatter? {
		TODO("loadFormatterIfEnabled")
	}

	override fun subscribeObserver(
			owner: LifecycleOwner,
			observer: Observer<List<ExtensionEntity>>
	) = liveData.observe(owner, observer)
}