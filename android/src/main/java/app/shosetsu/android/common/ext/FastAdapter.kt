package app.shosetsu.android.common.ext

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.select.selectExtension
import com.mikepenz.fastadapter.utils.AdapterPredicate

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
 * 15 / 07 / 2020
 */

fun <T : GenericItem> FastAdapter<T>.setOnPreClickListener(listener: ClickListener<T>) {
	onPreClickListener = listener
}

fun <T : GenericItem> FastAdapter<T>.setOnClickListener(listener: ClickListener<T>) {
	onClickListener = listener
}

/**
 * Launches on the IO thread a selection task, which will effect the UI in return
 */
fun <ITEM : GenericItem> FastAdapter<ITEM>.selectBetween(
	itemAdapter: ItemAdapter<ITEM>,
	selected: List<ITEM> = getSelectExtension().selectedItems.toList(),
) = launchIO {
	selectExtension {
		val adapterList = itemAdapter.adapterItems

		if (adapterList.isEmpty()) return@launchIO

		val first = adapterList.indexOfFirst { it.identifier == selected.firstOrNull()?.identifier }
		val last = adapterList.indexOfFirst { it.identifier == selected.lastOrNull()?.identifier }

		// Completely ignore the task if the following are true
		if (first == -1 || last == -1 || first == last || first + 1 == last) {
			return@launchIO
		}

		val smallest: Int
		val largest: Int
		when {
			first > last -> {
				largest = first
				smallest = last
			}
			else -> {
				smallest = first
				largest = last
			}
		}
		adapterList.subList(smallest, largest)
			.map { this@selectBetween.getPosition(it) }
			.let { launchUI { select(it) } }
	}
}

/**
 * Inverts the selection of items in this [FastAdapter]
 */
fun <ITEM : GenericItem> FastAdapter<ITEM>.invertSelection() {
	recursive(object : AdapterPredicate<ITEM> {
		override fun apply(
			lastParentAdapter: IAdapter<ITEM>,
			lastParentPosition: Int,
			item: ITEM,
			position: Int
		): Boolean {
			if (item.isSelected) {
				getSelectExtension().deselect(item)
			} else {
				getSelectExtension().select(
					adapter = lastParentAdapter,
					item = item,
					position = RecyclerView.NO_POSITION,
					fireEvent = false,
					considerSelectableFlag = true
				)
			}
			return false
		}
	}, false)
	notifyDataSetChanged()
}


inline fun <ITEM : GenericItem, reified VH : RecyclerView.ViewHolder> FastAdapter<ITEM>.hookClickEvent(
	crossinline bind: (VH) -> View? = { null },
	crossinline bindMany: (VH) -> List<View>? = { null },
	crossinline onClick: (
		@ParameterName("v") View,
		@ParameterName("position") Int,
		@ParameterName("fastAdapter") FastAdapter<ITEM>,
		@ParameterName("item") ITEM
	) -> Unit
) = addEventHook(object : ClickEventHook<ITEM>() {
	override fun onClick(
		v: View,
		position: Int,
		fastAdapter: FastAdapter<ITEM>,
		item: ITEM
	) =
		onClick(v, position, fastAdapter, item)

	override fun onBind(viewHolder: RecyclerView.ViewHolder): View? =
		if (viewHolder is VH) bind(viewHolder) else null

	override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? =
		if (viewHolder is VH) bindMany(viewHolder) else null
}
)
