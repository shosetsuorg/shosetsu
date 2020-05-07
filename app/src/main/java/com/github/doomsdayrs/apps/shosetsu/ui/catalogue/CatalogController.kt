package com.github.doomsdayrs.apps.shosetsu.ui.catalogue

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.values
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.secondDrawer.SDBuilder
import com.github.doomsdayrs.apps.shosetsu.ui.secondDrawer.SDViewBuilder
import com.github.doomsdayrs.apps.shosetsu.ui.secondDrawer.SecondDrawerController
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_FORMATTER
import com.github.doomsdayrs.apps.shosetsu.common.ext.build
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.common.ext.defaultListing
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.common.utils.FormatterUtils
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters.CatalogueAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async.CataloguePageLoader
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueHitBottom
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueRefresh
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueSearchQuery
import com.github.doomsdayrs.apps.shosetsu.ui.webView.WebViewApp
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelListingCard
import com.github.doomsdayrs.apps.shosetsu.view.base.RecyclerController
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
//TODO fix issue with not loading
class CatalogController(bundle: Bundle)
	: RecyclerController<CatalogueAdapter, NovelListingCard>(bundle), SecondDrawerController {

	override val layoutRes: Int = R.layout.catalogue

	@Attach(R.id.swipeRefreshLayout)
	var swipeRefreshLayout: SwipeRefreshLayout? = null

	private var cataloguePageLoader: CataloguePageLoader? = null

	val viewModel: ICatalogViewModel by viewModel()

	var formatter: Formatter = FormatterUtils.getByID(bundle.getInt(BUNDLE_FORMATTER))
	var selectedListing: Int = formatter.defaultListing
	var filterValues: Array<*> = formatter.listings[this.selectedListing].filters.values()

	var currentMaxPage = 1
	var isInSearch = false
	private var dontRefresh = false
	var isQuery = false

	init {
		setHasOptionsMenu(true)
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putInt(BUNDLE_FORMATTER, formatter.formatterID)
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle) {
		super.onRestoreInstanceState(savedInstanceState)
		formatter = FormatterUtils.getByID(savedInstanceState.getInt(BUNDLE_FORMATTER))
	}

	override fun onViewCreated(view: View) {
		Utilities.setActivityTitle(activity, formatter.name)
		swipeRefreshLayout?.setOnRefreshListener(CatalogueRefresh(this))
		if (!dontRefresh) {
			Log.d("Process", "Loading up latest")
			setLibraryCards(recyclerArray)
			if (recyclerArray.size > 0) {
				recyclerArray = ArrayList()
				adapter?.notifyDataSetChanged()
			}
			if (!formatter.hasCloudFlare) {
				viewModel.loadMore()
			} else {
				val intent = Intent(activity, WebViewApp::class.java)
				// TODO Formatter require of base URL
				intent.putExtra("url", formatter.imageURL)
				intent.putExtra("action", 1)
				startActivityForResult(intent, 42)
			}
		} else setLibraryCards(recyclerArray)
	}

	override fun onActivityPaused(activity: Activity) {
		super.onActivityPaused(activity)
		Log.d("Pause", "HERE")
		dontRefresh = true
		cataloguePageLoader?.cancel(true)
	}

	override fun onDestroy() {
		super.onDestroy()
		dontRefresh = false
		cataloguePageLoader?.cancel(true)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == 42) {
			viewModel.loadMore()
		}
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

	fun setLibraryCards(recycleListingCards: ArrayList<NovelListingCard>) {
		recyclerView?.setHasFixedSize(false)
		recyclerView?.adapter = CatalogueAdapter(
				recycleListingCards,
				this,
				formatter,
				if (Settings.novelCardType == 0)
					R.layout.recycler_novel_card
				else R.layout.recycler_novel_card_compressed
		)
		recyclerView?.layoutManager =
				if (Settings.novelCardType == 0)
					GridLayoutManager(
							context,
							Utilities.calculateColumnCount(context!!, 200f),
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

	override fun createDrawer(navigationView: NavigationView, drawerLayout: DrawerLayout) {
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
	}

	override fun handleConfirm(linearLayout: LinearLayout) {
		filterValues = formatter.listings[this.selectedListing].filters.values()
		setLibraryCards(arrayListOf())
		adapter?.notifyDataSetChanged()
		viewModel.loadMore()
	}

	override fun difAreItemsTheSame(oldItem: NovelListingCard, newItem: NovelListingCard): Boolean =
			oldItem.novelID == newItem.novelID
}
