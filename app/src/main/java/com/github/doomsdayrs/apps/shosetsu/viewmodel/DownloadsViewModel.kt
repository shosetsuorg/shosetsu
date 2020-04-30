package com.github.doomsdayrs.apps.shosetsu.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.DownloadEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.impl.DownloadsRepositoryImpl
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IDownloadsViewModel

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
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class DownloadsViewModel(val downloadsDaoImpl: DownloadsRepositoryImpl)
	: ViewModel(), IDownloadsViewModel {
	override val liveData: LiveData<List<DownloadEntity>> by lazy { downloadsDaoImpl.getDownloads() }

	override fun loadData(): List<DownloadEntity> = liveData.value ?: arrayListOf()

	override fun subscribeObserver(
			owner: LifecycleOwner,
			observer: Observer<List<DownloadEntity>>
	) = liveData.observe(owner, observer)
}