package app.shosetsu.android.ui.novel.pages

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import app.shosetsu.android.common.ext.getString
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.common.ext.withFadeTransaction
import app.shosetsu.android.ui.migration.MigrationController
import app.shosetsu.android.ui.novel.NovelController
import app.shosetsu.android.view.base.FABController
import app.shosetsu.android.view.base.ViewedController
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.abstracted.INovelInfoViewModel
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.id
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelInfoBinding
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelInfoBinding.inflate
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import app.shosetsu.android.common.dto.HResult.Empty as HEmpty
import app.shosetsu.android.common.dto.HResult.Error as HError
import app.shosetsu.android.common.dto.HResult.Loading as HLoading
import app.shosetsu.android.common.dto.HResult.Success as HSuccess

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
 * The page you see when you select a novel
 */
class NovelInfoController(bundle: Bundle) : ViewedController<ControllerNovelInfoBinding>(bundle), FABController {

	override fun bindView(inflater: LayoutInflater): ControllerNovelInfoBinding = inflate(inflater)

	val viewModel: INovelInfoViewModel by viewModel()

	private var novelUI: NovelUI? = null

	init {
		setHasOptionsMenu(true)
	}

	// UI items
	private val fab: FloatingActionButton? by lazy {
		(parentController as NovelController).fab
	}


	override fun onOptionsItemSelected(item: MenuItem): Boolean = novelUI?.let {
		when (item.itemId) {
			id.source_migrate -> {
				parentController?.router?.pushController(MigrationController(bundleOf(Pair(
						MigrationController.TARGETS_BUNDLE_KEY,
						arrayOf(novelUI!!.id).toIntArray()
				))).withFadeTransaction())
				true
			}
			id.webview -> {
				viewModel.openWebView(it)
				true
			}
			id.browser -> {
				viewModel.openBrowser(it)
				true
			}
			id.share -> {
				viewModel.share(it)
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	} ?: false

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_novel, menu)
		menu.findItem(id.source_migrate).isVisible = novelUI?.bookmarked ?: false
	}

	override fun onViewCreated(view: View) {
		viewModel.setNovelID(args.getInt(BUNDLE_NOVEL_ID))
		setObserver()
		setNovelData()
	}

	private fun setObserver() {
		viewModel.liveData.observe(this) {
			when (it) {
				is HSuccess -> {
					novelUI = it.data
					activity?.invalidateOptionsMenu()
					// If the data is not present, loads it
					if (!novelUI!!.loaded) (parentController as NovelController).refresh()
					else setNovelData()
				}
				is HError -> showError(it)
				is HEmpty -> {
				}
				is HLoading -> {
				}
			}
		}
		viewModel.formatterName.observe(this) {
			when (it) {
				is HSuccess -> app.shosetsu.android.common.ext.launchUI {
					setFormatterName(it.data)
				}
				is HError -> app.shosetsu.android.common.ext.launchUI {
					setFormatterName("Error on loading")
					showError(it)
				}
				is HEmpty -> app.shosetsu.android.common.ext.launchUI {
					setFormatterName("UNKNOWN")
				}
				is HLoading -> app.shosetsu.android.common.ext.launchUI {
					setFormatterName("Loading")
				}
			}
		}
	}

	override fun manipulateFAB(fab: FloatingActionButton) {
		fab.setOnClickListener {
			Log.d(logID(), "Toggling Bookmark")
			viewModel.toggleBookmark(novelUI!!)
			setFABIcon(fab)
		}
	}

	/**
	 * Sets the data of this page
	 */
	private fun setNovelData() {
		novelUI?.let { novelUI ->
			// Handle title
			this.setViewTitle(novelUI.title)
			binding.novelTitle?.text = novelUI.title

			// Handle authors
			if (novelUI.authors.isNotEmpty())
				binding.novelAuthor?.text = novelUI.authors.takeIf {
					it.isEmpty()
				}?.joinToString(", ") ?: getString(R.string.none)

			// Handle description
			binding.novelDescription?.text = novelUI.description

			// Handle artists
			if (novelUI.artists.isNotEmpty())
				binding.novelArtists?.text = novelUI.artists.takeIf {
					it.isEmpty()
				}?.joinToString(", ") ?: getString(R.string.none)

			// Handles the status of the novel
			when (novelUI.status) {
				Novel.Status.PAUSED -> binding.novelPublish?.setText(R.string.paused)
				Novel.Status.COMPLETED -> binding.novelPublish?.setText(R.string.completed)
				Novel.Status.PUBLISHING -> binding.novelPublish?.setText(R.string.publishing)
				else -> binding.novelPublish?.setText(R.string.unknown)
			}

			// Inserts the chips for genres
			binding.novelGenres?.removeAllViews()
			for (string in novelUI.genres) {
				val chip = Chip(binding.novelGenres!!.context)
				chip.text = string
				binding.novelGenres?.addView(chip)
			}

			// Loads the image
			if (novelUI.imageURL.isNotEmpty()) {
				Picasso.get().load(novelUI.imageURL).into(binding.novelImage, object : Callback {
					override fun onSuccess() {
						Picasso.get().load(novelUI.imageURL).into(binding.novelImageBackground)
					}

					override fun onError(e: Exception?) {
					}
				})
			}
			fab?.let {
				hideFAB(it)
				setFABIcon(it)
				showFAB(it)
			}
			// Show the option to add the novel
		}
		fab?.let {
			Log.d(logID(), "Setting FAB with setNovelData()")
			hideFAB(it)
			resetFAB(it)
			setFABIcon(it)
			manipulateFAB(it)
			showFAB(it)
		}
	}

	private fun setFormatterName(text: String) {
		binding.novelFormatter?.text = text
	}

	override fun setFABIcon(fab: FloatingActionButton) {
		Log.i(logID(), "Setting FAB image")
		fab.setImageResource(
				if (novelUI?.bookmarked == true)
					R.drawable.ic_baseline_check_circle_24
				else R.drawable.ic_add_circle_outline_24dp
		)
	}
}