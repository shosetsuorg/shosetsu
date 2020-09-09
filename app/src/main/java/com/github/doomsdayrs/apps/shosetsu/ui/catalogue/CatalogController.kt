package com.github.doomsdayrs.apps.shosetsu.ui.catalogue

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_FORMATTER
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.*
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueHitBottom
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueSearchQuery
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.view.base.FastAdapterRecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.base.SecondDrawerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.catlog.ACatalogNovelUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.ICatalogViewModel
import com.google.android.material.navigation.NavigationView
import org.kodein.di.generic.instance

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
) : FastAdapterRecyclerController<ACatalogNovelUI>(bundle), SecondDrawerController {

	override val layoutRes: Int = R.layout.catalogue

	@Attach(R.id.swipeRefreshLayout)
	var swipeRefreshLayout: SwipeRefreshLayout? = null

	private var searchView: SearchView? = null

	private var navigationView: NavigationView? = null
	private var drawerLayout: DrawerLayout? = null


	/***/
	val viewModel: ICatalogViewModel by viewModel()
	private val settings by instance<ShosetsuSettings>()

	/** If the user is currently searching something up*/
	var isInSearch: Boolean = false

	/** If the user is currently viewing query data*/
	var isQuery: Boolean = false

	init {
		setHasOptionsMenu(true)
	}

	override fun onDestroy() {
		super.onDestroy()
		searchView = null
	}

	override fun createLayoutManager(): RecyclerView.LayoutManager {
		return if (settings.novelCardType == 0) GridLayoutManager(
				context,
				settings.calculateColumnCount(context!!, 200f),
				VERTICAL,
				false
		)
		else LinearLayoutManager(
				context,
				VERTICAL,
				false
		)
	}

	override fun setupFastAdapter() {
		super.setupFastAdapter()
		fastAdapter.apply {
			setOnClickListener { _, _, item, _ ->
				router.pushController(NovelController(
						bundleOf(
								BUNDLE_NOVEL_ID to item.id,
								BUNDLE_FORMATTER to bundle.getInt(BUNDLE_FORMATTER)
						)
				).withFadeTransaction())
				true
			}
			onLongClickListener = { _, _, i, _ ->
				viewModel.backgroundNovelAdd(i.id)
				true
			}
		}
	}

	override fun onViewCreated(view: View) {
		viewModel.setFormatterID(bundle.getInt(BUNDLE_FORMATTER))
		swipeRefreshLayout?.setOnRefreshListener { viewModel.resetView() }
		setupObservers()
		setupRecyclerView()
	}

	override fun setupRecyclerView() {
		recyclerView?.setHasFixedSize(false)
		recyclerView?.addOnScrollListener(CatalogueHitBottom(viewModel))
		super.setupRecyclerView()
	}

	/***/
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		menu.clear()
		inflater.inflate(R.menu.toolbar_library, menu)
		searchView = menu.findItem(R.id.library_search).actionView as SearchView
		searchView?.setOnQueryTextListener(CatalogueSearchQuery(this))
		searchView?.setOnCloseListener {
			isQuery = false
			isInSearch = false
			true
		}
	}


	override fun createDrawer(navigationView: NavigationView, drawerLayout: DrawerLayout) {
		this.navigationView = navigationView
		this.drawerLayout = drawerLayout
		/*
				val builder = SDBuilder(navigationView, drawerLayout, this)
		if (formatter.listings.size > 1) {
			val listingSpinner = builder.spinner(
					"Listing",
					formatter.listings.map { it.name }.toTypedArray(),
					this.selectedListing
			)

			val build = { menu: SDViewBuilder ->
				formatter.listings[this.selectedListing].filters.forEach { it.build(menu) }

			}
			val menu = builder.inner(context!!.getString(R.string.listings), build)

			listingSpinner.onItemSelectedListener =
					object : AdapterView.OnItemSelectedListener {
						override fun onNothingSelected(parent: AdapterView<*>?) {}
						override fun onItemSelected(
								parent: AdapterView<*>?,
								view: View?,
								position: Int,
								id: Long
						) {
							selectedListing = position
							menu.removeAll()
							build(menu)
						}
					}
		}

		builder.inner(context!!.getString(R.string.search_filters)) {
			formatter.searchFilters.forEach { filter -> filter.build(it) }
		}

		navigationView.addView(builder.build())
		*/
	}

	override fun handleConfirm(linearLayout: LinearLayout) {
		//filterValues = formatter.listings[this.selectedListing].filters.values()
		//setLibraryCards(arrayListOf())
		//adapter?.notifyDataSetChanged()
		//viewModel.loadMore()
	}

	override fun updateUI(newList: List<ACatalogNovelUI>) {
		super.updateUI(newList)
		swipeRefreshLayout?.isRefreshing = false
	}

	override fun showError(e: HResult.Error) {
		super.showError(e)
		Log.i(logID(), "Error $e")
	}

	override fun showLoading() {
		super.showLoading()
		if (recyclerArray.isEmpty() && swipeRefreshLayout?.isRefreshing == false)
			swipeRefreshLayout?.isRefreshing = true
		else {
			//TODO show bottom loader
		}
	}

	private fun setupObservers() {
		viewModel.listingItemsLive.observe(this) {
			handleRecyclerUpdate(it)
		}
		viewModel.extensionName.observe(this) {
			when (it) {
				is HResult.Success -> {
					activity?.setActivityTitle(it.data)
					if (recyclerArray.isEmpty())
						viewModel.resetView()
				}
				else -> {
				}
			}
		}
		viewModel.hasSearchLive.observe(this) {
			when (it) {
				is HResult.Success -> {
					searchView?.isEnabled = it.data
				}
			}
		}
		viewModel.filterItemsLive.observe(this){

		}
	}
}
