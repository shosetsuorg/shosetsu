package com.github.doomsdayrs.apps.shosetsu.domain.repository.base
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
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.DownloadEntity
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.DownloadUI


/**
 * shosetsu
 * 25 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface IDownloadsRepository :
		SubscribeRepository<List<DownloadUI>>,
		SubscribeLiveData<List<DownloadEntity>> {
	suspend fun addDownload(download: DownloadUI): Long
	suspend fun updateDownload(download: DownloadUI)
	suspend fun removeDownload(download: DownloadUI)
}