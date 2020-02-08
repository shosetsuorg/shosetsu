package com.github.doomsdayrs.apps.shosetsu.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.github.doomsdayrs.api.shosetsu.services.core.ShosetsuLib
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.DownloadManager.initDownloadManager
import com.github.doomsdayrs.apps.shosetsu.backend.UpdateManager.init
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.WebviewCookieHandler
import com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CataloguesFragment
import com.github.doomsdayrs.apps.shosetsu.ui.downloads.DownloadsFragment
import com.github.doomsdayrs.apps.shosetsu.ui.extensions.ExtensionsFragment
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryFragment
import com.github.doomsdayrs.apps.shosetsu.ui.main.listener.NavigationSwapListener
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsFragment
import com.github.doomsdayrs.apps.shosetsu.ui.updates.UpdatesFragment
import com.github.doomsdayrs.apps.shosetsu.variables.requestPerms
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.AppUpdaterUtils.UpdateListener
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.Display
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient

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
//TODO Inform users to refresh their libraries
class MainActivity : AppCompatActivity(), Supporter {
    val cataloguesFragment = CataloguesFragment()
    val libraryFragment = LibraryFragment()
    val updatesFragment = UpdatesFragment()
    val settingsFragment = SettingsFragment()
    val downloadsFragment = DownloadsFragment()
    val scripManagementFragment = ExtensionsFragment()

    /**
     * Main activity
     *
     * @param savedInstanceState savedData from destruction
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestPerms()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        //  getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);


        val appUpdater = AppUpdater(this)
                .setUpdateFrom(UpdateFrom.XML)
                .setUpdateXML("https://raw.githubusercontent.com/Doomsdayrs/shosetsu/master/app/update.xml")
                .setDisplay(Display.DIALOG)
                .setTitleOnUpdateAvailable(getString(R.string.app_update_available))
                .setContentOnUpdateAvailable(getString(R.string.check_out_latest_app))
                .setTitleOnUpdateNotAvailable(getString(R.string.app_update_unavaliable))
                .setContentOnUpdateNotAvailable(getString(R.string.check_updates_later))
                .setButtonUpdate(getString(R.string.update_app_now_question)) //    .setButtonUpdateClickListener(...)
                .setButtonDismiss(getString(R.string.update_dismiss)) //       .setButtonDismissClickListener(...)
                .setButtonDoNotShowAgain(getString(R.string.update_not_interested)) //     .setButtonDoNotShowAgainClickListener(...)
                .setIcon(R.drawable.ic_system_update_alt_black_24dp)
                .setCancelable(true)
                .showEvery(5)
        appUpdater.start()
        // updateUtils()

        //Sets the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        nav_view.setNavigationItemSelectedListener(NavigationSwapListener(this))
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        // Webview agent retrieval
        WebViewScrapper.setUa(findViewById<WebView>(R.id.absolute_webView).settings.userAgentString)

        ShosetsuLib.httpClient = OkHttpClient.Builder()
                .cookieJar(WebviewCookieHandler())
                .build()

        initDownloadManager(this)
        when (intent.action) {
            Intent.ACTION_USER_BACKGROUND -> {
                Log.i("MainActivity", "Updating novels")
                init(this)
                transitionView(updatesFragment)
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.i("MainActivity", "Bootup")
                if (Utilities.isOnline)
                    init(this)
            }
            else -> {
                //Prevent the frag from changing on rotation
                if (savedInstanceState == null) {
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, libraryFragment).commit()
                    nav_view.setCheckedItem(R.id.nav_library)

                    // Initalzies formatters
                    //TODO Popup progress for this
                }
            }
        }
    }

    fun transitionView(target: Fragment) {
        supportFragmentManager.beginTransaction()
                .addToBackStack("tag")
                .replace(R.id.fragment_container, target)
                .commit()
    }

    @Suppress("unused")
    private fun updateUtils() {
        val appUpdaterUtils = AppUpdaterUtils(this)
                .setUpdateFrom(UpdateFrom.XML).setUpdateXML("https://raw.githubusercontent.com/Doomsdayrs/shosetsu/master/app/update.xml")
                .withListener(object : UpdateListener {
                    override fun onSuccess(update: Update, isUpdateAvailable: Boolean) {
                        Log.d("Latest Version", isUpdateAvailable.toString())
                        Log.d("Latest Version", update.latestVersion)
                        Log.d("Latest Version", update.latestVersionCode.toString())
                    }

                    override fun onFailed(error: AppUpdaterError) {
                        Log.d("AppUpdater Error", "Something went wrong")
                    }
                })
        appUpdaterUtils.start()
    }

    /**
     * When the back button while drawer is open, close it.
     */
    override fun onBackPressed() {
        if (drawer_layout!!.isDrawerOpen(GravityCompat.START)) drawer_layout!!.closeDrawer(GravityCompat.START) else {
            super.onBackPressed()
        }
    }

    override fun setTitle(name: String?) {
        if (supportActionBar != null) supportActionBar!!.title = name
    }

    fun getNavigationView(): NavigationView? {
        return nav_view
    }

    fun getDrawerLayout(): DrawerLayout? {
        return drawer_layout
    }
}