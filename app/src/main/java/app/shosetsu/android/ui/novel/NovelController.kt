package app.shosetsu.android.ui.novel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.ext.getNovelID
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.novel.adapters.NovelPagerAdapter
import app.shosetsu.android.view.base.FABController
import app.shosetsu.android.view.base.ViewedController
import app.shosetsu.android.viewmodel.abstracted.INovelViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelBinding
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelBinding.inflate
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
class NovelController(bundle: Bundle) : ViewedController<ControllerNovelBinding>(bundle), FABController {
	override fun bindView(inflater: LayoutInflater): ControllerNovelBinding =
			inflate(inflater)

	/** Floating action button */
	var fab: FloatingActionButton? = null

	/**
	 * View model of the major novel
	 */
	val viewModel: INovelViewModel by viewModel()

	var adapter: NovelPagerAdapter? = null

	/**
	 * Refreshes the novel
	 */
	fun refresh() {
		viewModel.refresh().observe(this) {
			when (it) {
				is HResult.Loading -> binding.swipeRefreshLayout.isRefreshing = true
				is HResult.Success -> binding.swipeRefreshLayout.isRefreshing = false
				is HResult.Error -> binding.swipeRefreshLayout.isRefreshing = false
				is HResult.Empty -> binding.swipeRefreshLayout.isRefreshing = false
			}
		}
	}

	override fun acceptFAB(fab: FloatingActionButton) {
		this.fab = fab
	}

	override fun setFABIcon(fab: FloatingActionButton) {
	}

	override fun onViewCreated(view: View) {
		viewModel.setNovelID(args.getNovelID())
		adapter = NovelPagerAdapter(this)
		binding.novelViewpager.adapter = adapter
		binding.novelViewpager.addOnPageChangeListener(TabLayoutOnPageChangeListener(binding.tabLayout))
		fab?.let {
			binding.novelViewpager.addOnPageChangeListener(adapter!!.PageController(it))
		}

		binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
			override fun onTabSelected(tab: TabLayout.Tab) {
				binding.novelViewpager.currentItem = tab.position
			}

			override fun onTabUnselected(tab: TabLayout.Tab) {}
			override fun onTabReselected(tab: TabLayout.Tab) {}
		})

		binding.tabLayout.post { binding.tabLayout.setupWithViewPager(binding.novelViewpager) }

		binding.swipeRefreshLayout.setOnRefreshListener {
			if (viewModel.isOnline())
				refresh()
			else toast(R.string.you_not_online)
		}
	}

}