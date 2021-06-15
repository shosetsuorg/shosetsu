package app.shosetsu.android.view.uimodels.model

import android.text.format.DateFormat.format
import android.view.View
import androidx.core.view.isVisible
import app.shosetsu.android.common.ext.picasso
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem
import app.shosetsu.android.view.uimodels.base.BindViewHolder
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.RecyclerUpdateUiBinding
import java.util.*

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
	override val layoutRes: Int = R.layout.recycler_update_ui
	override val type: Int = R.layout.recycler_update_ui
	override var identifier: Long
		get() = chapterID.toLong()
		set(_) {}

	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

	/**
	 * ViewHolder for [UpdateUI]
	 */
	class ViewHolder(itemView: View) : BindViewHolder<UpdateUI, RecyclerUpdateUiBinding>(itemView) {
		override val binding = RecyclerUpdateUiBinding.bind(view)

		override fun RecyclerUpdateUiBinding.bindView(item: UpdateUI, payloads: List<Any>) {
			if (item.novelImageURL.isNotEmpty()) {
				picasso(item.novelImageURL, imageView)
			} else {
				novelTitle.isVisible = true
				novelTitle.text = item.novelName
			}

			chapterTitle.text = item.chapterName
			date.text = format("hh:mm", Date(item.time))
		}

		override fun RecyclerUpdateUiBinding.unbindView(item: UpdateUI) {
			imageView.setImageResource(R.drawable.broken_image)
			chapterTitle.text = null
			novelTitle.isVisible = false
			novelTitle.text = null
			date.text = null
		}
	}
}