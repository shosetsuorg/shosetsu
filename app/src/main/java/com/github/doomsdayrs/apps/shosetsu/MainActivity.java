package com.github.doomsdayrs.apps.shosetsu;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.backend.database.DBHelper;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.MainActivityNavSwapFrag;
import com.github.doomsdayrs.apps.shosetsu.ui.main.DownloadsFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.main.LibraryFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.main.SettingsFragment;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.google.android.material.navigation.NavigationView;

/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see https://www.gnu.org/licenses/ .
 * ====================================================================
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */

public class MainActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public NavigationView navigationView;

    public final LibraryFragment libraryFragment = new LibraryFragment();

    public final SettingsFragment settingsFragment = new SettingsFragment();
    public final DownloadsFragment downloadsFragment = new DownloadsFragment();


    /**
     * Main activity
     *
     * @param savedInstanceState savedData from destruction
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //
        super.onCreate(savedInstanceState);
        SettingsController.view = getSharedPreferences("view", 0);
        SettingsController.download = getSharedPreferences("download", 0);
        SettingsController.advanced = getSharedPreferences("advanced", 0);
        SettingsController.tracking = getSharedPreferences("tracking", 0);
        SettingsController.backup = getSharedPreferences("backup", 0);
        SettingsController.init();

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

                .setTitleOnUpdateAvailable("Update available")
                .setContentOnUpdateAvailable("Check out the latest version available of my app!")
                .setTitleOnUpdateNotAvailable("Update not available")
                .setContentOnUpdateNotAvailable("No update available. Check for updates again later!")
                .setButtonUpdate("Update now?")
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
                        Log.d("Latest Version Code", update.getLatestVersion());
                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {
                        Log.d("AppUpdater Error", "Something went wrong");
                    }
                });
        appUpdaterUtils.start();

        Log.d("Updater", "Completed construction");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
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
        navigationView.setNavigationItemSelectedListener(new MainActivityNavSwapFrag(this));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Sets up DB
        DBHelper helper = new DBHelper(this);
        Database.library = helper.getWritableDatabase();

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
        else super.onBackPressed();
    }

}
