package com.github.doomsdayrs.apps.shosetsu.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.backend.database.DBHelper;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CataloguesFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.downloads.DownloadsFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.main.listener.NavigationSwapListener;
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.updates.UpdatesFragment;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.google.android.material.navigation.NavigationView;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.initPreferences;

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
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
//TODO Inform users to refresh their libraries
public class MainActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public final CataloguesFragment cataloguesFragment = new CataloguesFragment();
    private WebView webView;

    public final LibraryFragment libraryFragment = new LibraryFragment();
    private WebViewScrapper webViewScrapper;
    public final UpdatesFragment updatesFragment = new UpdatesFragment();
    public final SettingsFragment settingsFragment = new SettingsFragment();
    public final DownloadsFragment downloadsFragment = new DownloadsFragment();

    /**
     * Main activity
     *
     * @param savedInstanceState savedData from destruction
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 1);
        }


        Utilities.view = getSharedPreferences("view", 0);
        Utilities.download = getSharedPreferences("download", 0);
        Utilities.advanced = getSharedPreferences("advanced", 0);
        Utilities.tracking = getSharedPreferences("tracking", 0);
        Utilities.backup = getSharedPreferences("backup", 0);
        initPreferences(this);

        switch (Settings.themeMode) {
            case 0:
                setTheme(R.style.Theme_MaterialComponents_Light_NoActionBar);
                break;
            case 1:
                setTheme(R.style.Theme_MaterialComponents_NoActionBar);
                break;
            case 2:
                setTheme(R.style.ThemeOverlay_MaterialComponents_Dark);
        }
        //  getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        Log.d("Updater", "Start");
        AppUpdater appUpdater = new AppUpdater(this)
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
                .showEvery(5);
        appUpdater.start();

        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this)
                .setUpdateFrom(UpdateFrom.XML).setUpdateXML("https://raw.githubusercontent.com/Doomsdayrs/shosetsu/master/app/update.xml")
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        Log.d("Latest Version", String.valueOf(isUpdateAvailable));
                        Log.d("Latest Version", update.getLatestVersion());
                        Log.d("Latest Version", String.valueOf(update.getLatestVersionCode()));
                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {
                        Log.d("AppUpdater Error", "Something went wrong");
                    }
                });
        appUpdaterUtils.start();

        Log.d("Updater", "Completed construction");


        // Settings setup
        Settings.connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Set the content view
        setContentView(R.layout.activity_main);

        //Sets the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Statics.mainActionBar = getSupportActionBar();
        //Sets up the sidebar
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationSwapListener(this));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        webView = findViewById(R.id.absolute_webview);
        webViewScrapper = new WebViewScrapper(webView, this);
        cataloguesFragment.setWebViewScrapper(webViewScrapper);

        // Sets up DB
        DBHelper helper = new DBHelper(this);
        Database.sqLiteDatabase = helper.getWritableDatabase();

        Download_Manager.init();

        //Prevent the frag from changing on rotation
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, libraryFragment).commit();
            navigationView.setCheckedItem(R.id.nav_library);
        }
    }

    /**
     * When the back button while drawer is open, close it.
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else {
            super.onBackPressed();

        }
    }

}
