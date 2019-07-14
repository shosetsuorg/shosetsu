package com.github.doomsdayrs.apps.shosetsu.backend.async;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.CatalogueFragmentHitBottom;
import com.github.doomsdayrs.apps.shosetsu.ui.main.CatalogueFragment;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard;

import java.io.IOException;
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
 * 17 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class CataloguePageLoader extends AsyncTask<Integer, Void, Boolean> {
    // References to objects
    private final CatalogueFragment catalogueFragment;
    private final CatalogueFragmentHitBottom catalogueFragmentHitBottom;

    /**
     * Constructor
     *
     * @param catalogueFragment the fragment this is assigned to (reference to parent)
     */
    public CataloguePageLoader(CatalogueFragment catalogueFragment) {
        this.catalogueFragment = catalogueFragment;
        catalogueFragmentHitBottom = null;
    }

    /**
     * @param catalogueFragment          the fragment this is assigned to (reference to parent)
     * @param catalogueFragmentHitBottom The listener to update once new page is loaded
     */
    public CataloguePageLoader(CatalogueFragment catalogueFragment, CatalogueFragmentHitBottom catalogueFragmentHitBottom) {
        this.catalogueFragment = catalogueFragment;
        this.catalogueFragmentHitBottom = catalogueFragmentHitBottom;
    }

    /**
     * Loads up the category
     *
     * @param integers if length = 0, loads first page otherwise loads the page # correlated to the integer
     * @return if this was completed or not
     */
    @Override
    protected Boolean doInBackground(Integer... integers) {
        Log.d("Loading", "Catalogue");
        catalogueFragment.library_view.post(() -> catalogueFragment.errorView.setVisibility(View.GONE));
        try {
            List<Novel> novels;

            // Loads novel list
            if (integers.length == 0)
                novels = catalogueFragment.formatter.parseLatest(catalogueFragment.formatter.getLatestURL(1));
            else {
                novels = catalogueFragment.formatter.parseLatest(catalogueFragment.formatter.getLatestURL(integers[0]));
            }

            for (Novel novel : novels)
                catalogueFragment.catalogueNovelCards.add(new CatalogueNovelCard(novel.imageURL, novel.title, novel.link));
            catalogueFragment.library_view.post(() -> catalogueFragment.catalogueNovelCardsAdapter.notifyDataSetChanged());

            if (catalogueFragmentHitBottom != null) {
                catalogueFragment.library_view.post(() -> {
                    catalogueFragment.catalogueNovelCardsAdapter.notifyDataSetChanged();
                    catalogueFragment.library_view.addOnScrollListener(catalogueFragmentHitBottom);
                });
                catalogueFragmentHitBottom.running = false;
                Log.d("CatalogueFragmentLoad", "Completed");
            }
            Log.d("FragmentRefresh", "Complete");

            if (catalogueFragment.getActivity() != null)
                catalogueFragment.getActivity().runOnUiThread(() -> {
                    catalogueFragment.catalogueNovelCardsAdapter.notifyDataSetChanged();
                    catalogueFragment.swipeRefreshLayout.setRefreshing(false);
                });

            return true;
        } catch (IOException e) {
            if (catalogueFragment.getActivity() != null)
                catalogueFragment.getActivity().runOnUiThread(() -> {
                    catalogueFragment.errorView.setVisibility(View.VISIBLE);
                        catalogueFragment.errorMessage.setText(e.getMessage());
                    if (catalogueFragmentHitBottom == null)
                        catalogueFragment.errorButton.setOnClickListener(view -> new CataloguePageLoader(catalogueFragment).execute(integers));
                    else
                        catalogueFragment.errorButton.setOnClickListener(view -> new CataloguePageLoader(catalogueFragment, catalogueFragmentHitBottom).execute(integers));

                });

        }
        return false;
    }

    /**
     * Ends progress bar
     */
    @Override
    protected void onCancelled() {
        if (catalogueFragmentHitBottom != null)
            catalogueFragment.bottomProgressBar.setVisibility(View.INVISIBLE);
        else catalogueFragment.swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Starts the loading action
     */
    @Override
    protected void onPreExecute() {
        if (catalogueFragmentHitBottom != null)
            catalogueFragment.bottomProgressBar.setVisibility(View.VISIBLE);
        else catalogueFragment.swipeRefreshLayout.setRefreshing(true);
    }

    /**
     * Once done remove progress bar
     *
     * @param aBoolean result of doInBackground
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (catalogueFragmentHitBottom != null) {
            catalogueFragment.bottomProgressBar.setVisibility(View.GONE);
            if (catalogueFragment.catalogueNovelCards.size() > 0)
                catalogueFragment.empty.setVisibility(View.GONE);
        }
        else catalogueFragment.swipeRefreshLayout.setRefreshing(false);
    }
}