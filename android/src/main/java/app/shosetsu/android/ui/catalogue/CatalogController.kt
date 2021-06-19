package app.shosetsu.android.ui.catalogue

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_EXTENSION
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.catalogue.listeners.CatalogueSearchQuery
import app.shosetsu.android.ui.novel.NovelController
import app.shosetsu.android.view.controller.FastAdapterRecyclerController
import app.shosetsu.android.view.controller.base.BottomMenuController
import app.shosetsu.android.view.controller.base.ExtendedFABController
import app.shosetsu.android.view.controller.base.PushCapableController
import app.shosetsu.android.view.uimodels.model.ProgressItem
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.view.widget.SlidingUpBottomMenu
import app.shosetsu.android.viewmodel.abstracted.ICatalogViewModel
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.enums.NovelCardType.COMPRESSED
import com.bluelinelabs.conductor.Controller
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerCatalogueBinding
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class CatalogController(
	/** data bundle uwu */
	val bundle: Bundle,
) : FastAdapterRecyclerController<ControllerCatalogueBinding, ACatalogNovelUI>(bundle),
	PushCapableController, ExtendedFABController, BottomMenuController {

	override var pushController: (Controller) -> Unit = {}

	/***/
	val viewModel: ICatalogViewModel by viewModel()
	private val progressAdapter by lazy { ItemAdapter<ProgressItem>() }


	override val fastAdapter: FastAdapter<ACatalogNovelUI> by lazy {
		FastAdapter<ACatalogNovelUI>().apply {
			addAdapter(0, itemAdapter)
			@Suppress("UNCHECKED_CAST")
			addAdapter(1, progressAdapter as ItemAdapter<ACatalogNovelUI>)
		}
	}

	init {
		setHasOptionsMenu(true)
	}

	override fun onDestroy() {
		logV("")
		super.onDestroy()
		viewModel.destroy()
	}

	override fun createLayoutManager(): RecyclerView.LayoutManager {
		return when (viewModel.novelCardTypeLive.value) {
			COMPRESSED -> LinearLayoutManager(
				context,
				VERTICAL,
				false
			)
			else -> GridLayoutManager(
				context,
				context!!.resources.let {
					val density = it.displayMetrics.density
					val widthPixels = it.displayMetrics.widthPixels
					when (it.configuration.orientation) {
						Configuration.ORIENTATION_LANDSCAPE -> {
							viewModel.calculateHColumnCount(
								widthPixels,
								density,
								200f
							)
						}
						else -> {
							viewModel.calculatePColumnCount(
								widthPixels,
								density,
								200f
							)
						}
					}
				},
				VERTICAL,
				false
			)
		}
	}

	override fun FastAdapter<ACatalogNovelUI>.setupFastAdapter() {
		fastAdapter.apply {
			setOnClickListener { _, _, item, _ ->
				pushController(
					NovelController(
						bundleOf(
							BUNDLE_NOVEL_ID to item.id,
							BUNDLE_EXTENSION to bundle.getInt(BUNDLE_EXTENSION)
						)
					)
				)
				true
			}
			onLongClickListener = { _, _, i, _ ->
				logI("Adding novel to background")
				viewModel.backgroundNovelAdd(i.id)
				toast(R.string.controller_catalogue_toast_background_add)
				true
			}
		}
	}

	override fun onViewCreated(view: View) {
		viewModel.setExtensionID(bundle.getInt(BUNDLE_EXTENSION))
		binding.swipeRefreshLayout.setOnRefreshListener {
			logV("Refreshing")
			viewModel.resetView()
		}
		setupObservers()
		setupRecyclerView()
	}

	override fun setupRecyclerView() {
		recyclerView.setHasFixedSize(false)
		//recyclerView.addOnScrollListener(CatalogueHitBottom(viewModel))
		recyclerView.addOnScrollListener(object : EndlessRecyclerOnScrollListener(progressAdapter) {
			override fun onLoadMore(currentPage: Int) {
				// these are throwing exceptions that cant be catched, just ignore em
				launchUI {
					progressAdapter.clear()
					progressAdapter.add(ProgressItem())
				}

				viewModel.loadMore()
			}
		})
		super.setupRecyclerView()
	}

	/***/
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		menu.clear()
		inflater.inflate(R.menu.toolbar_catalogue, menu)
		menu.findItem(R.id.search_item)?.let { searchItem ->
			if (viewModel.hasSearchLive.value != true) {
				searchItem.isVisible = false
				return@let
			}
			(searchItem.actionView as SearchView).apply {
				setOnQueryTextListener(CatalogueSearchQuery(this@CatalogController))
				setOnCloseListener {
					logV("closing search view")
					viewModel.applyQuery("")
					viewModel.resetView()
					true
				}
			}
		}
	}

	override fun updateUI(newList: List<ACatalogNovelUI>) {
		super.updateUI(newList)
		binding.swipeRefreshLayout.isRefreshing = false
	}

	override fun handleErrorResult(e: HResult.Error) {
		viewModel.reportError(e)
	}

	override fun showLoading() {
		super.showLoading()
		if (recyclerArray.isEmpty() && !binding.swipeRefreshLayout.isRefreshing)
			binding.swipeRefreshLayout.isRefreshing = true
	}

	private fun setupObservers() {
		viewModel.itemsLive.observeRecyclerUpdates()

		viewModel.extensionName.handleObserve(this, onLoading = {
			setViewTitle(getString(R.string.loading))
		}) {
			setViewTitle(it)
			if (recyclerArray.isEmpty()) viewModel.resetView()
		}

		viewModel.hasSearchLive.observe {
			activity?.invalidateOptionsMenu()
		}
		viewModel.filterItemsLive.observe(this) {
		}
	}

	override fun bindView(inflater: LayoutInflater): ControllerCatalogueBinding =
		ControllerCatalogueBinding.inflate(inflater).also { recyclerView = it.recyclerView }

	override fun manipulateFAB(fab: ExtendedFloatingActionButton) {
		fab.setIconResource(R.drawable.filter)
	}

	override var bottomMenuRetriever: () -> SlidingUpBottomMenu? = { null }

	override fun getBottomMenuView(): View = CatalogFilterMenuBuilder(this).build()
}
