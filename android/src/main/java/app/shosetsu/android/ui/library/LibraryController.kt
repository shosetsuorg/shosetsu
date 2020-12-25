package app.shosetsu.android.ui.library

import android.content.res.Configuration
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
import app.shosetsu.android.view.controller.*
import app.shosetsu.android.view.controller.base.BottomMenuController
import app.shosetsu.android.view.controller.base.ExtendedFABController
import app.shosetsu.android.view.controller.base.PushCapableController
import app.shosetsu.android.view.controller.base.syncFABWithRecyclerView
import app.shosetsu.android.view.uimodels.model.library.ABookmarkedNovelUI
import app.shosetsu.android.view.widget.SlidingUpBottomMenu
import app.shosetsu.android.viewmodel.abstracted.ILibraryViewModel
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.enums.NovelUIType
import app.shosetsu.common.enums.NovelUIType.*
import com.bluelinelabs.conductor.Controller
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerLibraryBinding
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
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
	PushCapableController, ExtendedFABController, BottomMenuController {
	override var pushController: (Controller) -> Unit = {}

	override var bottomMenuRetriever: (() -> SlidingUpBottomMenu?) = { null }

	private var fab: ExtendedFloatingActionButton? = null

	override val viewTitleRes: Int = R.string.my_library

	/***/
	val viewModel: ILibraryViewModel by viewModel()

	init {
		setHasOptionsMenu(true)
	}

	override fun bindView(inflater: LayoutInflater): ControllerLibraryBinding =
		ControllerLibraryBinding.inflate(inflater).also { recyclerView = it.recyclerView }

	private fun NovelUIType.manager() = when (this) {
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

	override fun createLayoutManager(): RecyclerView.LayoutManager =
		viewModel.getNovelUIType().manager()

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
		fab?.let {
			syncFABWithRecyclerView(recyclerView, it)
		}
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
			pushController(
				NovelController(
					bundleOf(BundleKeys.BUNDLE_NOVEL_ID to item.id)
				)
			)
			true
		}
	}

	private fun setObservers() {
		viewModel.liveData.observe(this) { handleRecyclerUpdate(it) }
		viewModel.novelUITypeLiveData.observe(this) {
			updateLayoutManager(it.manager())
		}
	}

	/***/
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		if (fastAdapter.getSelectExtension().selectedItems.isEmpty()) {
			inflater.inflate(R.menu.toolbar_library, menu)
			val searchView =
				menu.findItem(R.id.library_search).actionView as SearchView?
			searchView?.setOnQueryTextListener(LibrarySearchQuery(this))
			searchView?.setOnCloseListener {
				val v = viewModel.liveData.value
				return@setOnCloseListener v is HResult.Success
			}
		} else {
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
				router.pushController(
					MigrationController(
						bundleOf(
							MigrationController.TARGETS_BUNDLE_KEY to arrayListOf<Int>()
						)
					).withFadeTransaction()
				)
				return true
			}
			R.id.view_type_normal -> {
				viewModel.setViewType(NORMAL)
				return true
			}
			R.id.view_type_comp -> {
				viewModel.setViewType(COMPRESSED)
				return true
			}
			else -> return false
		}
	}

	override fun hideEmpty() {
		if (!binding.recyclerView.isVisible) binding.recyclerView.isVisible = true
		binding.emptyDataView.hide()
	}

	override fun showEmpty() {
		if (itemAdapter.adapterItemCount > 0) return
		binding.recyclerView.isVisible = false
		binding.emptyDataView.show("You don't have any novels, Go to \"Browse\" and add some!")
	}

	override fun manipulateFAB(fab: ExtendedFloatingActionButton) {
		this.fab = fab
		fab.setOnClickListener { bottomMenuRetriever.invoke()?.show() }
		fab.setText(R.string.filter)
		fab.setIconResource(R.drawable.filter)
	}

	override fun getBottomMenuView(): View =
		LibraryFilterMenuBuilder(this, viewModel).build()

}