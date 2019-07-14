package com.github.doomsdayrs.apps.shosetsu.ui.listeners;

import androidx.annotation.NonNull;

import com.github.doomsdayrs.apps.shosetsu.ui.main.CatalogueFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.main.CataloguesFragment;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import android.util.Log;
import android.view.MenuItem;

import com.github.doomsdayrs.apps.shosetsu.MainActivity;
import com.github.doomsdayrs.apps.shosetsu.R;

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
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class MainActivityNavSwapFrag implements NavigationView.OnNavigationItemSelectedListener {

    private final MainActivity mainActivity;

    public MainActivityNavSwapFrag(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mainActivity.navigationView.setCheckedItem(menuItem);
        switch (menuItem.getItemId()) {
            case R.id.nav_library: {
                Log.d("Nav", "Library selected");
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .addToBackStack("tag")
                        .replace(R.id.fragment_container, mainActivity.libraryFragment)
                        .commit();
            }
            break;
            case R.id.nav_catalogue: {
                Log.d("Nav", "Catalogue selected");
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .addToBackStack("tag")
                        .replace(R.id.fragment_container, new CataloguesFragment())
                        .commit();
            }
            break;
            case R.id.nav_settings: {
                Log.d("Nav", "Settings selected");
                mainActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("tag")
                        .replace(R.id.fragment_container, mainActivity.settingsFragment)
                        .commit();
            }
            break;
            case R.id.nav_downloads: {
                Log.d("Nav", "Downloads Selected");
                mainActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("tag")
                        .replace(R.id.fragment_container, mainActivity.downloadsFragment)
                        .commit();
            }
            break;
        }
        mainActivity.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}
