package com.github.doomsdayrs.apps.shosetsu.ui.listeners;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.github.doomsdayrs.apps.shosetsu.backend.async.CataloguePageLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.main.CatalogueFragment;

import java.util.concurrent.ExecutionException;

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
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class CatalogueFragmentHitBottom extends RecyclerView.OnScrollListener {
    final CatalogueFragment catalogueFragment;
    boolean running = false;

    public CatalogueFragmentHitBottom(CatalogueFragment catalogueFragment) {
        this.catalogueFragment = catalogueFragment;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

        if (!running)
            if (!catalogueFragment.library_view.canScrollVertically(1)) {
                Log.d("CatalogueFragmentLoad", "Getting next page");
                running = true;
                catalogueFragment.currentMaxPage++;
                try {
                    if (new CataloguePageLoader(catalogueFragment, CatalogueFragment.formatter, CatalogueFragment.catalogueNovelCards).execute(catalogueFragment.currentMaxPage).get()) {
                        catalogueFragment.library_view.post(() -> {
                            catalogueFragment.library_Adapter.notifyDataSetChanged();
                            catalogueFragment.library_view.addOnScrollListener(this);
                        });

                        running = false;
                        Log.d("CatalogueFragmentLoad", "Completed");
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }
}
