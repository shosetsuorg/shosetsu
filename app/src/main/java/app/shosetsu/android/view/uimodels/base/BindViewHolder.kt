package app.shosetsu.android.view.uimodels.base

import android.view.View
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem

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
 */
abstract class BindViewHolder<ITEM, VB>(val view: View) : FastAdapter.ViewHolder<ITEM>(view)
		where  ITEM : GenericItem, VB : ViewBinding {
	abstract val binding: VB

	abstract fun VB.bindView(item: ITEM, payloads: List<Any>)
	abstract fun VB.unbindView(item: ITEM)

	@CallSuper
	override fun bindView(item: ITEM, payloads: List<Any>) {
		binding.bindView(item, payloads)
	}

	@CallSuper
	override fun unbindView(item: ITEM) {
		binding.unbindView(item)
	}
}