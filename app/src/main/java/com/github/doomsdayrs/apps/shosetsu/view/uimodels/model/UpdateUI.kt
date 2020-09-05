package com.github.doomsdayrs.apps.shosetsu.view.uimodels.model

import android.view.View
import com.github.doomsdayrs.apps.shosetsu.R
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
 *
 * UpdateUI entity to display
 */
data class UpdateUI(
		val chapterID: Int,
		val novelID: Int,
		val time: Long,
		val chapterName: String,
		val novelName: String,
		val novelImageURL: String,
) : BaseRecyclerItem<UpdateUI.ViewHolder>() {
	override val layoutRes: Int = R.layout.recycler_novel_card_compressed
	override val type: Int = R.layout.recycler_novel_card_compressed
	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

	/**  */
	class ViewHolder(itemView: View) : FastAdapter.ViewHolder<UpdateUI>(itemView) {
		override fun bindView(item: UpdateUI, payloads: List<Any>) {
		}

		override fun unbindView(item: UpdateUI) {
		}
	}
}