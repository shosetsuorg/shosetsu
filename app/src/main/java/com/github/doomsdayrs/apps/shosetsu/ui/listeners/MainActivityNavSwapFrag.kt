package com.github.doomsdayrs.apps.shosetsu.ui.listeners

import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment

import com.github.doomsdayrs.apps.shosetsu.MainActivity
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.main.catalogue.CataloguesFragment
import com.google.android.material.navigation.NavigationView

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 * Shosetsu
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
class MainActivityNavSwapFrag(private val mainActivity: MainActivity) : NavigationView.OnNavigationItemSelectedListener {

    private fun setRoot(fragment: Fragment, name: String = "tag") {
        mainActivity.supportFragmentManager
                .beginTransaction()
                .addToBackStack(name)
                .replace(R.id.fragment_container, fragment)
                .commit()
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        mainActivity.navigationView.setCheckedItem(menuItem)
        val nextFragment : Fragment = when (menuItem.itemId) {
            R.id.nav_library -> mainActivity.libraryFragment
            R.id.nav_updater -> mainActivity.updatesFragment
            R.id.nav_catalogue -> CataloguesFragment()
            R.id.nav_settings -> mainActivity.settingsMainFragment
            R.id.nav_downloads -> mainActivity.downloadsFragment
            else -> throw Exception("Invalid navigation item selected.")
        }
        setRoot(nextFragment)
        mainActivity.drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }
}
