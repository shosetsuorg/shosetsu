package app.shosetsu.android.view.controller

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerRecyclerWithBottomMenuBinding
import com.mikepenz.fastadapter.items.AbstractItem

abstract class BottomMenuBasicFastAdapterRecyclerController<ITEM : AbstractItem<*>> :
	FastAdapterRecyclerController<ControllerRecyclerWithBottomMenuBinding, ITEM> {

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


	override fun bindView(inflater: LayoutInflater): ControllerRecyclerWithBottomMenuBinding =
		ControllerRecyclerWithBottomMenuBinding.inflate(inflater)
			.also { recyclerView = it.recyclerView }
}