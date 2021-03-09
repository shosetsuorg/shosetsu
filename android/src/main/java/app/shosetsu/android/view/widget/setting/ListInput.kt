package app.shosetsu.android.view.widget.setting

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import app.shosetsu.android.common.ext.createUI
import app.shosetsu.lib.Filter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.DrawerLayoutExpandableBinding
import com.github.doomsdayrs.apps.shosetsu.databinding.DrawerLayoutExpandableBinding.inflate

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
 * 09 / 03 / 2021
 */
class ListInput @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	override val filterID: Int = -1
) : FilterSettingWidget<Map<Int, Any>>, FrameLayout(context, attrs) {


	private val drawerLayoutExpandableBinding: DrawerLayoutExpandableBinding =
		inflate(LayoutInflater.from(context), this, true)

	private var expanded: Boolean = false

	private val internalMap = hashMapOf<Int, Any>()

	override var result: Map<Int, Any>
		get() = internalMap
		set(value) {}

	constructor(
		filter: Filter.List,
		context: Context,
		attrs: AttributeSet? = null
	) : this(
		context,
		attrs,
		filterID = filter.id
	) {
		drawerLayoutExpandableBinding.apply {
			filter.filters.toList().createUI(root.context).forEach {
				list.addView(it)
			}

			expandableBar.apply {
				expandableBar.setOnClickListener { toggleBar() }
				title.setOnClickListener { toggleBar() }
				imageView.setOnClickListener { toggleBar() }
			}
		}

		internalMap.putAll(filter.state)
	}

	private fun toggleBar() {
		drawerLayoutExpandableBinding.apply {
			expandableBar.imageView.apply {
				expanded = !expanded

				setImageResource(
					if (expanded) R.drawable.expand_less else R.drawable.expand_more
				)
			}
			list.isVisible = expanded
			bottomDivider.divider.isVisible = expanded
		}
	}
}