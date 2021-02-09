package app.shosetsu.android.common.ext

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.listeners.ClickEventHook

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
