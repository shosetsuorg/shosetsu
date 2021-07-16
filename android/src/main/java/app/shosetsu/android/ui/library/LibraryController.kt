package app.shosetsu.android.ui.library

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.content.res.Resources
import android.view.*
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.consts.BundleKeys
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.library.listener.LibrarySearchQuery
import app.shosetsu.android.ui.migration.MigrationController
import app.shosetsu.android.ui.novel.NovelController
import app.shosetsu.android.view.controller.*
import app.shosetsu.android.view.controller.base.ExtendedFABController
import app.shosetsu.android.view.controller.base.syncFABWithRecyclerView
import app.shosetsu.android.view.uimodels.model.library.ABookmarkedNovelUI
import app.shosetsu.android.viewmodel.abstracted.ALibraryViewModel
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.enums.NovelCardType
import app.shosetsu.common.enums.NovelCardType.*
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.select.selectExtension
import kotlinx.coroutines.delay

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
	: FastAdapterRefreshableRecyclerController<ABookmarkedNovelUI>(), ExtendedFABController {

	private var fab: ExtendedFloatingActionButton? = null

	override val viewTitleRes: Int = R.string.my_library

	/***/
	val viewModel: ALibraryViewModel by viewModel()

	init {
		setHasOptionsMenu(true)
	}

	private val NovelCardType.manager: LinearLayoutManager
		get() = when (this) {
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
		(viewModel.novelCardTypeLiveData.value
			?: NovelCardType.valueOf(SettingKey.SelectedNovelCardType.default)).manager

	override fun setupRecyclerView() {
		recyclerView.setHasFixedSize(false)
		fab?.let {
			syncFABWithRecyclerView(recyclerView, it)
		}
		super.setupRecyclerView()
		startObservation()
	}

	@SuppressLint("StringFormatInvalid")
	override fun FastAdapter<ABookmarkedNovelUI>.setupFastAdapter() {
		selectExtension {
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
		setOnPreClickListener FastAdapterClick@{ _, _, item, position ->
			// Handles one click select when in selection mode
			selectExtension {
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

		hookClickEvent<ABookmarkedNovelUI, ABookmarkedNovelUI.ViewHolder>(bind = { it.chip }) { v, p, a, item ->
			try {
				makeSnackBar(
					resources!!.getQuantityString(
						R.plurals.toast_unread_count,
						item.unread,
						item.unread
					)
				)?.show()
			} catch (e: Resources.NotFoundException) {
				makeSnackBar(
					resources!!.getString(
						R.string.chapters_unread_label,
						item.unread
					)
				)?.show()
			}
		}
		setOnClickListener { _, _, item, _ ->
			router.shosetsuPush(
				NovelController(
					bundleOf(BundleKeys.BUNDLE_NOVEL_ID to item.id)
				)
			)
			true
		}
	}

	private fun startObservation() {
		viewModel.liveData.observeRecyclerUpdates()
		viewModel.novelCardTypeLiveData.observe(this) {
			updateLayoutManager(it.manager)
		}
	}

	private val selectedNovels: List<ABookmarkedNovelUI>
		get() = fastAdapter.getSelectExtension().selectedItems.toList()


	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		if (fastAdapter.getSelectExtension().selectedItems.isEmpty()) {
			inflater.inflate(R.menu.toolbar_library, menu)
		} else {
			inflater.inflate(R.menu.toolbar_library_selected, menu)
		}
	}

	private var searchView: SearchView? = null

	override fun onPrepareOptionsMenu(menu: Menu) {
		logI("Preparing options menu")
		searchView = (menu.findItem(R.id.library_search)?.actionView as? SearchView)
		searchView?.apply {
			setOnQueryTextListener(LibrarySearchQuery(this@LibraryController))
		}
		configureViewTypeMenu(menu)
	}

	private fun configureViewTypeMenu(menu: Menu, isRetry: Boolean = false) {
		logI("Syncing menu")
		when (viewModel.novelCardTypeLiveData.value) {
			NORMAL -> {
				menu.findItem(R.id.view_type_normal)?.isChecked = true
			}
			COMPRESSED -> {
				menu.findItem(R.id.view_type_comp)?.isChecked = true
			}
			COZY -> logE("Not cozy card implemented")
			null -> {
				if (isRetry) {
					logE("No value still found for novelCardType, aborting")
					return
				}

				logE("No value found for novelCardType, retrying in a 100 ms")
				launchIO {
					delay(100)
					launchUI {
						configureViewTypeMenu(menu, true)
					}
				}
			}
		}
	}

	override fun handleBack(): Boolean =
		if (searchView != null && searchView!!.isIconified) {
			searchView!!.onActionViewCollapsed()
			true
		} else super.handleBack()

	/***/
	override fun onOptionsItemSelected(item: MenuItem): Boolean =
		when (item.itemId) {
			R.id.updater_now -> {
				if (viewModel.isOnline())
					viewModel.startUpdateManager()
				else displayOfflineSnackBar()
				true
			}
			R.id.library_select_all -> {
				selectAll()
				true
			}
			R.id.library_deselect_all -> {
				deselectAll()
				true
			}
			R.id.library_inverse_selection -> {
				invertSelection()
				true
			}
			R.id.library_select_between -> {
				selectBetween()
				true
			}
			R.id.remove_from_library -> {
				launchIO {
					viewModel.removeFromLibrary(
						fastAdapter.getSelectExtension().selectedItems.toList()
					)
				}
				true
			}
			R.id.source_migrate -> {
				router.pushController(
					MigrationController(
						bundleOf(
							MigrationController.TARGETS_BUNDLE_KEY to arrayListOf<Int>()
						)
					).withFadeTransaction()
				)
				true
			}
			R.id.view_type_normal -> {
				item.isChecked = !item.isChecked
				viewModel.setViewType(NORMAL)
				true
			}
			R.id.view_type_comp -> {
				item.isChecked = !item.isChecked
				viewModel.setViewType(COMPRESSED)
				true
			}
			else -> false
		}


	private fun deselectAll() {
		fastAdapter.getSelectExtension().deselect()
	}

	private fun selectAll() {
		fastAdapter.getSelectExtension().select(true)
	}

	private fun invertSelection() {
		fastAdapter.invertSelection()
	}

	private fun selectBetween() {
		fastAdapter.selectBetween(itemAdapter, selectedNovels)
	}

	override fun showEmpty() {
		super.showEmpty()
		binding.emptyDataView.show(R.string.empty_library_message)
	}

	override fun manipulateFAB(fab: ExtendedFloatingActionButton) {
		this.fab = fab
		fab.setOnClickListener {
			//bottomMenuRetriever.invoke()?.show()
			BottomSheetDialog(binding.root.context).apply {
				setContentView(getBottomMenuView())
			}.show()
		}
		fab.setText(R.string.filter)
		fab.setIconResource(R.drawable.filter)
	}

	private fun getBottomMenuView(): View =
		LibraryFilterMenuBuilder(this, viewModel).build()

	override fun onRefresh() {
		if (viewModel.isOnline())
			viewModel.startUpdateManager()
		else displayOfflineSnackBar(R.string.generic_error_cannot_update_library_offline)
	}

	override fun handleErrorResult(e: HResult.Error) {
		TODO("Not yet implemented")
	}

}