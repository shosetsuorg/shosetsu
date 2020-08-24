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

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.observe
import androidx.viewpager.widget.ViewPager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.services.UpdateWorker.UpdateWorkerManager
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_DATE
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.setActivityTitle
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters.UpdatedDaysPager
import com.github.doomsdayrs.apps.shosetsu.view.base.ViewedController
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IUpdatesViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import org.kodein.di.generic.instance

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

	/**
	 * Days that have been processed
	 */
	val updateDays: ArrayList<UpdateController> = arrayListOf()
	private val updatesViewModel: IUpdatesViewModel by viewModel()
	private val updateManager: UpdateWorkerManager by instance()

	private val pagerAdapter = UpdatedDaysPager(this)

	override fun onOptionsItemSelected(item: MenuItem): Boolean =
			if (item.itemId == R.id.updater_now) {
				context?.let { updateManager.start(it); true } ?: false
			} else false

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
			inflater.inflate(R.menu.toolbar_updater, menu)

	override fun onViewCreated(view: View) {
		activity?.setActivityTitle(R.string.updates)
		setViewPager()
	}

	private fun observeData() {
		updatesViewModel.liveData.observe(this) { result ->
			when (result) {
				is HResult.Loading -> Log.i(logID(), "Implement Loading!")
				is HResult.Empty -> TODO("Empty UI")
				is HResult.Error -> showError(result)
				is HResult.Success -> {
					Log.d(logID(), "Recieved ${result.data.size}")
					val currentDays = updateDays
					val newDays = result.data.filter { newDate ->
						currentDays.forEach { day ->
							if (day.date == newDate)
								return@filter false
						}
						return@filter true
					}
					newDays.forEach {
						currentDays.add(UpdateController(bundleOf(
								BUNDLE_DATE to it
						)))
					}
					currentDays.sortBy { it.date }
					pagerAdapter.notifyDataSetChanged()
				}
			}
		}
	}

	private fun setViewPager() {
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