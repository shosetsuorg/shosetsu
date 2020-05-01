package com.github.doomsdayrs.apps.shosetsu.ui.updates

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

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.services.UpdateService
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.common.ext.getString
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters.UpdatedDaysPager
import com.github.doomsdayrs.apps.shosetsu.view.base.ViewedController
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IUpdatesViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * shosetsu
 * 15 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdatesController : ViewedController() {
	init {
		setHasOptionsMenu(true)
	}

	override val layoutRes: Int = R.layout.update

	@Attach(R.id.viewpager)
	var viewpager: ViewPager? = null

	@Attach(R.id.tabLayout)
	var tabLayout: TabLayout? = null

	private val updatesViewModel: IUpdatesViewModel by viewModel()

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return if (item.itemId == R.id.updater_now) {
			context?.let { UpdateService.init(it); true } ?: false
		} else false
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_updater, menu)
	}

	override fun onViewCreated(view: View) {
		Utilities.setActivityTitle(activity, getString(R.string.updates))
		GlobalScope.launch {
			setViewPager()
		}
	}

	private suspend fun setViewPager() {
		val pagerAdapter = UpdatedDaysPager(
				this,
				updatesViewModel.createControllers().toTypedArray()
		)
		viewpager?.adapter = pagerAdapter
		viewpager?.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
		tabLayout?.addOnTabSelectedListener(object : OnTabSelectedListener {
			override fun onTabSelected(tab: TabLayout.Tab) {
				viewpager!!.currentItem = tab.position
			}

			override fun onTabUnselected(tab: TabLayout.Tab) {}
			override fun onTabReselected(tab: TabLayout.Tab) {}
		})
		tabLayout?.post { tabLayout?.setupWithViewPager(viewpager) }
	}
}