package com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.search

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.base.BaseRecyclerItem
import com.mikepenz.fastadapter.FastAdapter
import com.squareup.picasso.Picasso

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
 * 09 / 09 / 2020
 * @param name Name displayed of this data
 */
data class SearchRowUI(
		val formatterID: Int,
		val name: String,
		val imageURL: String?
) : BaseRecyclerItem<SearchRowUI.ViewHolder>() {

	override val layoutRes: Int = R.layout.recycler_search_row
	override val type: Int = R.layout.recycler_search_row

	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

	/***/
	class ViewHolder(itemView: View) : FastAdapter.ViewHolder<SearchRowUI>(itemView) {
		/** Progress Bar */
		val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

		/** ImageView */
		val imageView: ImageView = itemView.findViewById(R.id.imageView)

		/** Title */
		val title: TextView = itemView.findViewById(R.id.title)

		/** Recycler View */
		val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)

		override fun bindView(item: SearchRowUI, payloads: List<Any>) {
			title.text = item.name
			recyclerView.layoutManager = LinearLayoutManager(recyclerView.context, HORIZONTAL, false)
			recyclerView.setHasFixedSize(false)
			if (item.imageURL != null) Picasso.get().load(item.imageURL).into(imageView)
		}

		override fun unbindView(item: SearchRowUI) {
			title.text = null
			progressBar.visibility = View.GONE
		}
	}
}