package app.shosetsu.android.view.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import app.shosetsu.android.activity.MainActivity
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
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?
	): View {
		return super.onCreateView(inflater, container, savedViewState).also {
			(activity as? MainActivity)?.holdAtBottom(binding.bottomMenu)
		}
	}


	@CallSuper
	override fun onDestroyView(view: View) {
		super.onDestroyView(view)
		(activity as? MainActivity)?.removeHoldAtBottom(binding.bottomMenu)
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