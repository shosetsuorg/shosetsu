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
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.services.UpdateWorker
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.*
import com.github.doomsdayrs.apps.shosetsu.ui.library.listener.LibrarySearchQuery
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.view.base.FastAdapterRecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.base.SecondDrawerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.library.ABookmarkedNovelUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ILibraryViewModel
import com.google.android.material.navigation.NavigationView
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.select.selectExtension
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
 */


/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class LibraryController
	: FastAdapterRecyclerController<ABookmarkedNovelUI>(), SecondDrawerController {
	/***/
	val viewModel: ILibraryViewModel by viewModel()
	private val settings by instance<ShosetsuSettings>()
	private val manager by instance<UpdateWorker.UpdateWorkerManager>()


	/** Inflater */
	val inflater: MenuInflater = MenuInflater(applicationContext)

	override fun createLayoutManager(): RecyclerView.LayoutManager {
		return if (settings.novelCardType == 0)
			GridLayoutManager(
					applicationContext,
					applicationContext!!.calculateColumnCount(200f, settings),
					RecyclerView.VERTICAL,
					false
			) else
			LinearLayoutManager(
					applicationContext,
					LinearLayoutManager.VERTICAL,
					false
			)
	}

	init {
		setHasOptionsMenu(true)
	}

	override fun onViewCreated(view: View) {
		activity?.setActivityTitle(R.string.my_library)
	}

	override fun setupRecyclerView() {
		recyclerView?.setHasFixedSize(false)
		super.setupRecyclerView()
		setObservers()
	}

	override fun setupFastAdapter() {
		fastAdapter.selectExtension {
			isSelectable = true
			multiSelect = true
			selectOnLongClick = true
			setSelectionListener { item, _ ->
				// Recreates the item view
				fastAdapter.notifyItemChanged(fastAdapter.getPosition(item))

				// Swaps the options menu on top
				val size = selectedItems.size
				if (size == 0 || size == 1) activity?.invalidateOptionsMenu()
			}
		}
		itemAdapter.itemFilter.filterPredicate = { item, constraint ->
			item.title.contains(constraint.toString(), ignoreCase = true)
		}
		fastAdapter.setOnPreClickListener FastAdapterClick@{ _, _, item, position ->
			// Handles one click select when in selection mode
			fastAdapter.selectExtension {
				if (selectedItems.isNotEmpty()) {
					if (!item.isSelected)
						select(
								item = item,
								considerSelectableFlag = true
						)
					else
						deselect(position)
					return@FastAdapterClick true
				}
			}
			false
		}

		fastAdapter.setOnClickListener { _, _, item, _ ->
			router.pushController(NovelController(
					bundleOf(BundleKeys.BUNDLE_NOVEL_ID to item.id)
			).withFadeTransaction())
			true
		}
	}

	private fun setObservers() {
		viewModel.liveData.observe(this) { handleRecyclerUpdate(it) }
	}

	override fun updateUI(list: List<ABookmarkedNovelUI>) {
		Log.d(logID(), "Received ${list.size} bookmarked novels")
		super.updateUI(list)
	}

	override fun difAreItemsTheSame(
			oldItem: ABookmarkedNovelUI,
			newItem: ABookmarkedNovelUI
	): Boolean = oldItem.id == newItem.id

	override fun handleConfirm(linearLayout: LinearLayout) {
		// TODO
	}

	override fun createDrawer(navigationView: NavigationView, drawerLayout: DrawerLayout) {
		// TODO
	}


	// - Option Menu

	/***/
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		if (fastAdapter.getSelectExtension().selectedItems.isEmpty()) {
			Log.d(logID(), "Creating default menu")
			inflater.inflate(R.menu.toolbar_library, menu)
			val searchView =
					menu.findItem(R.id.library_search).actionView as SearchView?
			searchView?.setOnQueryTextListener(LibrarySearchQuery(this))
			searchView?.setOnCloseListener {
				val v = viewModel.liveData.value
				return@setOnCloseListener v is HResult.Success
			}
		} else {
			Log.d(logID(), "Creating selected menu")
			inflater.inflate(R.menu.toolbar_library_selected, menu)
		}
	}

	/***/
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.updater_now -> {
				manager.start(
						applicationContext!!
				)
				return true
			}
			R.id.chapter_select_all -> {
				fastAdapter.getSelectExtension().select()
				return true
			}
			R.id.chapter_deselect_all -> {
				fastAdapter.getSelectExtension().deselect()
				return true
			}
			R.id.remove_from_library -> {
				launchAsync {
					viewModel.removeFromLibrary(
							fastAdapter.getSelectExtension().selectedItems.toList()
					)
				}
				return true
			}
			R.id.source_migrate -> {
				router.pushController(MigrationController(bundleOf(
						MigrationController.TARGETS_BUNDLE_KEY to arrayListOf<Int>()
				)).withFadeTransaction())
				return true
			}
		}
		return false
	}
}