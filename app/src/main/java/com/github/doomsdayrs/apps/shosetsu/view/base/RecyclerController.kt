package com.github.doomsdayrs.apps.shosetsu.view.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

	/**
	 * Call back to update ui of [RecyclerController]
	 * @param oldList Old list
	 * @param newList New List
	 */
	abstract inner class RecyclerDiffToolCallBack(
			var newList: List<V> = arrayListOf(),
			val oldList: List<V> = recyclerArray
	) : DiffUtil.Callback() {
		override fun getOldListSize() = oldList.size
		override fun getNewListSize() = newList.size
	}

	/**
	 *  DiffToolCallback to be used
	 */
	abstract val diffToolCallBack: RecyclerDiffToolCallBack

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
	var adapter: T? = null

	/**
	 * Recycler array
	 */
	var recyclerArray: ArrayList<V> = arrayListOf()

	override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup,
			savedViewState: Bundle?
	): View {
		val view = createViewInstance(inflater, container)
		recyclerView = view.findViewById(resourceID)!!
		recyclerView!!.layoutManager = LinearLayoutManager(context)
		onViewCreated(view)
		recyclerView?.adapter = adapter
		return view
	}

	/**
	 *
	 */
	fun handleRecyclerUpdate(result: HResult<List<V>>) {
		when (result) {
			is HResult.Loading -> {
				Log.i(logID(), "Loading UWU")
			}
			is HResult.Success -> updateUI(result.data)
			is HResult.Error -> {
				Log.i(logID(), "ERROR OWO ${result.message}")
			}
		}
	}


	abstract override fun onViewCreated(view: View)

	/**
	 * Updates the UI with a new list
	 */
	fun updateUI(list: List<V>) {
		diffToolCallBack.newList = list
		val callback = DiffUtil.calculateDiff(diffToolCallBack)
		recyclerArray.clear()
		recyclerArray.addAll(list)
		adapter?.let { callback.dispatchUpdatesTo(it) }
	}
}