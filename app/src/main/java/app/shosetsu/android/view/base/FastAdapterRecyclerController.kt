package app.shosetsu.android.view.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.launchUI
import app.shosetsu.android.common.ext.logV
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerRecyclerBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
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
abstract class FastAdapterRecyclerController<VB, ITEM> : RecyclerController<FastAdapter<ITEM>, ITEM, VB>
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
		setupFastAdapter()
	}

	override fun createRecyclerAdapter(): FastAdapter<ITEM> = fastAdapter

	/**
	 * Allows child classes to manipulate the fast adapter
	 */
	open fun setupFastAdapter() {}

	/** @param result [HResult], if [HResult.Success] then updates UI */
	fun <T : GenericItem> handleRecyclerUpdate(
			itemAdapter: ItemAdapter<T>,
			showEmpty: () -> Unit,
			hideEmpty: () -> Unit,
			result: HResult<List<T>>
	) {
		when (result) {
			is HResult.Loading -> showLoading()
			is HResult.Success -> updateUI(itemAdapter, showEmpty, hideEmpty, result.data)
			is HResult.Error -> showError(result)
			is HResult.Empty -> showEmpty()
		}
	}


	override fun updateUI(newList: List<ITEM>) {
		if (newList.isEmpty()) showEmpty() else hideEmpty()
		launchIO {
			logV("Calculating result")
			val result = FastAdapterDiffUtil.calculateDiff(itemAdapter, newList)
			logV("Result calculated, Dispatching on UI")
			launchUI { FastAdapterDiffUtil[itemAdapter] = result }
		}
	}

	fun <T : GenericItem> updateUI(
			itemAdapter: ItemAdapter<T>,
			showEmpty: () -> Unit,
			hideEmpty: () -> Unit,
			newList: List<T>
	) {
		if (newList.isEmpty()) showEmpty() else hideEmpty()
		launchIO {
			logV("Calculating result")
			val result = FastAdapterDiffUtil.calculateDiff(itemAdapter, newList)
			logV("Result calculated, Dispatching on UI")
			launchUI { FastAdapterDiffUtil[itemAdapter] = result }
		}
	}

	override fun difAreItemsTheSame(oldItem: ITEM, newItem: ITEM): Boolean =
			difAreContentsTheSame(oldItem, newItem)


	abstract class BasicFastAdapterRecyclerController<ITEM : AbstractItem<*>> :
			FastAdapterRecyclerController<ControllerRecyclerBinding, ITEM> {

		constructor() : super()
		constructor(args: Bundle) : super(args)

		@CallSuper
		override fun showEmpty() {
			binding.recyclerView.isVisible = false
		}


		@CallSuper
		override fun hideEmpty() {
			binding.recyclerView.isVisible = true
			binding.emptyDataView.hide()
		}

		override fun bindView(inflater: LayoutInflater): ControllerRecyclerBinding =
				ControllerRecyclerBinding.inflate(inflater).also { recyclerView = it.recyclerView }
	}

}