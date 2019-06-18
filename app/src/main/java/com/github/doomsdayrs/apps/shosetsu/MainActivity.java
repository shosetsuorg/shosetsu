package com.github.doomsdayrs.apps.shosetsu;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.github.doomsdayrs.apps.shosetsu.database.DBHelper;
import com.github.doomsdayrs.apps.shosetsu.database.Database;
import com.github.doomsdayrs.apps.shosetsu.fragment.CataloguesFragment;
import com.github.doomsdayrs.apps.shosetsu.fragment.LibraryFragment;
import com.github.doomsdayrs.apps.shosetsu.fragment.SettingsFragment;
import com.github.doomsdayrs.apps.shosetsu.settings.Settings;
import com.github.doomsdayrs.apps.shosetsu.settings.SettingsController;

/**
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Foobar is distributed in the hope that it will be useful,
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
@SuppressWarnings("EmptyMethod")
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private final LibraryFragment libraryFragment = new LibraryFragment();
    private final CataloguesFragment cataloguesFragment = new CataloguesFragment();
    private final SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        // Settings setup
        SettingsController.view = getSharedPreferences("view", 0);
        SettingsController.download = getSharedPreferences("download", 0);
        SettingsController.advanced = getSharedPreferences("advanced", 0);
        SettingsController.tracking = getSharedPreferences("tracking", 0);
        Settings.connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        SettingsController.init();

        //Set the content view
        setContentView(R.layout.activity_main);
        //Sets the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Sets up the sidebar
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Sets up DB
        DBHelper helper = new DBHelper(this);
        Database.library = helper.getWritableDatabase();

        //Prevent the frag from changing on rotation
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, libraryFragment).commit();
            navigationView.setCheckedItem(R.id.nav_library);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        navigationView.setCheckedItem(menuItem);
        switch (menuItem.getItemId()) {
            case R.id.nav_library: {
                Log.e("Nav", "Library selected");
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack("tag")
                        .replace(R.id.fragment_container, libraryFragment)
                        .commit();
            }
            break;
            case R.id.nav_catalogue: {
                Log.e("Nav", "Catalogue selected");
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack("tag")
                        .replace(R.id.fragment_container, cataloguesFragment)
                        .commit();
            }
            break;
            case R.id.nav_settings: {
                Log.e("Nav", "Settings selected");
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("tag")
                        .replace(R.id.fragment_container, settingsFragment)
                        .commit();
            }
            break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}
