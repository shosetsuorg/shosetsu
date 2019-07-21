package com.github.doomsdayrs.apps.shosetsu

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager
import com.github.doomsdayrs.apps.shosetsu.backend.SettingsController
import com.github.doomsdayrs.apps.shosetsu.backend.database.DBHelper
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.MainActivityNavSwapFrag
import com.github.doomsdayrs.apps.shosetsu.ui.main.DownloadsFragment
import com.github.doomsdayrs.apps.shosetsu.ui.main.LibraryFragment
import com.github.doomsdayrs.apps.shosetsu.ui.main.settings.SettingsMainFragment
import com.github.doomsdayrs.apps.shosetsu.ui.main.UpdatesFragment
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import com.github.doomsdayrs.apps.shosetsu.variables.Statics
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.Display
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView

    val libraryFragment = LibraryFragment()
    val updatesFragment = UpdatesFragment()
    val settingsMainFragment = SettingsMainFragment()
    val downloadsFragment = DownloadsFragment()


    /**
     * Main activity
     *
     * @param savedInstanceState savedData from destruction
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //
        super.onCreate(savedInstanceState)
        SettingsController.view = getSharedPreferences("view", 0)
        SettingsController.download = getSharedPreferences("download", 0)
        SettingsController.advanced = getSharedPreferences("advanced", 0)
        SettingsController.tracking = getSharedPreferences("tracking", 0)
        SettingsController.backup = getSharedPreferences("backup", 0)
        SettingsController.init()

        when (Settings.themeMode) {
            0 -> setTheme(R.style.Theme_MaterialComponents_Light_NoActionBar)
            1 -> setTheme(R.style.Theme_MaterialComponents_NoActionBar)
            2 -> setTheme(R.style.ThemeOverlay_MaterialComponents_Dark)
        }
        //  getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        Log.d("Updater", "Start")
        val appUpdater = AppUpdater(this)
                .setUpdateFrom(UpdateFrom.XML)
                .setUpdateXML("https://raw.githubusercontent.com/Doomsdayrs/shosetsu/master/app/update.xml")

                .setDisplay(Display.DIALOG)
                .setDisplay(Display.NOTIFICATION)
                .setDisplay(Display.SNACKBAR)

                .setTitleOnUpdateAvailable("ChapterUpdate available")
                .setContentOnUpdateAvailable("Check out the latest version available of my app!")
                .setTitleOnUpdateNotAvailable("ChapterUpdate not available")
                .setContentOnUpdateNotAvailable("No update available. Check for chapterUpdates again later!")
                .setButtonUpdate("ChapterUpdate now?")
                //    .setButtonUpdateClickListener(...)
                .setButtonDismiss("Maybe later")
                //       .setButtonDismissClickListener(...)
                .setButtonDoNotShowAgain("Huh, not interested")
                //     .setButtonDoNotShowAgainClickListener(...)
                .setIcon(R.drawable.ic_system_update_alt_black_24dp)
                .setCancelable(true)
                .showEvery(5)
        appUpdater.start()

        val appUpdaterUtils = AppUpdaterUtils(this)
                .setUpdateFrom(UpdateFrom.XML).setUpdateXML("https://raw.githubusercontent.com/Doomsdayrs/shosetsu/master/app/update.xml")
                .withListener(object : AppUpdaterUtils.UpdateListener {
                    override fun onSuccess(update: Update, isUpdateAvailable: Boolean?) {
                        Log.d("Latest Version", isUpdateAvailable.toString())
                        Log.d("Latest Version", update.latestVersion)
                        Log.d("Latest Version", update.latestVersionCode.toString())
                    }

                    override fun onFailed(error: AppUpdaterError) {
                        Log.d("AppUpdater Error", "Something went wrong")
                    }
                })
        appUpdaterUtils.start()

        Log.d("Updater", "Completed construction")


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
        // Settings setup
        Settings.connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        //Set the content view
        setContentView(R.layout.activity_main)

        //Sets the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        Statics.mainActionBar = supportActionBar
        //Sets up the sidebar
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(MainActivityNavSwapFrag(this))
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Sets up DB
        val helper = DBHelper(this)
        Database.library = helper.writableDatabase

        Download_Manager.init()

        //Prevent the frag from changing on rotation
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, libraryFragment).commit()
            navigationView.setCheckedItem(R.id.nav_library)
        }
    }

    /**
     * When the back button while drawer is open, close it.
     */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

}
