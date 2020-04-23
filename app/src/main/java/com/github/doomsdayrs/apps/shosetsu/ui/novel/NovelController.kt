package com.github.doomsdayrs.apps.shosetsu.ui.novel

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.Novel
import com.bluelinelabs.conductor.Controller
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.controllers.ViewedController
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.getNovelPage
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.getNovelStatus
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.isNotInNovels
import com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters.NovelPagerAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.novel.async.NovelLoader
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelChaptersController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelInfoController
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status
import com.github.doomsdayrs.apps.shosetsu.variables.ext.context
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Formatters
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener


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
 */
class NovelController(bundle: Bundle) : ViewedController(bundle) {
	companion object {
		const val BUNDLE_URL = "novelURL"
		const val BUNDLE_ID = "novelID"
		const val BUNDLE_FORMATTER = "formatter"
	}

	override val layoutRes: Int = R.layout.novel

	// This is a never before loaded novel
	private var new: Boolean = true

	var novelID = -2
	var novelURL: String = ""
	var novelPage = Novel.Info()
	var formatter: Formatter

	var status = Status.UNREAD

	var novelInfoController: NovelInfoController? = null
	var novelChaptersController: NovelChaptersController? = null

	@Attach(R.id.fragment_novel_tabLayout)
	var novelTabLayout: TabLayout? = null

	@Attach(R.id.fragment_novel_viewpager)
	var novelViewpager: ViewPager? = null

	@Attach(R.id.fragment_novel_main_refresh)
	var fragmentNovelMainRefresh: SwipeRefreshLayout? = null

	init {
		setHasOptionsMenu(true)
		novelID = bundle.getInt(BUNDLE_ID)
		novelURL = bundle.getString(BUNDLE_URL, "")
		formatter = Formatters.getByID(bundle.getInt(BUNDLE_FORMATTER, -1))
	}

	override fun onSaveInstanceState(outState: Bundle) {
		outState.putInt("status", status.a)
		outState.putBoolean("new", new)
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle) {
		super.onRestoreInstanceState(savedInstanceState)
		status = Status.getStatus(savedInstanceState.getInt("status"))
		novelPage = getNovelPage(novelID)
		new = savedInstanceState.getBoolean("new")
		setViewPager()
	}

	override fun onViewCreated(view: View) {
		// Attach UI to program
		run {
			if (novelInfoController == null)
				novelInfoController = NovelInfoController()
			novelInfoController?.let {
				it.novelController = this
				it.novelID = novelID
			}
			if (novelChaptersController == null)
				novelChaptersController = NovelChaptersController()
			novelChaptersController?.let {
				it.novelController = this
				it.novelID = novelID
			}
		}
		//TODO FINISH TRACKING
		//boolean track = SettingsController.isTrackingEnabled();
		if (Utilities.isOnline && isNotInNovels(novelID)) {
			setViewPager()
			novelTabLayout!!.post {
				NovelLoader(
						novelURL,
						novelID,
						formatter,
						this,
						true
				).execute()
			}
		} else {
			novelPage = getNovelPage(novelID)
			new = false
			//   novelChapters = DatabaseChapter.getChapters(novelID)
			status = getNovelStatus(novelID)
			if (activity != null && activity!!.actionBar != null)
				activity!!.actionBar!!.title = novelPage.title
			setViewPager()
		}
		fragmentNovelMainRefresh?.setOnRefreshListener {
			novelInfoController?.let { it ->
				it.novelController?.let { novelController ->
					context?.toast("")
					NovelLoader(
							novelController.novelURL,
							novelController.novelID,
							novelController.formatter,
							novelController,
							true
					).execute()
				}
			}
		}
	}

	private fun setViewPager() {
		val fragments: MutableList<Controller> = ArrayList()
		run {
			Log.d("FragmentLoading", "Main")
			fragments.add(novelInfoController!!)
			Log.d("FragmentLoading", "Chapters")
			fragments.add(novelChaptersController!!)
		}
		val pagerAdapter = NovelPagerAdapter(this, fragments)
		novelViewpager?.adapter = pagerAdapter
		novelViewpager?.addOnPageChangeListener(TabLayoutOnPageChangeListener(novelTabLayout))
		novelTabLayout?.addOnTabSelectedListener(object : OnTabSelectedListener {
			override fun onTabSelected(tab: TabLayout.Tab) {
				novelViewpager?.currentItem = tab.position
			}

			override fun onTabUnselected(tab: TabLayout.Tab) {}
			override fun onTabReselected(tab: TabLayout.Tab) {}
		})
		novelTabLayout?.post { novelTabLayout?.setupWithViewPager(novelViewpager) }
	}

}