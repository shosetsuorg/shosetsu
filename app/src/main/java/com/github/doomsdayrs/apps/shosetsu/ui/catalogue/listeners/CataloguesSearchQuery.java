package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners;

import android.app.Activity;
import android.widget.SearchView;

import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;

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
public class CataloguesSearchQuery implements SearchView.OnQueryTextListener {
    private final Activity activity;

    public CataloguesSearchQuery(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Utilities.search(activity, s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
