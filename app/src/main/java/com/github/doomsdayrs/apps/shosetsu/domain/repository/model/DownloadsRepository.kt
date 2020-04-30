package com.github.doomsdayrs.apps.shosetsu.domain.repository.model

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
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.DownloadEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IDownloadsRepository
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.DownloadsDao
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.DownloadUI

/**
 * shosetsu
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class DownloadsRepository(private val downloadsDao: DownloadsDao) : IDownloadsRepository {
	override val daoLiveData: LiveData<List<DownloadEntity>>
			by lazy { downloadsDao.loadDownloadItems() }

	override suspend fun addDownload(download: DownloadUI): Long =
			downloadsDao.insertIgnore(download.convertTo())

	override suspend fun updateDownload(download: DownloadUI) =
			downloadsDao.update(download.convertTo())

	override suspend fun removeDownload(download: DownloadUI) =
			downloadsDao.delete(download.convertTo())

	override fun subscribeRepository(
			owner: LifecycleOwner,
			observer: Observer<List<DownloadUI>>
	) = subscribeDao(owner, Observer { observer.onChanged(it.map { l -> l.convertTo() }) })

	override fun subscribeDao(
			owner: LifecycleOwner,
			observer: Observer<List<DownloadEntity>>
	) = daoLiveData.observe(owner, Observer { observer.onChanged(it) })

	override fun loadData(): List<DownloadUI> = loadDataSnap().map { it.convertTo() }

	override fun loadDataSnap(): List<DownloadEntity> = daoLiveData.value ?: arrayListOf()
}