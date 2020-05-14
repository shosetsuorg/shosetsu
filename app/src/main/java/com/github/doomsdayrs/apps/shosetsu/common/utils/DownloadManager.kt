package com.github.doomsdayrs.apps.shosetsu.common.utils

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */
import android.app.Activity
import android.content.Context
import android.util.Log
import com.github.doomsdayrs.apps.shosetsu.backend.services.DownloadService
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.DownloadUI

/**
 * Shosetsu
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 * Manages downloading and downloaded chapters
 */
class DownloadManager {
	/**
	 * Adds to download list
	 *
	 * @param downloadEntity download item to add
	 */
	fun addToDownload(activity: Activity?, downloadEntity: DownloadUI) {
		//	downloadsDao.insertDownloadEntity(downloadEntity)
		activity?.let { DownloadService.start(it) }
	}

	/**
	 * delete downloaded chapter
	 *
	 * @param context      context to work with
	 * @param downloadItem download item to remove
	 * @return if downloaded
	 */
	fun delete(context: Context?, downloadItem: DownloadUI): Boolean {
		TODO("FIX")
		Log.d("DeletingChapter", downloadItem.toString())
		//	val file = File(Utilities.shoDir + "/download/" + downloadItem.formatter.formatterID + "/" + downloadItem.novelName + "/" + downloadItem.chapterName + ".txt")
		//removePath(downloadItem.chapterID)
		//	if (file.exists()) if (!file.delete()) if (context != null) {
		//		context.toast(R.string.download_fail_delete, duration = LENGTH_LONG)
//			return false
		//	}
		//	return true
	}

}