package com.github.doomsdayrs.apps.shosetsu.view.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID

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
 * 29 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 * <p>
 *     A simple controller that is just a recyclerView.
 *     Default will fill with a LinearLayoutManager
 * </p>
 */
abstract class RecyclerController<T : RecyclerView.Adapter<*>, V>(bundle: Bundle)
	: ViewedController(bundle) {
	constructor() : this(bundleOf())

	@LayoutRes
	override val layoutRes: Int = R.layout.recycler_controller

	/**
	 * What is the resource ID of the formatter
	 */
	@IdRes
	open val resourceID: Int = R.id.recyclerView

	/**
	 * RecyclerView
	 */
	var recyclerView: RecyclerView? = null

	/**
	 * Adapter of the RecyclerView
	 */
	open var adapter: T? = null

	/**
	 * Recycler array
	 */
	open var recyclerArray: ArrayList<V> = arrayListOf()

	override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup,
			savedViewState: Bundle?
	): View {
		val view = createViewInstance(inflater, container)
		recyclerView = view.findViewById(resourceID)!!
		onViewCreated(view)
		setupRecyclerView()
		return view
	}

	/** @param result [HResult], if [HResult.Success] then updates UI */
	open fun handleRecyclerUpdate(result: HResult<List<V>>) {
		when (result) {
			is HResult.Loading -> showLoading()
			is HResult.Success -> updateUI(result.data)
			is HResult.Error -> showError(result)
		}
	}

	abstract override fun onViewCreated(view: View)

	/**
	 * Allows manipulation of the recyclerview
	 */
	@CallSuper
	open fun setupRecyclerView() {
		Log.d(logID(), "Setup of recyclerView")
		recyclerView?.layoutManager = createLayoutManager()
		adapter = createRecyclerAdapter()
		recyclerView?.adapter = adapter
	}

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
		adapter?.let {
			DiffUtil.calculateDiff(RecyclerDiffToolCallBack(
					newList = newList,
					oldList = recyclerArray
			)).dispatchUpdatesTo(it)
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

	/**
	 * Call back to update ui of [RecyclerController]
	 * @param oldList Old list
	 * @param newList New List
	 */
	inner class RecyclerDiffToolCallBack(
			private val newList: List<V> = arrayListOf(),
			private val oldList: List<V> = recyclerArray
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