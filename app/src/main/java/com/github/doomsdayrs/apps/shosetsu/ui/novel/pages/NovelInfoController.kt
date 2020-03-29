package com.github.doomsdayrs.apps.shosetsu.ui.novel.pages

import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.id
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.controllers.ViewedController
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
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
	var novelFragment: NovelController? = null

	@Attach(id.fragment_novel_add)
	var fragmentNovelAdd: FloatingActionButton? = null

	@Attach(id.fragment_novel_title)
	var fragmentNovelTitle: TextView? = null

	@Attach(id.fragment_novel_author)
	var fragmentNovelAuthor: TextView? = null

	@Attach(id.fragment_novel_status)
	var fragmentNovelStatus: TextView? = null

	@Attach(id.fragment_novel_description)
	var fragmentNovelDescription: TextView? = null

	@Attach(id.fragment_novel_publish)
	var fragmentNovelPublish: TextView? = null

	@Attach(id.fragment_novel_artists)
	var fragmentNovelArtists: TextView? = null

	@Attach(id.fragment_novel_genres)
	var fragmentNovelGenres: ChipGroup? = null

	@Attach(id.fragment_novel_formatter)
	var fragmentNovelFormatter: TextView? = null

	@Attach(id.fragment_novel_image)
	var fragmentNovelImage: ImageView? = null

	@Attach(id.fragment_novel_image_background)
	var fragmentNovelImageBackground: ImageView? = null

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			id.source_migrate -> {
				parentController?.router?.pushController(MigrationController(bundleOf(Pair(MigrationController.TARGETS_BUNDLE_KEY, arrayOf(novelFragment!!.novelID).toIntArray()))).withFadeTransaction())
				return true
			}
			id.webview -> {
				if (activity != null) openInWebview(activity!!, novelFragment!!.novelURL)
				return true
			}
			id.browser -> {
				if (activity != null) Utilities.openInBrowser(activity!!, novelFragment!!.novelURL)
				return true
			}
		}
		return false
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_novel, menu)
		menu.findItem(id.source_migrate).isVisible = novelFragment != null && Database.DatabaseNovels.isBookmarked(novelFragment!!.novelID)
	}

	override val layoutRes: Int = R.layout.novel_main


	override fun onViewCreated(view: View) {
		novelFragment = parentController as NovelController
		if (novelFragment != null) {
			novelFragment!!.novelInfoController = this
		}
		fragmentNovelAdd?.hide()
		if (novelFragment != null && Database.DatabaseNovels.isBookmarked(novelFragment!!.novelID)) fragmentNovelAdd?.setImageResource(R.drawable.ic_baseline_check_circle_24)
		setData(view)
		fragmentNovelAdd?.setOnClickListener {
			if (novelFragment != null)
				if (!Database.DatabaseNovels.isBookmarked(novelFragment!!.novelID)) {
					Database.DatabaseNovels.bookMark(novelFragment!!.novelID)
					fragmentNovelAdd?.setImageResource(R.drawable.ic_baseline_check_circle_24)
				} else {
					Database.DatabaseNovels.unBookmark(novelFragment!!.novelID)
					fragmentNovelAdd?.setImageResource(R.drawable.ic_add_circle_outline_24dp)
				}
		}
	}


	/**
	 * Sets the data of this page
	 */
	fun setData(view: View? = this.view) {
		view?.post {
			Utilities.setActivityTitle(activity, novelFragment!!.novelPage.title)
			fragmentNovelTitle?.text = novelFragment!!.novelPage.title
			if (novelFragment!!.novelPage.authors.isNotEmpty()) fragmentNovelAuthor?.text = novelFragment!!.novelPage.authors.contentToString()
			fragmentNovelDescription?.text = novelFragment!!.novelPage.description
			if (novelFragment!!.novelPage.artists.isNotEmpty()) fragmentNovelArtists?.text = novelFragment!!.novelPage.artists.contentToString()
			fragmentNovelStatus?.text = novelFragment!!.status.status
			when (novelFragment!!.novelPage.status) {
				Novel.Status.PAUSED -> {
					fragmentNovelPublish?.setText(R.string.paused)
				}
				Novel.Status.COMPLETED -> {
					fragmentNovelPublish?.setText(R.string.completed)
				}
				Novel.Status.PUBLISHING -> {
					fragmentNovelPublish?.setText(R.string.publishing)
				}
				else -> fragmentNovelPublish?.setText(R.string.unknown)
			}
			if (context != null) {
				val layoutInflater = LayoutInflater.from(context)
				for (string in novelFragment!!.novelPage.genres) {
					val chip = Chip(fragmentNovelGenres!!.context)
					chip.text = string
					fragmentNovelGenres?.addView(chip)
				}
			} else fragmentNovelGenres!!.visibility = View.GONE

			if (novelFragment!!.novelPage.imageURL.isNotEmpty()) {
				Picasso.get().load(novelFragment!!.novelPage.imageURL).into(fragmentNovelImage)
				Picasso.get().load(novelFragment!!.novelPage.imageURL).into(fragmentNovelImageBackground)
			}
			fragmentNovelAdd!!.show()
			fragmentNovelFormatter!!.text = novelFragment!!.formatter.name
		} ?: Log.e("NovelFragmentInfo", "NovelFragmentInfo view is null")
	}

	/**
	 * Constructor
	 */
	init {
		setHasOptionsMenu(true)
	}
}