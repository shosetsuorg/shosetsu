package com.github.doomsdayrs.apps.shosetsu.ui.listeners;

import android.os.Build;
import android.util.Log;
import android.widget.SearchView;

import com.github.doomsdayrs.apps.shosetsu.backend.async.CatalogueQuerySearch;
import com.github.doomsdayrs.apps.shosetsu.ui.main.CatalogueFragment;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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
public class CatalogueSearchQuery implements SearchView.OnQueryTextListener {
    private final CatalogueFragment catalogueFragment;

    public CatalogueSearchQuery(CatalogueFragment catalogueFragment) {
        this.catalogueFragment = catalogueFragment;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        catalogueFragment.isQuery = false;
        catalogueFragment.isInSearch = true;
        try {
            ArrayList<CatalogueNovelCard> searchResults = new CatalogueQuerySearch(catalogueFragment).execute(query).get();
            catalogueFragment.setLibraryCards(searchResults);
            return true;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("Library search", newText);
        catalogueFragment.isQuery = true;
        ArrayList<CatalogueNovelCard> recycleCards = new ArrayList<>(catalogueFragment.catalogueNovelCards);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recycleCards.removeIf(recycleCard -> !recycleCard.title.toLowerCase().contains(newText.toLowerCase()));
        } else {
            for (int x = recycleCards.size() - 1; x >= 0; x--) {
                if (!recycleCards.get(x).title.toLowerCase().contains(newText.toLowerCase())) {
                    recycleCards.remove(x);
                }
            }
        }
        catalogueFragment.setLibraryCards(recycleCards);
        return recycleCards.size() != 0;
    }


}
