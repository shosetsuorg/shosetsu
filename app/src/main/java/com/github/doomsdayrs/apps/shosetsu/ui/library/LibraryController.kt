package com.github.doomsdayrs.apps.shosetsu.ui.library

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.controllers.secondDrawer.SecondDrawerController
import com.github.doomsdayrs.apps.shosetsu.backend.services.UpdateService
import com.github.doomsdayrs.apps.shosetsu.ui.library.adapter.LibraryNovelAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.library.listener.LibrarySearchQuery
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationController
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationController.Companion.TARGETS_BUNDLE_KEY
import com.github.doomsdayrs.apps.shosetsu.variables.ext.launchAsync
import com.github.doomsdayrs.apps.shosetsu.variables.ext.runOnMain
import com.github.doomsdayrs.apps.shosetsu.variables.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.variables.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.view.base.RecyclerController
import com.github.doomsdayrs.apps.shosetsu.viewmodel.LibraryViewModel
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ILibraryViewModel
import com.google.android.material.navigation.NavigationView
import java.util.*

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
class LibraryController :
		RecyclerController<LibraryNovelAdapter, Int>(), SecondDrawerController {

	val viewModel: ILibraryViewModel by viewModel()

	val inflater: MenuInflater?
		get() = MenuInflater(applicationContext)

	init {
		setHasOptionsMenu(true)
	}

	override fun onViewCreated(view: View) {
		recyclerView = view.findViewById(R.id.recyclerView)
		Utilities.setActivityTitle(activity, applicationContext!!.getString(R.string.my_library))
		recyclerArray.addAll(viewModel.loadNovelIDs())
		setLibraryCards(recyclerArray)
		subscribe()
	}

	private fun subscribe() {
		viewModel.subscribeObserver(this, Observer { newRecyclerArray ->
			val difCallbacck = LibraryViewModel.LibraryDiffCallBack(
					recyclerArray,
					newRecyclerArray.map { it.id }
			)
			val diffResult = DiffUtil.calculateDiff(difCallbacck)
			recyclerArray.clear()
			recyclerArray.addAll(newRecyclerArray.map { it.id })
			adapter?.let { it -> diffResult.dispatchUpdatesTo(it) }
		})
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		if (viewModel.selectedNovels.size <= 0) {
			Log.d("LibraryFragment", "Creating default menu")
			inflater.inflate(R.menu.toolbar_library, menu)
			val searchView =
					menu.findItem(R.id.library_search).actionView as SearchView?
			searchView?.setOnQueryTextListener(LibrarySearchQuery(this))
			searchView?.setOnCloseListener {
				setLibraryCards(recyclerArray)
				false
			}
		} else {
			Log.d("LibraryFragment", "Creating selected menu")
			inflater.inflate(R.menu.toolbar_library_selected, menu)
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.updater_now -> {
				UpdateService.init(applicationContext!!, recyclerArray)
				return true
			}
			R.id.chapter_select_all -> {
				launchAsync { viewModel.selectAll() }
				return true
			}
			R.id.chapter_deselect_all -> {
				launchAsync {
					viewModel.deselectAll()
					runOnMain {
						if (inflater != null) activity?.invalidateOptionsMenu()
					}
				}
				return true
			}
			R.id.remove_from_library -> {
				launchAsync {
					viewModel.removeAllFromLibrary(recyclerView!!)
				}
				return true
			}
			R.id.source_migrate -> {
				router.pushController(MigrationController(bundleOf(
						TARGETS_BUNDLE_KEY to viewModel.selectedNovels.toIntArray()
				)).withFadeTransaction())
				return true
			}
		}
		return false
	}


	/**
	 * Sets the cards to display
	 */
	fun setLibraryCards(novelCards: ArrayList<Int>) {
		recyclerView?.setHasFixedSize(false)
		if (Settings.novelCardType == 0) {
			adapter = LibraryNovelAdapter(
					novelCards,
					this,
					R.layout.recycler_novel_card
			)
			recyclerView?.layoutManager = GridLayoutManager(
					applicationContext,
					Utilities.calculateColumnCount(applicationContext!!, 200f),
					RecyclerView.VERTICAL,
					false
			)
		} else {
			adapter = LibraryNovelAdapter(
					novelCards,
					this,
					R.layout.recycler_novel_card_compressed
			)
			recyclerView?.layoutManager = LinearLayoutManager(
					applicationContext,
					LinearLayoutManager.VERTICAL,
					false
			)
		}
	}

	override fun createDrawer(navigationView: NavigationView, drawerLayout: DrawerLayout) {
		// TODO
	}

	override fun handleConfirm(linearLayout: LinearLayout) {
		// TODO
	}
}