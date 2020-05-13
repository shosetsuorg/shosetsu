package com.github.doomsdayrs.apps.shosetsu.ui.catalogue

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.Settings
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_FORMATTER
import com.github.doomsdayrs.apps.shosetsu.common.ext.calculateColumnCount
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.common.ext.setActivityTitle
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters.CatalogueAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueHitBottom
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueSearchQuery
import com.github.doomsdayrs.apps.shosetsu.view.base.RecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.base.SecondDrawerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.IDTitleImageUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ICatalogViewModel
import com.google.android.material.navigation.NavigationView

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
class CatalogController(bundle: Bundle)
	: RecyclerController<CatalogueAdapter, IDTitleImageUI>(bundle), SecondDrawerController {

	override val layoutRes: Int = R.layout.catalogue

	/***/
	@Attach(R.id.swipeRefreshLayout)
	var swipeRefreshLayout: SwipeRefreshLayout? = null


	/***/
	val viewModel: ICatalogViewModel by viewModel()

	/** If the user is currently searching something up*/
	var isInSearch = false

	/** If the user is currently viewing query data*/
	var isQuery = false

	init {
		setHasOptionsMenu(true)
		viewModel.setFormatterID(bundle.getInt(BUNDLE_FORMATTER))
	}

	override fun onViewCreated(view: View) {
		activity?.setActivityTitle(viewModel.formatter.value?.name)
		swipeRefreshLayout?.setOnRefreshListener {
			viewModel.clearAndLoad()
		}
		setLibraryCards(recyclerArray)
		if (recyclerArray.isEmpty()) viewModel.loadMore()

		viewModel.liveData.observe(this, Observer {
			handleRecyclerUpdate(it)
		})

		recyclerView?.setHasFixedSize(false)
		recyclerView?.layoutManager =
				if (Settings.novelCardType == 0)
					GridLayoutManager(
							context,
							context!!.calculateColumnCount(200f),
							RecyclerView.VERTICAL,
							false
					)
				else
					LinearLayoutManager(
							context,
							LinearLayoutManager.VERTICAL,
							false
					)
		recyclerView?.addOnScrollListener(CatalogueHitBottom(this))
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		menu.clear()
		inflater.inflate(R.menu.toolbar_library, menu)
		val searchView = menu.findItem(R.id.library_search).actionView as SearchView
		searchView.setOnQueryTextListener(CatalogueSearchQuery(this))
		searchView.setOnCloseListener {
			isQuery = false
			isInSearch = false
			setLibraryCards(recyclerArray)
			true
		}
	}

	fun setLibraryCards(recycleListingCards: ArrayList<IDTitleImageUI>) {
		recyclerView?.adapter = CatalogueAdapter(
				recycleListingCards,
				this,
				viewModel.getFormatterID(),
				if (Settings.novelCardType == 0)
					R.layout.recycler_novel_card
				else R.layout.recycler_novel_card_compressed
		)
	}

	override fun createDrawer(navigationView: NavigationView, drawerLayout: DrawerLayout) {
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

	override fun difAreItemsTheSame(oldItem: IDTitleImageUI, newItem: IDTitleImageUI): Boolean =
			oldItem.id == newItem.id
}
