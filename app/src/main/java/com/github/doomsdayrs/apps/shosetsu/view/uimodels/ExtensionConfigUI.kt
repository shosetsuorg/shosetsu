package com.github.doomsdayrs.apps.shosetsu.view.uimodels

import android.view.View
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.domain.model.base.Convertible
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtensionEntity
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.base.BaseRecyclerItem
import com.mikepenz.fastadapter.FastAdapter

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
 */
data class ExtensionConfigUI(
		val id: Int,
		val repoID: Int,
		val name: String,
		val fileName: String,
		val imageURL: String?,
		val lang: String,
		var isExtEnabled: Boolean, // The only thing that can be changed >^<)
		val installed: Boolean,
		val installedVersion: String?,
		val repositoryVersion: String,
		val md5: String
) : BaseRecyclerItem<ExtensionConfigUI.ViewHolder>(), Convertible<ExtensionEntity> {
	override fun convertTo() = ExtensionEntity(
			id,
			repoID,
			name,
			fileName,
			imageURL,
			lang,
			isExtEnabled,
			installed,
			installedVersion,
			repositoryVersion,
			md5
	)

	class ViewHolder(itemView: View) : FastAdapter.ViewHolder<ExtensionConfigUI>(itemView) {
		override fun bindView(item: ExtensionConfigUI, payloads: List<Any>) {
			TODO("Not yet implemented")
		}

		override fun unbindView(item: ExtensionConfigUI) {
			TODO("Not yet implemented")
		}

	}

	override val layoutRes: Int
		get() = R.layout.alert_extensions_configure_card
	override val type: Int
		get() = -1

	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)
}