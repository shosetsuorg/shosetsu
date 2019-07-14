package com.github.doomsdayrs.apps.shosetsu.ui.listeners;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;

import com.github.doomsdayrs.apps.shosetsu.backend.async.CataloguePageLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.main.CatalogueFragment;

import java.util.ArrayList;

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
public class CatalogueRefresh implements SwipeRefreshLayout.OnRefreshListener {
    private final CatalogueFragment catalogueFragment;

    public CatalogueRefresh(CatalogueFragment catalogueFragment) {
        this.catalogueFragment = catalogueFragment;
    }

    @Override
    public void onRefresh() {
        catalogueFragment.swipeRefreshLayout.setRefreshing(true);

        catalogueFragment.catalogueNovelCards = new ArrayList<>();
        catalogueFragment.currentMaxPage = 1;
        Log.d("FragmentRefresh", "Refreshing catalogue data");
        new CataloguePageLoader(catalogueFragment).execute();


    }
}
