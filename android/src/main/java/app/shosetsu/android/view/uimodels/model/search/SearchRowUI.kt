package app.shosetsu.android.view.uimodels.model.search

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import app.shosetsu.android.common.ext.shosetsuLoad
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import app.shosetsu.android.view.uimodels.base.BindViewHolder
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.RecyclerSearchRowBinding

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
	val extensionID: Int,
	val name: String,
	val imageURL: String
) : BaseRecyclerItem<SearchRowUI.ViewHolder>() {
	override val layoutRes: Int = R.layout.recycler_search_row
	override val type: Int = R.layout.recycler_search_row

	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

	/***/
	class ViewHolder(itemView: View) :
		BindViewHolder<SearchRowUI, RecyclerSearchRowBinding>(itemView) {
		override val binding = RecyclerSearchRowBinding.bind(view)

		override fun RecyclerSearchRowBinding.bindView(item: SearchRowUI, payloads: List<Any>) {
			title.text = item.name
			recyclerView.layoutManager =
				LinearLayoutManager(recyclerView.context, HORIZONTAL, false)
			recyclerView.setHasFixedSize(false)
			if (item.imageURL.isNotEmpty()) imageView.shosetsuLoad(item.imageURL)
		}

		override fun RecyclerSearchRowBinding.unbindView(item: SearchRowUI) {
			title.text = null
			imageView.setImageResource(R.drawable.library)
			progressBar.isVisible = false
		}
	}
}