package app.shosetsu.android.view.uimodels.model

import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatTextView
import app.shosetsu.android.common.consts.SELECTED_STROKE_WIDTH
import app.shosetsu.android.common.ext.serializeToString
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.card.MaterialCardView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

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

/**
 * shosetsu
 * 17 / 09 / 2020
 *
 * Choices for colors
 * @param name Name of the color choice
 * @param textColor Color of the text
 * @param backgroundColor Color of the background
 */
data class ColorChoiceUI(
	override var identifier: Long,
	val name: String,
	val textColor: Int,
	val backgroundColor: Int,
) : AbstractItem<ColorChoiceUI.ViewHolder>() {
	/**
	 * If this is in the chapter reader or not
	 */
	var inReader: Boolean = false


	override fun toString(): String = try {
		"$identifier,${name.serializeToString()},$textColor,$backgroundColor"
	} catch (e: Exception) {
		"$identifier,FAILED,$textColor,$backgroundColor"
	}

	/**
	 * View Holder
	 * @param view view
	 */
	class ViewHolder(val view: View) : FastAdapter.ViewHolder<ColorChoiceUI>(view) {
		override fun bindView(item: ColorChoiceUI, payloads: List<Any>) {
			view.findViewById<AppCompatTextView>(R.id.textView).apply {
				setTextColor(item.textColor)
				setBackgroundColor(item.backgroundColor)
			}
			view.findViewById<MaterialCardView>(R.id.materialCardView).apply {
				strokeWidth = if (item.isSelected) SELECTED_STROKE_WIDTH else 0
			}

			if (item.inReader)
				view.findViewById<ImageButton>(R.id.removeButton).apply {
					visibility = View.GONE
				}
		}

		override fun unbindView(item: ColorChoiceUI) {
			view.findViewById<ImageButton>(R.id.removeButton).apply {
				setOnClickListener(null)
				visibility = View.VISIBLE
			}
			view.findViewById<MaterialCardView>(R.id.materialCardView).apply {
				strokeWidth = 0
			}
		}
	}

	override val layoutRes: Int = R.layout.reader_theme_selection_item
	override val type: Int = 1
	override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)
}