package app.shosetsu.android.view.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.github.doomsdayrs.apps.shosetsu.R
import kotlin.math.max


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
 * 03 / 10 / 2020
 */
class StickyHeaderDecor(
	private var context: Context,
	private var sectionCallback: SectionCallback,
	private var headerOffset: Int = context.resources.getDimensionPixelSize(R.dimen.header_height),
	private var sticky: Boolean = true,
) : ItemDecoration() {
	private var headerView: View? = null
	private var tvTitle: AppCompatTextView? = null

	override fun getItemOffsets(
		outRect: Rect,
		view: View,
		parent: RecyclerView,
		state: RecyclerView.State
	) {
		super.getItemOffsets(outRect, view, parent, state)
		val pos = parent.getChildAdapterPosition(view)
		if (sectionCallback.isSection(pos)) {
			outRect.top = headerOffset
		}
	}

	override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
		super.onDrawOver(c, parent, state)
		if (headerView == null) {
			headerView = inflateHeader(parent)
			tvTitle = headerView?.findViewById(R.id.header_title)
			fixLayoutSize(headerView!!, parent)
		}
		var prevTitle = ""
		for (i in 0 until parent.childCount) {
			val child: View = parent.getChildAt(i)
			val childPos = parent.getChildAdapterPosition(child)
			val title = sectionCallback.getSectionHeaderName(childPos)
			tvTitle!!.text = title
			if (!prevTitle.equals(
					title,
					ignoreCase = true
				) || sectionCallback.isSection(childPos)
			) {
				drawHeader(c, child, headerView!!)
				prevTitle = title
			}
		}
	}

	/**
	 * Draws the header onto the canvas
	 */
	private fun drawHeader(c: Canvas, child: View, headerView: View) {
		c.save()
		if (sticky) {
			c.translate(0f, max(0, child.top - headerView.height).toFloat())
		} else {
			c.translate(0f, ((child.top - headerView.height).toFloat()))
		}
		headerView.draw(c)
		c.restore()
	}

	private fun fixLayoutSize(view: View, viewGroup: ViewGroup) {
		val widthSpec: Int =
			View.MeasureSpec.makeMeasureSpec(viewGroup.width, View.MeasureSpec.EXACTLY)
		val heightSpec: Int =
			View.MeasureSpec.makeMeasureSpec(viewGroup.height, View.MeasureSpec.UNSPECIFIED)
		val childWidth = ViewGroup.getChildMeasureSpec(
			widthSpec,
			viewGroup.paddingLeft + viewGroup.paddingRight,
			view.layoutParams.width
		)
		val childHeight = ViewGroup.getChildMeasureSpec(
			heightSpec,
			viewGroup.paddingTop + viewGroup.paddingBottom,
			view.layoutParams.height
		)
		view.measure(childWidth, childHeight)
		view.layout(0, 0, view.measuredWidth, view.measuredHeight)
	}

	private fun inflateHeader(recyclerView: RecyclerView): View =
		LayoutInflater.from(context).inflate(R.layout.row_section_header, recyclerView, false)

	interface SectionCallback {
		/**
		 *
		 */
		fun isSection(pos: Int): Boolean
		fun getSectionHeaderName(pos: Int): String
	}

}