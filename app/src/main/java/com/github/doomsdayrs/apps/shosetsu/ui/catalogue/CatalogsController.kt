package com.github.doomsdayrs.apps.shosetsu.ui.catalogue

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.isOnline
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys
import com.github.doomsdayrs.apps.shosetsu.common.ext.*
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CataloguesSearchQuery
import com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.ConfigureExtensions
import com.github.doomsdayrs.apps.shosetsu.view.base.FastAdapterRecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.CatalogUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ICatalogsViewModel

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
//TODO Searching mechanics here
class CatalogsController : FastAdapterRecyclerController<CatalogUI>() {
	private val viewModel: ICatalogsViewModel by viewModel()

	init {
		setHasOptionsMenu(true)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_catalogues, menu)
		(menu.findItem(R.id.catalogues_search).actionView as SearchView)
				.setOnQueryTextListener(CataloguesSearchQuery(activity))
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.catalogues_search -> true
			R.id.configure_parsers -> {
				router.pushController(ConfigureExtensions().withFadeTransaction())
				true
			}
			else -> false
		}
	}

	override fun onViewCreated(view: View) {
		activity?.setActivityTitle(R.string.catalogues)
		viewModel.liveData.observe(this, Observer(::handleRecyclerUpdate))
	}

	override fun setupFastAdapter() {
		fastAdapter.setOnClickListener { v, a, i, p ->
			Log.d("FormatterSelection", i.title)
			if (isOnline) {
				val catalogueFragment = CatalogController(bundleOf(
						BundleKeys.BUNDLE_FORMATTER to i.identifier.toInt()
				))
				router.pushController(catalogueFragment.withFadeTransaction())
			} else context?.toast(R.string.you_not_online)
			true
		}
	}

	override fun setupRecyclerView() {
		recyclerView?.setHasFixedSize(true)
		super.setupRecyclerView()
	}
}