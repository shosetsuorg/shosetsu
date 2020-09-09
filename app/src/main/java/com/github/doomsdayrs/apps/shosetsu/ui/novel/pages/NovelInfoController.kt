package com.github.doomsdayrs.apps.shosetsu.ui.novel.pages

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.id
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import com.github.doomsdayrs.apps.shosetsu.common.ext.*
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.view.base.FABView
import com.github.doomsdayrs.apps.shosetsu.view.base.ViewedController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.NovelUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.INovelInfoViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult.Empty as HEmpty
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult.Error as HError
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult.Loading as HLoading
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult.Success as HSuccess

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
class NovelInfoController(bundle: Bundle) : ViewedController(bundle), FABView {
	override val layoutRes: Int = R.layout.novel_main

	val viewModel: INovelInfoViewModel by viewModel()

	private var novelUI: NovelUI? = null

	init {
		setHasOptionsMenu(true)
	}

	// UI items
	private val fab: FloatingActionButton? by lazy {
		(parentController as NovelController).fab
	}

	@Attach(id.novel_title)
	var novelTitle: TextView? = null

	@Attach(id.novel_author)
	var novelAuthor: TextView? = null

	@Attach(id.novel_description)
	var novelDescription: TextView? = null

	@Attach(id.novel_publish)
	var novelPublish: TextView? = null

	@Attach(id.novel_artists)
	var novelArtists: TextView? = null

	@Attach(id.novel_genres)
	var novelGenres: ChipGroup? = null

	@Attach(id.novel_formatter)
	var novelFormatter: TextView? = null

	@Attach(id.novel_image)
	var novelImage: ImageView? = null

	@Attach(id.novel_image_background)
	var novelImageBackground: ImageView? = null

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
		viewModel.setNovelID(bundle.getInt(BUNDLE_NOVEL_ID))
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
				is HSuccess -> launchUI {
					setFormatterName(it.data)
				}
				is HError -> launchUI {
					setFormatterName("Error on loading")
					showError(it)
				}
				is HEmpty -> launchUI {
					setFormatterName("UNKNOWN")
				}
				is HLoading -> launchUI {
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
			activity?.setActivityTitle(novelUI.title)
			novelTitle?.text = novelUI.title

			// Handle authors
			if (novelUI.authors.isNotEmpty())
				novelAuthor?.text = novelUI.authors.contentToString()

			// Handle description
			novelDescription?.text = novelUI.description

			// Handle artists
			if (novelUI.artists.isNotEmpty())
				novelArtists?.text = novelUI.artists.contentToString()

			// Handles the status of the novel
			when (novelUI.status) {
				Novel.Status.PAUSED -> novelPublish?.setText(R.string.paused)
				Novel.Status.COMPLETED -> novelPublish?.setText(R.string.completed)
				Novel.Status.PUBLISHING -> novelPublish?.setText(R.string.publishing)
				else -> novelPublish?.setText(R.string.unknown)
			}

			// Inserts the chips for genres
			for (string in novelUI.genres) {
				val chip = Chip(novelGenres!!.context)
				chip.text = string
				novelGenres?.addView(chip)
			}

			// Loads the image
			if (novelUI.imageURL.isNotEmpty()) {
				Picasso.get().load(novelUI.imageURL).into(novelImage, object : Callback {
					override fun onSuccess() {
						Picasso.get().load(novelUI.imageURL).into(novelImageBackground)
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
			hideFAB(it)
			resetFAB(it)
			setFABIcon(it)
			manipulateFAB(it)
			showFAB(it)
		}
	}

	private fun setFormatterName(text: String) {
		novelFormatter?.text = text
	}

	override fun setFABIcon(fab: FloatingActionButton) {
		fab.setImageResource(
				if (novelUI?.bookmarked == true)
					R.drawable.ic_baseline_check_circle_24
				else R.drawable.ic_add_circle_outline_24dp
		)
	}
}