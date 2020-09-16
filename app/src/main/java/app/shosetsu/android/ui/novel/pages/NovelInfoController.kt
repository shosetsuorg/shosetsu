package app.shosetsu.android.ui.novel.pages

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.core.os.bundleOf
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.migration.MigrationController
import app.shosetsu.android.ui.migration.MigrationController.Companion.TARGETS_BUNDLE_KEY
import app.shosetsu.android.ui.novel.NovelController
import app.shosetsu.android.view.base.ViewedController
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.abstracted.INovelInfoViewModel
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.id
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelInfoBinding
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelInfoBinding.inflate
import com.google.android.material.chip.Chip
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
class NovelInfoController(bundle: Bundle) : ViewedController<ControllerNovelInfoBinding>(bundle) {

	val viewModel: INovelInfoViewModel by viewModel()
	override val viewTitle: String = ""
	private var novelUI: NovelUI? = null

	// UI items
	init {
		setHasOptionsMenu(true)
	}

	override fun bindView(inflater: LayoutInflater): ControllerNovelInfoBinding = inflate(inflater)
	override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
		id.source_migrate -> {
			parentController?.router?.pushController(MigrationController(bundleOf(Pair(
					TARGETS_BUNDLE_KEY,
					arrayOf(args.getNovelID()).toIntArray()
			))).withFadeTransaction())
			true
		}
		id.webview -> {
			viewModel.openWebView()
			true
		}
		id.browser -> {
			viewModel.openBrowser()
			true
		}
		id.share -> {
			viewModel.share()
			true
		}
		else -> super.onOptionsItemSelected(item)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_novel, menu)
		menu.findItem(id.source_migrate).isVisible = novelUI?.bookmarked ?: false
	}

	override fun onViewCreated(view: View) {
		viewModel.setNovelID(args.getNovelID())
		binding.inLibrary?.setOnClickListener {
			viewModel.toggleBookmark()
		}

		binding.webView?.setOnClickListener {
			viewModel.openWebView()
		}

		binding.share?.setOnClickListener {
			viewModel.share()
		}

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

	/** Sets the data of this page */
	private fun setNovelData() {
		novelUI?.let { novelUI ->
			// Handle title
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
			listOf(binding.novelImage, binding.novelImageBackground).forEach { iV: ImageView? ->
				if (novelUI.imageURL.isNotEmpty()) {
					iV?.let {
						picasso(novelUI.imageURL, it)
					}
				} else {
					iV?.setImageResource(R.drawable.ic_broken_image_24dp)
				}
			}

			if (novelUI.bookmarked) {
				binding.inLibrary?.setChipIconResource(R.drawable.ic_heart_svg_filled)
				binding.inLibrary?.setText(R.string.in_library)
			} else {
				binding.inLibrary?.setChipIconResource(R.drawable.ic_heart_svg)
				binding.inLibrary?.setText(R.string.add_to_library)
			}

			// Show the option to add the novel
		}
	}

	private fun setFormatterName(text: String) {
		binding.novelSite?.text = text
	}

}