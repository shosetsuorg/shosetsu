package com.github.doomsdayrs.apps.shosetsu.ui.novel.async;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.backend.ErrorView;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;

import org.jsoup.nodes.Document;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getNovelIDFromNovelURL;
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
 */

/**
 * Shosetsu
 * 17 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 * <p>
 * This task loads a novel for the novel fragment
 * </p>
 */
public class NovelLoader extends AsyncTask<Void, Void, Boolean> {
    private NovelFragment novelFragment;
    private boolean loadAll;
    private ErrorView errorView;


    /**
     * Constructor
     *
     * @param novelFragment reference to the fragment
     * @param errorView     Holder that contains error view and needed code;
     */
    public NovelLoader(NovelFragment novelFragment, ErrorView errorView, boolean loadAll) {
        this.novelFragment = novelFragment;
        this.loadAll = loadAll;
        this.errorView = errorView;
    }


    private NovelLoader(@NonNull NovelLoader novelLoader) {
        this.novelFragment = novelLoader.novelFragment;
        this.loadAll = novelLoader.loadAll;
        this.errorView = novelLoader.errorView;
    }


    @Override
    protected void onPreExecute() {
        novelFragment.novelFragmentInfo.swipeRefreshLayout.setRefreshing(true);
    }

    /**
     * Background process
     *
     * @param voids voided
     * @return if completed
     */
    @NonNull
    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.d("Loading", String.valueOf(novelFragment.novelURL));
        errorView.activity.runOnUiThread(() -> errorView.errorView.setVisibility(View.GONE));

        try {
            Document document = docFromURL(novelFragment.novelURL, novelFragment.formatter.hasCloudFlare());
            assert (document != null);
            novelFragment.novelPage = novelFragment.formatter.parseNovel(document);
            if (!errorView.activity.isDestroyed() && !Database.DatabaseNovels.inDatabase(novelFragment.novelID)) {
                Database.DatabaseNovels.addToLibrary(novelFragment.formatter.getID(), novelFragment.novelPage, novelFragment.novelURL, com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD.getA());
            }
            //TODO The getNovelID in this method likely will cause slowdowns due to IO
            int novelID = getNovelIDFromNovelURL(novelFragment.novelURL);
            for (NovelChapter novelChapter : novelFragment.novelPage.novelChapters)
                if (!errorView.activity.isDestroyed() && !Database.DatabaseChapter.inChapters(novelChapter.link))
                    Database.DatabaseChapter.addToChapters(novelID, novelChapter);


            Log.d("Loaded Novel:", novelFragment.novelPage.title);
            return true;
        } catch (Exception e) {
            errorView.activity.runOnUiThread(() -> errorView.errorView.setVisibility(View.VISIBLE));
            errorView.activity.runOnUiThread(() -> errorView.errorMessage.setText(e.getMessage()));
            errorView.activity.runOnUiThread(() -> errorView.errorButton.setOnClickListener(view -> refresh()));
            e.printStackTrace();

        }
        return false;
    }

    private void refresh() {
        new NovelLoader(this).execute();
    }

    @Override
    protected void onCancelled() {
        onPostExecute(false);
    }

    private void setData() {
        novelFragment.novelFragmentInfo.setData();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        assert (novelFragment != null);


        novelFragment.novelFragmentInfo.swipeRefreshLayout.setRefreshing(false);
        if (Database.DatabaseNovels.inDatabase(novelFragment.novelID)) {
            try {
                Database.DatabaseNovels.updateData(novelFragment.novelURL, novelFragment.novelPage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (result) {
            assert novelFragment != null;
            if (loadAll)
                errorView.activity.runOnUiThread(() -> new ChapterLoader(novelFragment.novelPage, novelFragment.novelURL, novelFragment.formatter).execute());
            errorView.activity.runOnUiThread(this::setData);
        }
    }
}