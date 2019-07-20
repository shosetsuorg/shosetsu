package com.github.doomsdayrs.apps.shosetsu.ui.listeners

import android.util.Log
import android.view.MenuItem
import androidx.core.view.GravityCompat

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

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        mainActivity.navigationView.setCheckedItem(menuItem)
        when (menuItem.itemId) {
            R.id.nav_library -> {
                Log.d("Nav", "Library selected")
                mainActivity.supportFragmentManager.beginTransaction()
                        .addToBackStack("tag")
                        .replace(R.id.fragment_container, mainActivity.libraryFragment)
                        .commit()
            }
            R.id.nav_catalogue -> {
                Log.d("Nav", "Catalogue selected")
                mainActivity.supportFragmentManager.beginTransaction()
                        .addToBackStack("tag")
                        .replace(R.id.fragment_container, CataloguesFragment())
                        .commit()
            }
            R.id.nav_settings -> {
                Log.d("Nav", "Settings selected")
                mainActivity.supportFragmentManager
                        .beginTransaction()
                        .addToBackStack("tag")
                        .replace(R.id.fragment_container, mainActivity.settingsFragment)
                        .commit()
            }
            R.id.nav_downloads -> {
                Log.d("Nav", "Downloads Selected")
                mainActivity.supportFragmentManager
                        .beginTransaction()
                        .addToBackStack("tag")
                        .replace(R.id.fragment_container, mainActivity.downloadsFragment)
                        .commit()
            }
            R.id.nav_updater -> {
                Log.d("Nav", "Updater Selected")
                mainActivity.supportFragmentManager
                        .beginTransaction()
                        .addToBackStack("tag")
                        .replace(R.id.fragment_container, mainActivity.updatesFragment)
                        .commit()
            }
        }
        mainActivity.drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }
}
