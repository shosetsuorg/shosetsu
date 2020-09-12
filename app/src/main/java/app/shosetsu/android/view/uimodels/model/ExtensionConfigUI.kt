package app.shosetsu.android.view.uimodels.model

import android.view.View
import app.shosetsu.android.domain.model.base.Convertible
import app.shosetsu.android.domain.model.local.ExtensionEntity
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import app.shosetsu.android.view.uimodels.base.GetImageURL
import app.shosetsu.android.view.uimodels.base.GetTitle
import app.shosetsu.android.view.uimodels.model.ExtensionConfigUI.ViewHolder
import app.shosetsu.android.view.viewholders.TitleImageFViewHolder
import com.github.doomsdayrs.apps.shosetsu.R

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
		val imageURL: String,
		val lang: String,
		var isExtEnabled: Boolean, // The only thing that can be changed >^<)
		val installed: Boolean,
		val installedVersion: String?,
		val repositoryVersion: String,
		val md5: String,
) :
		BaseRecyclerItem<ViewHolder>(),
		Convertible<ExtensionEntity>,
		GetImageURL,
		GetTitle {
	override fun getDataTitle(): String = name

	override fun getDataImageURL(): String = imageURL

	override val layoutRes: Int
		get() = R.layout.extension_config_choice_item_card

	override val type: Int
		get() = -1

	override var identifier: Long
		get() = id.toLong()
		set(@Suppress("UNUSED_PARAMETER") value) {}

	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

	override fun convertTo(): ExtensionEntity = ExtensionEntity(
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

	class ViewHolder(itemView: View) : TitleImageFViewHolder<ExtensionConfigUI>(itemView)
}