package com.github.doomsdayrs.apps.shosetsu.ui.updates

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.UpdateManager
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.newStruc.view.base.ViewedController
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.updatesDao
import com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters.UpdatedDaysPager
import com.github.doomsdayrs.apps.shosetsu.variables.ext.context
import com.github.doomsdayrs.apps.shosetsu.variables.ext.getString
import com.github.doomsdayrs.apps.shosetsu.variables.ext.trimDate
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import org.joda.time.DateTime
import java.util.*

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

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return if (item.itemId == R.id.updater_now) {
			if (context != null) {
				UpdateManager.init(context!!)
				true
			} else false
		} else false
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_updater, menu)
	}

	override fun onViewCreated(view: View) {
		Utilities.setActivityTitle(activity, getString(R.string.updates))
		setViewPager()
	}

	private fun setViewPager() {
		val updatesFragments = ArrayList<UpdateController>()
		val days = updatesDao.getTotalDays()
		Log.d("TotalDays", days.toString())
		var startTime = updatesDao.getStartingDayTime()
		Log.d("StartingDay", DateTime(startTime).toString())
		for (x in 0 until days) {
			val updateFragment = UpdateController()
			updateFragment.date = (startTime)
			startTime += 86400000
			updatesFragments.add(updateFragment)
		}
		// Removing empty days
		for (x in updatesFragments.size - 1 downTo 1) {
			val updateFragment = updatesFragments[x]
			val c = updatesDao.loadDayCountBetweenDates(
					updateFragment.date,
					updateFragment.date + 86399999
			)
			if (c <= 0) updatesFragments.removeAt(x)
		}
		// TODAY
		val updateFragment = UpdateController()
		updateFragment.date = DateTime(System.currentTimeMillis()).trimDate().millis
		updatesFragments.add(updateFragment)
		updatesFragments.reverse()
		val pagerAdapter = UpdatedDaysPager(this, updatesFragments.toTypedArray())
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