package app.shosetsu.android.view.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import app.shosetsu.android.common.ext.context
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
 *
 * @param AD RecyclerView adapter type
 * @param IT Item the [AD] uses to make the UI
 * @param VB ViewBinding of the UI
 */
abstract class RecyclerController<AD, IT, VB> : ViewedController<VB>
		where AD : RecyclerView.Adapter<*>,
		      VB : ViewBinding {


	lateinit var recyclerView: RecyclerView

	/**
	 * Adapter of the RecyclerView
	 */
	open var adapter: AD? = null

	/**
	 * Recycler array
	 */
	open var recyclerArray: ArrayList<IT> = arrayListOf()

	constructor() : super()
	constructor(args: Bundle) : super(args)

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?,
	): View {
		setViewTitle()
		binding = bindView(inflater)
		setupRecyclerView()
		return binding.root
	}

	/** @param result [HResult], if [HResult.Success] then updates UI */
	open fun handleRecyclerUpdate(result: HResult<List<IT>>) = when (result) {
		HResult.Loading -> showLoading()
		is HResult.Success -> updateUI(result.data)
		is HResult.Error -> handleErrorResult(result)
		HResult.Empty -> showEmpty()
		else -> {
		}
	}

	/**
	 * Convenience method to observe a [LiveData] containing data
	 * that matches what [handleRecyclerUpdate] needs
	 */
	fun LiveData<HResult<List<IT>>>.observeRecyclerUpdates() =
		observe(this@RecyclerController) {
			handleRecyclerUpdate(it)
		}

	abstract override fun onViewCreated(view: View)

	/**
	 * Allows manipulation of the recyclerview
	 *
	 * Invoked after [onCreateView]
	 */
	@CallSuper
	open fun setupRecyclerView() {
		recyclerView.layoutManager = createLayoutManager()
		adapter = createRecyclerAdapter()
		recyclerView.adapter = adapter
	}

	/**
	 * Convenience method to change the layout manager of the recyclerview post UI create
	 *
	 * Applies the [layoutManager] and the same old [adapter] to the [recyclerView]
	 */
	fun updateLayoutManager(layoutManager: LinearLayoutManager) {
		recyclerView.layoutManager = layoutManager
		recyclerView.adapter = adapter
	}

	/**
	 * Called when the [HResult] for the data is empty
	 */
	open fun showEmpty() {}

	/**
	 * Undo [showEmpty]
	 */
	open fun hideEmpty() {}

	/**
	 * What is the layout manager
	 */
	open fun createLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(context)

	/**
	 * Creates the adapter for the recycler view to use
	 */
	abstract fun createRecyclerAdapter(): AD

	/**
	 * The data for this view is loading
	 */
	open fun showLoading() {}

	/**
	 * Updates the UI with a new list
	 */
	open fun updateUI(newList: List<IT>) {
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
	open fun difAreContentsTheSame(oldItem: IT, newItem: IT): Boolean =
		//Log.d(logID(), "$oldItem v $newItem = $b")
		oldItem == newItem

	/**
	 * If the identification of two items are the same
	 */
	abstract fun difAreItemsTheSame(oldItem: IT, newItem: IT): Boolean


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
		private val newList: List<IT> = arrayListOf(),
		private val oldList: List<IT> = recyclerArray,
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