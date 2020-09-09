package com.github.doomsdayrs.apps.shosetsu.view.base

import android.os.Bundle
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.mikepenz.fastadapter.items.AbstractItem

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
 * 02 / 07 / 2020
 */
abstract class FastAdapterRecyclerController<ITEM> : RecyclerController<FastAdapter<ITEM>, ITEM>
		where ITEM : AbstractItem<*> {

	/**
	 * This contains the items
	 */
	open val itemAdapter: ItemAdapter<ITEM> by lazy { ItemAdapter() }
	override var adapter: FastAdapter<ITEM>?
		get() = fastAdapter
		set(value) {}

	/**
	 * This is the adapter
	 */
	open val fastAdapter: FastAdapter<ITEM> by lazy { FastAdapter.with(itemAdapter) }
	override var recyclerArray: ArrayList<ITEM>
		get() = ArrayList(itemAdapter.itemList.items)
		set(value) {}

	constructor() : super()
	constructor(args: Bundle) : super(args)

	override fun setupRecyclerView() {
		super.setupRecyclerView()
		setupFastAdapter()
	}

	/**
	 * Allows child classes to manipulate the fast adapter
	 */
	open fun setupFastAdapter() {}
	override fun updateUI(newList: List<ITEM>) {
		FastAdapterDiffUtil[itemAdapter] = FastAdapterDiffUtil.calculateDiff(itemAdapter, newList)
	}

	override fun difAreItemsTheSame(oldItem: ITEM, newItem: ITEM): Boolean =
			difAreContentsTheSame(oldItem, newItem)

	override fun createRecyclerAdapter(): FastAdapter<ITEM> = fastAdapter
}