package com.github.doomsdayrs.apps.shosetsu.backend.async;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueFragmentHitBottom;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        if (catalogueFragment.formatter.hasCloudFlare()) {
            if (catalogueFragment.getActivity() != null)
                catalogueFragment.getActivity().runOnUiThread(() -> Toast.makeText(catalogueFragment.getContext(), "CLOUDFLARE", Toast.LENGTH_SHORT).show());
        }

        try {
            List<Novel> novels;
            if (catalogueFragment.formatter.hasCloudFlare()) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
        } else catalogueFragment.swipeRefreshLayout.setRefreshing(false);
    }
}