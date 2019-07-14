package com.github.doomsdayrs.apps.shosetsu.backend.async;

import android.os.AsyncTask;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.doomsdayrs.apps.shosetsu.ui.main.CatalogueFragment;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class CatalogueQuerySearch extends AsyncTask<String, Void, ArrayList<CatalogueNovelCard>> {
    private CatalogueFragment catalogueFragment;

    public CatalogueQuerySearch(CatalogueFragment catalogueFragment) {
        this.catalogueFragment = catalogueFragment;
    }

    /**
     * Search catalogue
     * @param strings ignored
     * @return List of results
     */
    @Override
    protected ArrayList<CatalogueNovelCard> doInBackground(String... strings) {
        ArrayList<CatalogueNovelCard> result = new ArrayList<>();
        try {
            List<Novel> novels = catalogueFragment.formatter.search(strings[0]);
            for (Novel novel : novels)
                result.add(new CatalogueNovelCard(novel.imageURL, novel.title, novel.link));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}