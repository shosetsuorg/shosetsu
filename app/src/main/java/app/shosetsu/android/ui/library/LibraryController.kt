package app.shosetsu.android.ui.library

import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.ShosetsuSettings
import app.shosetsu.android.common.consts.BundleKeys
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.library.listener.LibrarySearchQuery
import app.shosetsu.android.ui.migration.MigrationController
import app.shosetsu.android.ui.novel.NovelController
import app.shosetsu.android.view.base.FastAdapterRecyclerController
import app.shosetsu.android.view.base.SecondDrawerController
import app.shosetsu.android.view.uimodels.model.library.ABookmarkedNovelUI
import app.shosetsu.android.viewmodel.abstracted.ILibraryViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerLibraryBinding
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerRecyclerBinding
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
	: FastAdapterRecyclerController<ControllerLibraryBinding, ABookmarkedNovelUI>(), SecondDrawerController {
	override val viewTitleRes: Int = R.string.my_library

	/***/
	val viewModel: ILibraryViewModel by viewModel()
	private val settings by instance<ShosetsuSettings>()

	/** Inflater */
	val inflater: MenuInflater = MenuInflater(applicationContext)

	init {
		setHasOptionsMenu(true)
	}

	override fun bindView(inflater: LayoutInflater): ControllerLibraryBinding =
			ControllerLibraryBinding.inflate(inflater).also { recyclerView = it.recyclerView }

	override fun createLayoutManager(): RecyclerView.LayoutManager {
		return if (settings.novelCardType == 0)
			GridLayoutManager(
					applicationContext,
					settings.calculateColumnCount(applicationContext!!, 200f),
					RecyclerView.VERTICAL,
					false
			) else
			LinearLayoutManager(
					applicationContext,
					LinearLayoutManager.VERTICAL,
					false
			)
	}

	override fun onViewCreated(view: View) {
		ControllerRecyclerBinding.bind(view)
	}

	override fun setupRecyclerView() {
		recyclerView.setHasFixedSize(false)
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

	override fun updateUI(newList: List<ABookmarkedNovelUI>) {
		Log.d(logID(), "Received ${newList.size} bookmarked novels")
		super.updateUI(newList)
	}

	override fun difAreItemsTheSame(
			oldItem: ABookmarkedNovelUI,
			newItem: ABookmarkedNovelUI,
	): Boolean = oldItem.id == newItem.id

	override fun handleConfirm(linearLayout: LinearLayout) {
		// TODO
	}

	override fun createDrawer(navigationView: NavigationView, drawerLayout: DrawerLayout) {
		// TODO
	}

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
				if (viewModel.isOnline())
					viewModel.startUpdateManager()
				else toast(R.string.you_not_online)
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
				app.shosetsu.android.common.ext.launchAsync {
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