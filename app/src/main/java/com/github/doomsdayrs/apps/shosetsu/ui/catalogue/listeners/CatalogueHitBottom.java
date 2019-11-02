package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async.CataloguePageLoader;

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
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class CatalogueHitBottom extends RecyclerView.OnScrollListener {
    private final CatalogueFragment catalogueFragment;
    public boolean running = false;

    public CatalogueHitBottom(CatalogueFragment catalogueFragment) {
        this.catalogueFragment = catalogueFragment;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        if (!catalogueFragment.isQuery && !catalogueFragment.isInSearch)
            if (!running)
                if (!catalogueFragment.library_view.canScrollVertically(1)) {
                    Log.d("CatalogueFragmentLoad", "Getting next page");
                    running = true;
                    catalogueFragment.currentMaxPage++;
                    new CataloguePageLoader(catalogueFragment, this).execute(catalogueFragment.currentMaxPage);
                }
    }
}
