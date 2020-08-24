package com.github.doomsdayrs.apps.shosetsu.ui.novel

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.getNovelID
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters.NovelPagerAdapter
import com.github.doomsdayrs.apps.shosetsu.view.base.ViewedController
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.INovelViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
 */

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class NovelController(val bundle: Bundle) : ViewedController(bundle) {
	override val layoutRes: Int = R.layout.novel

	@Attach(R.id.fragment_novel_tabLayout)
	var novelTabLayout: TabLayout? = null

	@Attach(R.id.fragment_novel_viewpager)
	var novelViewpager: ViewPager? = null

	@Attach(R.id.swipeRefreshLayout)
	var swipeRefreshLayout: SwipeRefreshLayout? = null

	@Attach(R.id.fab)
	var fab: FloatingActionButton? = null

	/**
	 * View model of the major novel
	 */
	val viewModel: INovelViewModel by viewModel()

	/**
	 * Refreshes the novel
	 */
	fun refresh() {
		viewModel.refresh().observe(this) {
			when (it) {
				is HResult.Loading -> swipeRefreshLayout?.isRefreshing = true
				is HResult.Success -> swipeRefreshLayout?.isRefreshing = false
				is HResult.Error -> swipeRefreshLayout?.isRefreshing = false
				is HResult.Empty -> swipeRefreshLayout?.isRefreshing = false
			}
		}
	}

	override fun onViewCreated(view: View) {
		viewModel.setNovelID(bundle.getNovelID())
		val adapter = NovelPagerAdapter(this)
		novelViewpager?.adapter = adapter
		novelViewpager?.addOnPageChangeListener(TabLayoutOnPageChangeListener(novelTabLayout))
		fab?.let {
			novelViewpager?.addOnPageChangeListener(adapter.PageController(it))
		}

		novelTabLayout?.addOnTabSelectedListener(object : OnTabSelectedListener {
			override fun onTabSelected(tab: TabLayout.Tab) {
				novelViewpager?.currentItem = tab.position
			}

			override fun onTabUnselected(tab: TabLayout.Tab) {}
			override fun onTabReselected(tab: TabLayout.Tab) {}
		})

		novelTabLayout?.post { novelTabLayout?.setupWithViewPager(novelViewpager) }

		swipeRefreshLayout?.setOnRefreshListener { refresh() }
	}
}