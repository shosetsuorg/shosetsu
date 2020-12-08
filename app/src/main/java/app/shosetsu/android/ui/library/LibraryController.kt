package app.shosetsu.android.ui.library

import android.content.res.Configuration
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.consts.BundleKeys
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.library.listener.LibrarySearchQuery
import app.shosetsu.android.ui.migration.MigrationController
import app.shosetsu.android.ui.novel.NovelController
import app.shosetsu.android.view.base.FastAdapterRecyclerController
import app.shosetsu.android.view.base.PushCapableController
import app.shosetsu.android.view.uimodels.model.library.ABookmarkedNovelUI
import app.shosetsu.android.viewmodel.abstracted.ILibraryViewModel
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.com.enums.NovelUIType.COMPRESSED
import com.bluelinelabs.conductor.Controller
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerLibraryBinding
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.select.selectExtension

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
	: FastAdapterRecyclerController<ControllerLibraryBinding, ABookmarkedNovelUI>(),
		PushCapableController {
	lateinit var pushController: (Controller) -> Unit

	override val viewTitleRes: Int = R.string.my_library

	/***/
	val viewModel: ILibraryViewModel by viewModel()

	/** Inflater */
	val inflater: MenuInflater = MenuInflater(applicationContext)

	init {
		setHasOptionsMenu(true)
	}

	override fun bindView(inflater: LayoutInflater): ControllerLibraryBinding =
			ControllerLibraryBinding.inflate(inflater).also { recyclerView = it.recyclerView }

	override fun createLayoutManager(): RecyclerView.LayoutManager {
		return when (viewModel.getNovelUIType()) {
			COMPRESSED -> LinearLayoutManager(
					applicationContext,
					LinearLayoutManager.VERTICAL,
					false
			)
			else -> GridLayoutManager(
					applicationContext,
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
					RecyclerView.VERTICAL,
					false
			)
		}
	}

	override fun onViewCreated(view: View) {
		showEmpty()
		binding.swipeRefreshLayout.setOnRefreshListener {
			if (viewModel.isOnline())
				viewModel.startUpdateManager()
			else toast(R.string.you_not_online)

			binding.swipeRefreshLayout.isRefreshing = false
		}
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
			pushController(NovelController(
					bundleOf(BundleKeys.BUNDLE_NOVEL_ID to item.id)
			))
			true
		}
	}

	private fun setObservers() {
		viewModel.liveData.observe(this) { handleRecyclerUpdate(it) }
	}

	override fun updateUI(newList: List<ABookmarkedNovelUI>) {
		// sorts in IO, updates on UI
		launchIO { newList.sortedBy { it.title }.let { launchUI { super.updateUI(it) } } }
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
				launchIO {
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

	override fun hideEmpty() {
		if (!binding.recyclerView.isVisible) binding.recyclerView.isVisible = true
		binding.emptyDataView.hide()
	}

	override fun showEmpty() {
		binding.recyclerView.isVisible = false
		binding.emptyDataView.show("You don't have any novels, Go to \"Browse\" and add some!")
	}

	override fun acceptPushing(pushController: (Controller) -> Unit) {
		this.pushController = pushController
	}
}