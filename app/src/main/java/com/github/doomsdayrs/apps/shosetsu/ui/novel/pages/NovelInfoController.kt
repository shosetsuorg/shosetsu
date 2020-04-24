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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.id
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.controllers.ViewedController
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.bookmarkNovel
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.isNovelBookmarked
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.unBookmarkNovel
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController.Companion.BUNDLE_FORMATTER
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController.Companion.BUNDLE_ID
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController.Companion.BUNDLE_URL
import com.github.doomsdayrs.apps.shosetsu.ui.novel.async.NovelLoader
import com.github.doomsdayrs.apps.shosetsu.variables.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.variables.ext.context
import com.github.doomsdayrs.apps.shosetsu.variables.ext.openInWebview
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast
import com.github.doomsdayrs.apps.shosetsu.variables.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Formatters
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
class NovelInfoController(bundle: Bundle) : ViewedController(bundle) {
	override val layoutRes: Int = R.layout.novel_main

	// Var
	var novelID: Int
	var novelController: NovelController? = null
	var novelPage = Novel.Info()
	var novelURL: String
	var formatter: Formatter
	var status = ReadingStatus.UNREAD

	init {
		setHasOptionsMenu(true)
		novelID = bundle.getInt(BUNDLE_ID, -1)
		novelURL = bundle.getString(BUNDLE_URL, "")
		formatter = Formatters.getByID(bundle.getInt(BUNDLE_FORMATTER, -1))
	}

	// UI items

	@Attach(R.id.fragment_novel_main_refresh)
	var fragmentNovelMainRefresh: SwipeRefreshLayout? = null

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
		novelController = parentController as NovelController?
		novelController?.novelInfoController = this

		if (Utilities.isOnline && Database.DatabaseNovels.isNotInNovels(novelID)) {
			novelController?.novelTabLayout!!.post {
				NovelLoader(
						novelURL,
						novelID,
						formatter,
						this,
						true
				).execute()
			}
		} else {
			novelPage = Database.DatabaseNovels.getNovelPage(novelID)
			//   novelChapters = DatabaseChapter.getChapters(novelID)
			status = Database.DatabaseNovels.getNovelStatus(novelID)
			if (activity != null && activity!!.actionBar != null)
				activity?.actionBar?.title = novelPage.title
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

		fragmentNovelMainRefresh?.setOnRefreshListener {
				novelController?.let { novelController ->
					context?.toast("")
					NovelLoader(
							novelController.novelURL,
							novelController.novelID,
							novelController.formatter,
							this,
							true
					).execute()
			}
		}
	}


	/**
	 * Sets the data of this page
	 */
	fun setData(view: View? = this.view) {
		view?.post {
			Utilities.setActivityTitle(activity, novelPage.title)
			novelTitle?.text = novelPage.title

			if (novelPage.authors.isNotEmpty())
				novelAuthor?.text = novelPage.authors.contentToString()

			novelDescription?.text = novelPage.description

			if (novelPage.artists.isNotEmpty())
				novelArtists?.text = novelPage.artists.contentToString()

			novelStatus?.text = status.status
			when (novelPage.status) {
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
				for (string in novelPage.genres) {
					val chip = Chip(novelGenres!!.context)
					chip.text = string
					novelGenres?.addView(chip)
				}
			} else novelGenres!!.visibility = View.GONE

			if (novelPage.imageURL.isNotEmpty()) {
				Picasso.get().load(novelPage.imageURL).into(novelImage)
				Picasso.get().load(novelPage.imageURL).into(novelImageBackground)
			}
			novelAdd?.show()
			novelFormatter?.text = novelController!!.formatter.name
		} ?: Log.e("NovelFragmentInfo", "NovelFragmentInfo view is null")
	}

}