package app.shosetsu.android.view.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import app.shosetsu.android.common.ext.context
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.common.ext.logV
import app.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerRecyclerBinding
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerRecyclerBinding.inflate

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
 * 29 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 * <p>
 *     A simple controller that is just a recyclerView.
 *     Default will fill with a LinearLayoutManager
 * </p>
 */
abstract class RecyclerController<T, V, VB> : ViewedController<VB>
		where T : RecyclerView.Adapter<*>,
		      VB : ViewBinding {


	lateinit var recyclerView: RecyclerView

	/**
	 * Adapter of the RecyclerView
	 */
	open var adapter: T? = null

	/**
	 * Recycler array
	 */
	open var recyclerArray: ArrayList<V> = arrayListOf()

	constructor() : super()
	constructor(args: Bundle) : super(args)

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?,
	): View {
		setViewTitle()
		binding = bindView(inflater)
		onViewCreated(binding.root)
		setupRecyclerView()
		return binding.root
	}

	/** @param result [HResult], if [HResult.Success] then updates UI */
	open fun handleRecyclerUpdate(result: HResult<List<V>>) {
		when (result) {
			is HResult.Loading -> showLoading()
			is HResult.Success -> updateUI(result.data)
			is HResult.Error -> handleErrorResult(result)
			is HResult.Empty -> showEmpty()
		}
	}

	abstract override fun onViewCreated(view: View)

	/**
	 * Allows manipulation of the recyclerview
	 */
	@CallSuper
	open fun setupRecyclerView() {
		Log.d(logID(), "Setup of recyclerView")
		recyclerView.layoutManager = createLayoutManager()
		adapter = createRecyclerAdapter()
		recyclerView.adapter = adapter
	}

	open fun showEmpty() {}
	open fun hideEmpty() {}

	/**
	 * What is the layout manager
	 */
	open fun createLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(context)

	/**
	 * Creates the adapter for the recycler view to use
	 */
	abstract fun createRecyclerAdapter(): T

	/**
	 * The data for this view is loading
	 */
	@CallSuper
	open fun showLoading() {
		Log.i(logID(), "Loading UWU")
	}

	/**
	 * Updates the UI with a new list
	 */
	open fun updateUI(newList: List<V>) {
		if (newList.isEmpty()) showEmpty() else hideEmpty()
		adapter?.let {
			DiffUtil.calculateDiff(
				RecyclerDiffToolCallBack(
					newList = newList,
					oldList = recyclerArray
				)
			).dispatchUpdatesTo(it)
		}
		recyclerArray.clear()
		recyclerArray.addAll(newList)
	}

	/**
	 * If the contents of two items are the same
	 */
	open fun difAreContentsTheSame(oldItem: V, newItem: V): Boolean =
		//Log.d(logID(), "$oldItem v $newItem = $b")
		oldItem == newItem

	/**
	 * If the identification of two items are the same
	 */
	abstract fun difAreItemsTheSame(oldItem: V, newItem: V): Boolean


	abstract class BasicRecyclerController<T : RecyclerView.Adapter<*>, V>
		: RecyclerController<T, V, ControllerRecyclerBinding> {

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

		override fun bindView(inflater: LayoutInflater): ControllerRecyclerBinding {
			return inflate(inflater).also { recyclerView = it.recyclerView }
		}
	}

	/**
	 * Call back to update ui of [RecyclerController]
	 * @param oldList Old list
	 * @param newList New List
	 */
	inner class RecyclerDiffToolCallBack(
		private val newList: List<V> = arrayListOf(),
		private val oldList: List<V> = recyclerArray,
	) : DiffUtil.Callback() {
		override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
			this@RecyclerController.difAreContentsTheSame(
				oldItem = oldList[oldItemPosition],
				newItem = newList[newItemPosition]
			)

		override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
			this@RecyclerController.difAreItemsTheSame(
				oldItem = oldList[oldItemPosition],
				newItem = newList[newItemPosition]
			)

		override fun getOldListSize(): Int = oldList.size
		override fun getNewListSize(): Int = newList.size
	}
}