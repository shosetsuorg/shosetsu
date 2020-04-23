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
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.controllers.ViewedController
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.bookmarkNovel
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.isNovelBookmarked
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.unBookmarkNovel
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.variables.ext.context
import com.github.doomsdayrs.apps.shosetsu.variables.ext.openInWebview
import com.github.doomsdayrs.apps.shosetsu.variables.ext.withFadeTransaction
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso

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
 *
 *
 * The page you see when you select a novel
 *
 */
class NovelInfoController : ViewedController() {

	override val layoutRes: Int = R.layout.novel_main

	// Var

	var novelID: Int = -1
	var novelController: NovelController? = null

	// UI items

	@Attach(id.novel_add)
	var novelAdd: FloatingActionButton? = null

	@Attach(id.novel_title)
	var novelTitle: TextView? = null

	@Attach(id.novel_author)
	var novelAuthor: TextView? = null

	@Attach(id.novel_status)
	var novelStatus: TextView? = null

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

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			id.source_migrate -> {
				parentController?.router?.pushController(MigrationController(bundleOf(Pair(
						MigrationController.TARGETS_BUNDLE_KEY,
						arrayOf(novelID).toIntArray()
				))).withFadeTransaction())
				return true
			}
			id.webview -> {
				if (activity != null) openInWebview(activity!!, novelController!!.novelURL)
				return true
			}
			id.browser -> {
				if (activity != null)
					Utilities.openInBrowser(activity!!, novelController!!.novelURL)
				return true
			}
		}
		return false
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_novel, menu)
		menu.findItem(id.source_migrate).isVisible = novelController != null && isNovelBookmarked(novelID)
	}


	override fun onSaveInstanceState(outState: Bundle) {
		outState.putInt("novelID", novelID)
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle) {
		novelID = savedInstanceState.getInt("novelID", -1)
	}

	override fun onViewCreated(view: View) {
		novelController = parentController as NovelController
		if (novelController != null) {
			novelController!!.novelInfoController = this
		}
		novelAdd?.hide()
		if (novelController != null && isNovelBookmarked(novelID))
			novelAdd?.setImageResource(R.drawable.ic_baseline_check_circle_24)
		setData(view)
		novelAdd?.setOnClickListener {
			if (novelController != null)
				if (!isNovelBookmarked(novelID)) {
					bookmarkNovel(novelID)
					novelAdd?.setImageResource(R.drawable.ic_baseline_check_circle_24)
				} else {
					unBookmarkNovel(novelID)
					novelAdd?.setImageResource(R.drawable.ic_add_circle_outline_24dp)
				}
		}
	}


	/**
	 * Sets the data of this page
	 */
	fun setData(view: View? = this.view) {
		view?.post {
			Utilities.setActivityTitle(activity, novelController!!.novelPage.title)
			novelTitle?.text = novelController!!.novelPage.title

			if (novelController!!.novelPage.authors.isNotEmpty())
				novelAuthor?.text = novelController!!.novelPage.authors.contentToString()

			novelDescription?.text = novelController!!.novelPage.description

			if (novelController!!.novelPage.artists.isNotEmpty())
				novelArtists?.text = novelController!!.novelPage.artists.contentToString()

			novelStatus?.text = novelController!!.status.status
			when (novelController!!.novelPage.status) {
				Novel.Status.PAUSED -> {
					novelPublish?.setText(R.string.paused)
				}
				Novel.Status.COMPLETED -> {
					novelPublish?.setText(R.string.completed)
				}
				Novel.Status.PUBLISHING -> {
					novelPublish?.setText(R.string.publishing)
				}
				else -> novelPublish?.setText(R.string.unknown)
			}
			if (context != null) {
				for (string in novelController!!.novelPage.genres) {
					val chip = Chip(novelGenres!!.context)
					chip.text = string
					novelGenres?.addView(chip)
				}
			} else novelGenres!!.visibility = View.GONE

			if (novelController!!.novelPage.imageURL.isNotEmpty()) {
				Picasso.get().load(novelController!!.novelPage.imageURL).into(novelImage)
				Picasso.get().load(novelController!!.novelPage.imageURL).into(novelImageBackground)
			}
			novelAdd!!.show()
			novelFormatter!!.text = novelController!!.formatter.name
		} ?: Log.e("NovelFragmentInfo", "NovelFragmentInfo view is null")
	}

	/**
	 * Constructor
	 */
	init {
		setHasOptionsMenu(true)
	}
}