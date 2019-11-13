package com.github.doomsdayrs.apps.shosetsu.ui.novel.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.backend.ErrorView;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;

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
public class NovelLoader extends AsyncTask<Activity, Void, Boolean> {
    private String novelURL;
    private Formatter formatter;
    private NovelPage novelPage;
    private int novelID;

    ErrorView errorView;

    private final boolean loadAll;


    /**
     * Constructor
     *
     * @param novelFragment reference to the fragment
     */
    public NovelLoader(NovelFragment novelFragment, ErrorView errorView, boolean loadAll) {
        this.novelURL = novelFragment.novelURL;
        this.formatter = novelFragment.formatter;
        this.novelID = novelFragment.novelID;
        this.novelPage = novelFragment.novelPage;
        this.loadAll = loadAll;
        this.errorView = errorView;
    }


    private NovelLoader(NovelLoader novelLoader) {
        this.novelURL = novelLoader.novelURL;
        assert (novelLoader.formatter != null);
        this.formatter = novelLoader.formatter;
        this.novelPage = novelLoader.novelPage;
        this.novelID = novelLoader.novelID;
        this.loadAll = novelLoader.loadAll;
        this.errorView = novelLoader.errorView;
    }

    /**
     * Background process
     *
     * @param voids activity to work with
     * @return if completed
     */
    @Override

    protected Boolean doInBackground(Activity... voids) {
        Activity activity = voids[0];
        Log.d("Loading", String.valueOf(novelURL));
        errorView.activity.runOnUiThread(() -> errorView.errorView.setVisibility(View.GONE));

        try {
            Document document = docFromURL(novelURL, formatter.hasCloudFlare());
            assert (document != null);
            novelPage = formatter.parseNovel(document);
            if (!activity.isDestroyed() && !Database.DatabaseNovels.inDatabase(novelID)) {
                Database.DatabaseNovels.addToLibrary(formatter.getID(), novelPage, novelURL, com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD.getA());
            }
            //TODO The getNovelID in this method likely will cause slowdowns due to IO
            int novelID = getNovelIDFromNovelURL(novelURL);
            for (NovelChapter novelChapter : novelPage.novelChapters)
                if (!activity.isDestroyed() && !Database.DatabaseChapter.inChapters(novelChapter.link))
                    Database.DatabaseChapter.addToChapters(novelID, novelChapter);


            Log.d("Loaded Novel:", novelPage.title);
            return true;
        } catch (Exception e) {
            errorView.activity.runOnUiThread(() -> errorView.errorView.setVisibility(View.VISIBLE));
            errorView.activity.runOnUiThread(() -> errorView.errorMessage.setText(e.getMessage()));
            errorView.activity.runOnUiThread(() -> errorView.errorButton.setOnClickListener(view -> refresh(activity)));
            e.printStackTrace();

        }
        return false;
    }

    private void refresh(Activity activity) {
        new NovelLoader(this).execute(activity);
    }

    /**
     * Show progress bar
     */
    @Override
    protected void onPreExecute() {
        if (loadAll) {
            if (novelFragment != null) {
                novelFragment.progressBar.setVisibility(View.VISIBLE);
            }
        } else {
            if (novelFragmentInfo != null) {
                novelFragmentInfo.swipeRefreshLayout.setRefreshing(true);
            }
        }
    }

    @Override
    protected void onCancelled() {
        onPostExecute(false);
    }

    /**
     * Hides progress and sets data
     *
     * @param aBoolean if completed
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        Activity activity = null;
        if (novelFragmentInfo != null)
            activity = novelFragmentInfo.getActivity();
        else if (novelFragment != null) {
            activity = novelFragment.getActivity();
        }

        if (activity != null) {
            if (loadAll) {
                if (novelFragment != null) {
                    novelFragment.progressBar.setVisibility(View.GONE);
                }
            } else {
                if (novelFragmentInfo != null) {
                    novelFragmentInfo.swipeRefreshLayout.setRefreshing(false);
                }
                if (novelFragment != null && Database.DatabaseNovels.inDatabase(novelFragment.novelID)) {
                    try {
                        Database.DatabaseNovels.updateData(novelURL, novelPage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (aBoolean) {
                Statics.mainActionBar.setTitle(novelPage.title);
                activity.runOnUiThread(() -> {
                    if (loadAll)
                        if (novelFragment != null) {
                            novelFragment.novelFragmentInfo.setData();
                        } else {
                            novelFragmentInfo.setData();
                        }
                });
                if (loadAll) {
                    activity.runOnUiThread(() -> new ChapterLoader(novelPage, novelURL, formatter).execute());
                }
            }
        }

    }
}