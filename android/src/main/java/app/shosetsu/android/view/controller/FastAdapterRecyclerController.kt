package app.shosetsu.android.view.controller

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil

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
abstract class FastAdapterRecyclerController<VB, ITEM> :
	RecyclerController<FastAdapter<ITEM>, ITEM, VB>
		where ITEM : GenericItem, VB : ViewBinding {

	/**
	 * This contains the items
	 */
	open val itemAdapter: ItemAdapter<ITEM> by lazy { ItemAdapter() }

	override var adapter: FastAdapter<ITEM>?
		get() = fastAdapter
		set(@Suppress("UNUSED_PARAMETER") value) {}

	/**
	 * This is the adapter
	 */
	open val fastAdapter: FastAdapter<ITEM> by lazy { FastAdapter.with(itemAdapter) }

	override var recyclerArray: ArrayList<ITEM>
		get() = ArrayList(itemAdapter.itemList.items)
		set(@Suppress("UNUSED_PARAMETER") value) {}

	constructor() : super()
	constructor(args: Bundle) : super(args)

	override fun setupRecyclerView() {
		super.setupRecyclerView()
		adapter?.setupFastAdapter()
	}

	override fun createRecyclerAdapter(): FastAdapter<ITEM> = fastAdapter

	/**
	 * Allows child classes to manipulate the fast adapter
	 */
	open fun FastAdapter<ITEM>.setupFastAdapter() {}

	/** @param result [HResult], if [HResult.Success] then updates UI */
	fun <T : GenericItem> handleRecyclerUpdate(
		itemAdapter: ItemAdapter<T>,
		showEmpty: () -> Unit,
		hideEmpty: () -> Unit,
		result: HResult<List<T>>
	) = result.handle(
		onLoading = { showLoading() },
		onError = { handleErrorResult(it) },
		onEmpty = { showEmpty() }
	) { newList ->
		updateUI(itemAdapter, showEmpty, hideEmpty, newList)
	}


	override fun updateUI(newList: List<ITEM>) {
		if (newList.isEmpty()) {
			showEmpty()
		} else hideEmpty()
		FastAdapterDiffUtil[itemAdapter] = FastAdapterDiffUtil.calculateDiff(itemAdapter, newList)
	}

	@Suppress("MemberVisibilityCanBePrivate")
	fun <T : GenericItem> updateUI(
		itemAdapter: ItemAdapter<T>,
		showEmpty: () -> Unit,
		hideEmpty: () -> Unit,
		newList: List<T>
	) {
		if (newList.isEmpty()) {
			showEmpty()
		} else hideEmpty()
		FastAdapterDiffUtil[itemAdapter] = FastAdapterDiffUtil.calculateDiff(itemAdapter, newList)
	}

	override fun difAreItemsTheSame(oldItem: ITEM, newItem: ITEM): Boolean =
		difAreContentsTheSame(oldItem, newItem)
}