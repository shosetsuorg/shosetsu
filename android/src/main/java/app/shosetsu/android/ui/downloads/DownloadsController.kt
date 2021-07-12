package app.shosetsu.android.ui.downloads

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

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.view.controller.BottomMenuBasicFastAdapterRecyclerController
import app.shosetsu.android.view.controller.base.ExtendedFABController
import app.shosetsu.android.view.uimodels.model.DownloadUI
import app.shosetsu.android.viewmodel.abstracted.ADownloadsViewModel
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.enums.DownloadStatus.*
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.select.selectExtension

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class DownloadsController : BottomMenuBasicFastAdapterRecyclerController<DownloadUI>(), ExtendedFABController {

	override val viewTitleRes: Int = R.string.downloads
	private val viewModel: ADownloadsViewModel by viewModel()
	private var fab: ExtendedFloatingActionButton? = null
	private var actionMode: ActionMode? = null

	init {
		setHasOptionsMenu(true)
	}

	override fun showEmpty() {
		super.showEmpty()
		binding.emptyDataView.show(R.string.empty_downloads_message)
	}

	private fun startSelectionAction(): Boolean {
		if (actionMode != null) return false
		hideFAB(fab!!)
		actionMode = activity?.startActionMode(SelectionActionMode())
		return true
	}

	private fun calculateBottomSelectionMenuChanges() {
		val selectedDownloads =
			fastAdapter.getSelectExtension().selectedItems.toList()

		binding.bottomMenu.findItem(R.id.pause)?.isVisible = selectedDownloads.any {
			it.status == PENDING
		}

		binding.bottomMenu.findItem(R.id.restart)?.isVisible = selectedDownloads.any {
			it.status == ERROR
		}

		binding.bottomMenu.findItem(R.id.start)?.isVisible = selectedDownloads.any {
			it.status == PAUSED
		}

		binding.bottomMenu.findItem(R.id.delete)?.isVisible = selectedDownloads.any {
			it.status == PAUSED || it.status == PENDING || it.status == ERROR
		}
	}

	override fun FastAdapter<DownloadUI>.setupFastAdapter() {
		selectExtension {
			isSelectable = true
			multiSelect = true
			selectOnLongClick = true
			setSelectionListener { item, _ ->
				// Recreates the item view
				fastAdapter.notifyItemChanged(fastAdapter.getPosition(item))

				// Updates action mode
				calculateBottomSelectionMenuChanges()

				// Swaps the options menu on top
				val size = selectedItems.size
				if (size == 1) startSelectionAction() else if (size == 0) actionMode?.finish()
			}
		}
		setOnPreClickListener FastAdapterClick@{ _, _, item, position ->
			// Handles one click select when in selection mode
			selectExtension {
				if (selectedItems.isNotEmpty()) {
					if (!item.isSelected) {
						select(
							item = item,
							considerSelectableFlag = true
						)
					} else {
						deselect(position)
					}
					return@FastAdapterClick true
				}
			}
			false
		}
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_downloads, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.set_all_pending -> {
				itemAdapter.itemList.items.forEach {
					viewModel.start(it)
				}
				return true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun togglePause() {
		if (viewModel.isOnline()) viewModel.togglePause() else displayOfflineSnackBar(R.string.controller_downloads_snackbar_offline_no_download)
	}

	override fun onViewCreated(view: View) {
		viewModel.liveData.observe(this) { handleRecyclerUpdate(it) }
		viewModel.isDownloadPaused.observe(this) {
			fab?.setText(
				if (it)
					R.string.resume
				else R.string.pause
			)
			fab?.setIconResource(
				if (it)
					R.drawable.play_arrow
				else R.drawable.ic_pause_circle_outline_24dp
			)
		}
	}

	override fun handleErrorResult(e: HResult.Error) {
		viewModel.reportError(e)
	}

	override fun setupRecyclerView() {
		recyclerView.setHasFixedSize(false)
		recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
				when (newState) {
					RecyclerView.SCROLL_STATE_DRAGGING -> {
						fab?.let { hideFAB(it) }
					}
					RecyclerView.SCROLL_STATE_SETTLING -> {
					}
					RecyclerView.SCROLL_STATE_IDLE -> {
						fab?.let { showFAB(it) }
					}
				}
			}
		})
		super.setupRecyclerView()
	}

	override fun onDestroy() {
		actionMode?.finish()
		super.onDestroy()
	}

	override fun manipulateFAB(fab: ExtendedFloatingActionButton) {
		this.fab = fab
		fab.setOnClickListener { togglePause() }
		fab.setText(R.string.paused)
		fab.setIconResource(R.drawable.ic_pause_circle_outline_24dp)
	}

	override fun showFAB(fab: ExtendedFloatingActionButton) {
		if (actionMode == null) super.showFAB(fab)
	}

	private fun startSelection() {
		fastAdapter.getSelectExtension().selectedItems.filter {
			it.status == PAUSED || it.status == ERROR
		}.let {
			viewModel.startAll(it)
		}
	}

	private fun startFailedSelection() {
		fastAdapter.getSelectExtension().selectedItems.filter {
			it.status == ERROR
		}.let {
			viewModel.startAll(it)
		}
	}

	private fun pauseSelection() {
		fastAdapter.getSelectExtension().selectedItems.filter {
			it.status == PENDING || it.status == ERROR
		}.let {
			viewModel.pauseAll(it)
		}
	}

	private fun deleteNotDownloadingSelection() {
		fastAdapter.getSelectExtension().selectedItems.filter {
			it.status == PENDING || it.status == ERROR || it.status == PAUSED
		}.let {
			viewModel.deleteAll(it)
		}
	}

	private fun selectAll() {
		fastAdapter.getSelectExtension().select(true)
	}

	private fun invertSelection() {
		fastAdapter.invertSelection()
	}


	private fun selectBetween() {
		fastAdapter.selectBetween(itemAdapter)
	}

	private inner class SelectionActionMode : ActionMode.Callback {
		override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
			// Hides the original action bar
			// (activity as MainActivity?)?.supportActionBar?.hide()

			mode.menuInflater.inflate(R.menu.toolbar_downloads_selected, menu)
			mode.setTitle(R.string.selection)
			binding.bottomMenu.show(mode, R.menu.toolbar_downloads_selected_bottom) {
				when (it.itemId) {
					R.id.pause -> {
						pauseSelection()
						actionMode?.finish()
						true
					}
					R.id.start -> {
						startSelection()
						actionMode?.finish()
						true
					}
					R.id.restart -> {
						startFailedSelection()
						actionMode?.finish()
						true
					}
					R.id.delete -> {
						deleteNotDownloadingSelection()
						actionMode?.finish()
						true
					}
					else -> false
				}
			}
			calculateBottomSelectionMenuChanges()
			return true
		}


		override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false

		override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean =
			when (item.itemId) {
				R.id.chapter_select_all -> {
					selectAll()
					true
				}
				R.id.chapter_select_between -> {
					selectBetween()
					true
				}
				R.id.chapter_inverse -> {
					invertSelection()
					true
				}
				else -> false
			}

		override fun onDestroyActionMode(mode: ActionMode) {
			binding.bottomMenu.hide()
			binding.bottomMenu.clear()
			actionMode = null
			showFAB(fab!!)
			fastAdapter.getSelectExtension().deselect()
		}
	}
}