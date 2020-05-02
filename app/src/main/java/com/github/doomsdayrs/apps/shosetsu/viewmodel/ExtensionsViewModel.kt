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
import androidx.recyclerview.widget.DiffUtil
import com.github.doomsdayrs.apps.shosetsu.common.utils.FormatterUtils
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtensionEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.ExtensionsDao
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ExtensionUI
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
	inner class ExtensionsDifCalc(
			val old: List<ExtensionUI>,
			val new: List<ExtensionUI>
	) : DiffUtil.Callback() {
		override fun getOldListSize() = old.size
		override fun getNewListSize(): Int = new.size

		override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
				old[oldItemPosition] == new[newItemPosition]

		override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
				old[oldItemPosition].id == new[newItemPosition].id
	}


	override val daoLiveData: LiveData<List<ExtensionEntity>> =
			extensionsDao.loadFormatters()

	override fun reloadFormatters() {
		TODO("Not yet implemented")
	}

	override fun refreshRepository() {
		TODO("Not yet implemented")
	}

	override fun installExtension(extensionEntity: ExtensionUI) {
		extensionEntity.installed = true
		extensionEntity.enabled = true
		TODO("installExtension")
	}

	override fun uninstallExtension(extensionEntity: ExtensionUI) {
		extensionEntity.installed = false
		extensionEntity.enabled = false
		TODO("uninstallExtension")
	}

	override fun subscribeObserver(owner: LifecycleOwner, observer: Observer<List<ExtensionUI>>) {
		TODO("Not yet implemented")
	}

	override suspend fun getLiveData(): List<ExtensionUI> =
			loadDataSnap().map { it.convertTo() }

	override fun subscribeDao(
			owner: LifecycleOwner,
			observer: Observer<List<ExtensionEntity>>
	) =
			daoLiveData.observe(owner, observer)

	override fun loadDataSnap(): List<ExtensionEntity> =
			daoLiveData.value ?: arrayListOf()
}