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

/**
 * shosetsu
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class DownloadsRepository(private val downloadsDao: DownloadsDao) : IDownloadsRepository {
	override val daoLiveData: LiveData<List<DownloadEntity>>
			by lazy { downloadsDao.loadDownloadItems() }

	override fun loadFirstDownload(): DownloadEntity = downloadsDao.loadAndStartFirstDownload()

	override fun loadDownloadCount(): Int = downloadsDao.loadDownloadCount()

	override suspend fun addDownload(download: DownloadEntity): Long =
			downloadsDao.insertIgnore(download)

	override suspend fun suspendedUpdate(download: DownloadEntity) =
			downloadsDao.suspendedUpdate(download)

	override suspend fun suspendedDelete(download: DownloadEntity) =
			downloadsDao.suspendedDelete(download)

	override fun subscribeDao(
			owner: LifecycleOwner,
			observer: Observer<List<DownloadEntity>>
	) = daoLiveData.observe(owner, Observer { observer.onChanged(it) })

	override fun loadDataSnap(): List<DownloadEntity> = daoLiveData.value ?: arrayListOf()
}