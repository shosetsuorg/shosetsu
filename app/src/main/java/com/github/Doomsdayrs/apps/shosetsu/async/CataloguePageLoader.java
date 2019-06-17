package com.github.Doomsdayrs.apps.shosetsu.async;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.Doomsdayrs.apps.shosetsu.fragment.CatalogueFragement;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.CatalogueNovelCard;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
 * 17 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class CataloguePageLoader extends AsyncTask<Integer, Void, Boolean> {
    private CatalogueFragement catalogueFragement;
    private Formatter formatter;
    private ArrayList<CatalogueNovelCard> catalogueNovelCards;

    public CataloguePageLoader(CatalogueFragement catalogueFragement, Formatter formatter, ArrayList<CatalogueNovelCard> catalogueNovelCards) {
        this.catalogueFragement = catalogueFragement;
        this.formatter = formatter;
        this.catalogueNovelCards = catalogueNovelCards;
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {
        Log.d("Loading", "Catalogue");
        try {
            List<Novel> novels;
            if (integers.length == 0)
                novels = formatter.parseLatest(formatter.getLatestURL(1));
            else novels = formatter.parseLatest(formatter.getLatestURL(integers[0]));

            for (Novel novel : novels)
                catalogueNovelCards.add(new CatalogueNovelCard(novel.imageURL, novel.title, new URI(novel.link)));
            catalogueFragement.library_view.post(() -> catalogueFragement.library_Adapter.notifyDataSetChanged());

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onCancelled() {
        catalogueFragement.progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onPreExecute() {
        catalogueFragement.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        catalogueFragement.progressBar.setVisibility(View.GONE);
    }
}