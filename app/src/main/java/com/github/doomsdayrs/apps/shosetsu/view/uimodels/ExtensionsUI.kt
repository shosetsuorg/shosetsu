package com.github.doomsdayrs.apps.shosetsu.view.uimodels

import android.view.View
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.base.BaseRecyclerUI
import com.github.doomsdayrs.apps.shosetsu.view.viewholders.DownloadViewHolder

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
 * ====================================================================
 */

/**
 * shosetsu
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
data class ExtensionsUI(
		val id: Int,
		val repoID: Int,
		var name: String,
		val fileName: String,
		var imageURL: String?,
		var lang: String,
		var extensionEnabled: Boolean,
		var installed: Boolean,
		var installedVersion: String?,
		var repositoryVersion: String,
		var md5: String
) : BaseRecyclerUI<DownloadViewHolder>(){
	override fun getViewHolder(v: View): DownloadViewHolder {
		TODO("Not yet implemented")
	}
}