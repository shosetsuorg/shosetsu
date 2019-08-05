package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners;

import android.os.Build;
import android.util.Log;
import android.widget.SearchView;

import com.github.doomsdayrs.apps.shosetsu.backend.async.CatalogueQuerySearch;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueFragment;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
