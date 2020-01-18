package com.github.doomsdayrs.apps.shosetsu.ui.main.listener

import android.util.Log
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.main.MainActivity
import com.google.android.material.navigation.NavigationView

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
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
class NavigationSwapListener(private val mainActivity: MainActivity) : NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        mainActivity.getNavigationView()?.setCheckedItem(menuItem)
        when (menuItem.itemId) {
            R.id.nav_library -> {
                mainActivity.supportFragmentManager.popBackStack()
                Log.d("Nav", "Library selected")
                mainActivity.transitionView(mainActivity.libraryFragment)
            }
            R.id.nav_catalogue -> {
                Log.d("Nav", "Catalogue selected")
                mainActivity.transitionView(mainActivity.cataloguesFragment)
            }
            R.id.nav_extensions -> {
                Log.d("Nav", "Extensions selected")
                mainActivity.transitionView(mainActivity.scripManagementFragment)
            }
            R.id.nav_settings -> {
                Log.d("Nav", "Settings selected")
                mainActivity.transitionView(mainActivity.settingsFragment)
            }
            R.id.nav_downloads -> {
                mainActivity.supportFragmentManager.popBackStack()
                Log.d("Nav", "Downloads Selected")
                mainActivity.transitionView(mainActivity.downloadsFragment)
            }
            R.id.nav_updater -> {
                Log.d("Nav", "Updater Selected")
                mainActivity.transitionView(mainActivity.updatesFragment)
            }
        }
        mainActivity.getDrawerLayout()?.closeDrawer(GravityCompat.START)
        return true
    }

}