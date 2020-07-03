package com.github.doomsdayrs.apps.shosetsu.view.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
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
abstract class FastAdapterRecyclerController<ITEM : AbstractItem<*>>(
		bundle: Bundle
) : RecyclerController<FastAdapter<ITEM>, ITEM>() {
	constructor() : this(bundleOf())

	/**
	 * This contains the items
	 */
	open val itemAdapter: ItemAdapter<ITEM> by lazy { ItemAdapter<ITEM>() }

	/**
	 * This is the adapter
	 */
	open val fastAdapter: FastAdapter<ITEM> by lazy { FastAdapter.with(itemAdapter) }

	override var recyclerArray: ArrayList<ITEM>
		get() = ArrayList(itemAdapter.itemList.items)
		set(value) {}

	override fun handleRecyclerUpdate(result: HResult<List<ITEM>>) {
		when (result) {
			is HResult.Loading -> showLoading()
			is HResult.Success -> updateFastAdapterUI(result.data)
			is HResult.Error -> showError(result)
		}
	}

	/**
	 * This replaces [updateUI]
	 */
	@CallSuper
	open fun updateFastAdapterUI(list: List<ITEM>) {
		val diffToolCallBack = RecyclerDiffToolCallBack(list, itemAdapter.itemList.items)
		val callback = DiffUtil.calculateDiff(diffToolCallBack)
		callback.dispatchUpdatesTo(fastAdapter)
		itemAdapter.clear()
		itemAdapter.add(list)
	}

	override fun createRecyclerAdapter(): FastAdapter<ITEM> = fastAdapter
}