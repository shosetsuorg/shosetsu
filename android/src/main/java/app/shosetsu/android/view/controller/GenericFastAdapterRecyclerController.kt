package app.shosetsu.android.view.controller

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerRecyclerBinding
import com.mikepenz.fastadapter.items.AbstractItem

abstract class GenericFastAdapterRecyclerController<ITEM : AbstractItem<*>> :
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