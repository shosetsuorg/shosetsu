package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.Doomsdayrs.api.shosetsu.services.core.objects.Novel;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CatalogueHitBottom;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper.docFromURL;


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
 * 17 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class CataloguePageLoader extends AsyncTask<Integer, Void, Boolean> {
    // References to objects
    private final CatalogueFragment catalogueFragment;
    @Nullable
    private final CatalogueHitBottom catalogueHitBottom;

    /**
     * Constructor
     *
     * @param catalogueFragment the fragment this is assigned to (reference to parent)
     */
    public CataloguePageLoader(CatalogueFragment catalogueFragment) {
        this.catalogueFragment = catalogueFragment;
        catalogueHitBottom = null;
    }

    /**
     * @param catalogueFragment  the fragment this is assigned to (reference to parent)
     * @param catalogueHitBottom The listener to update once new page is loaded
     */
    public CataloguePageLoader(CatalogueFragment catalogueFragment, CatalogueHitBottom catalogueHitBottom) {
        this.catalogueFragment = catalogueFragment;
        this.catalogueHitBottom = catalogueHitBottom;
    }

    /**
     * Loads up the category
     *
     * @param integers if length = 0, loads first page otherwise loads the page # correlated to the integer
     * @return if this was completed or not
     */
    @NonNull
    @Override
    protected Boolean doInBackground(@NonNull Integer... integers) {
        Log.d("Loading", "Catalogue");
        catalogueFragment.library_view.post(() -> catalogueFragment.errorView.setVisibility(View.GONE));
        if (catalogueFragment.formatter.hasCloudFlare()) {
            if (catalogueFragment.getActivity() != null)
                catalogueFragment.getActivity().runOnUiThread(() -> Toast.makeText(catalogueFragment.getContext(), "CLOUDFLARE", Toast.LENGTH_SHORT).show());
        }

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
            novels = catalogueFragment.formatter.parseLatest(docFromURL(catalogueFragment.formatter.getLatestURL(1), catalogueFragment.formatter.hasCloudFlare()));
        else {
            novels = catalogueFragment.formatter.parseLatest(docFromURL(catalogueFragment.formatter.getLatestURL(integers[0]), catalogueFragment.formatter.hasCloudFlare()));
        }

        for (Novel novel : novels)
            catalogueFragment.catalogueNovelCards.add(new CatalogueNovelCard(novel.imageURL, novel.title, Database.DatabaseIdentification.getNovelIDFromNovelURL(novel.link), novel.link));
        catalogueFragment.library_view.post(() -> catalogueFragment.catalogueAdapter.notifyDataSetChanged());

        if (catalogueHitBottom != null) {
            catalogueFragment.library_view.post(() -> {
                catalogueFragment.catalogueAdapter.notifyDataSetChanged();
                catalogueFragment.library_view.addOnScrollListener(catalogueHitBottom);
            });
            catalogueHitBottom.running = false;
            Log.d("CatalogueFragmentLoad", "Completed");
        }
        Log.d("FragmentRefresh", "Complete");

        if (catalogueFragment.getActivity() != null)
            catalogueFragment.getActivity().runOnUiThread(() -> {
                catalogueFragment.catalogueAdapter.notifyDataSetChanged();
                catalogueFragment.swipeRefreshLayout.setRefreshing(false);
            });

        return true;
    }

    /**
     * Ends progress bar
     */
    @Override
    protected void onCancelled() {
        if (catalogueHitBottom != null)
            catalogueFragment.bottomProgressBar.setVisibility(View.INVISIBLE);
        else catalogueFragment.swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Starts the loading action
     */
    @Override
    protected void onPreExecute() {
        if (catalogueHitBottom != null)
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
        if (catalogueHitBottom != null) {
            catalogueFragment.bottomProgressBar.setVisibility(View.GONE);
            if (catalogueFragment.catalogueNovelCards.size() > 0)
                catalogueFragment.empty.setVisibility(View.GONE);
        } else catalogueFragment.swipeRefreshLayout.setRefreshing(false);
    }
}