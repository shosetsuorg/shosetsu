package com.github.doomsdayrs.apps.shosetsu.ui.novel.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentMain;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;

import java.io.IOException;
import java.util.ArrayList;

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

/**
 * This task loads a novel for the novel fragment
 */
public class NovelLoader extends AsyncTask<Activity, Void, Boolean> {
    // References
    private final NovelFragment novelFragment;
    private final NovelFragmentMain novelFragmentMain;
    private boolean C = true;

    public void setC(boolean c) {
        C = c;
    }

    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    private boolean loadAll;

    /**
     * Constructor
     *
     * @param novelFragment reference to the fragment
     */
    public NovelLoader(NovelFragment novelFragment, boolean loadAll) {
        this.novelFragment = novelFragment;
        this.loadAll = loadAll;
        this.novelFragmentMain = null;
    }

    public NovelLoader(NovelFragmentMain novelFragmentMain, boolean loadAll) {
        this.novelFragment = null;
        this.loadAll = loadAll;
        this.novelFragmentMain = novelFragmentMain;
    }


    /**
     * Background process
     *
     * @param voids activity to work with
     * @return if completed
     */
    @Override

    protected Boolean doInBackground(Activity... voids) {
        this.activity = voids[0];
        StaticNovel.novelPage = null;
        Log.d("Loading", StaticNovel.novelURL);
        if (loadAll) {
            if (novelFragment != null && novelFragment.getActivity() != null)
                novelFragment.getActivity().runOnUiThread(() -> novelFragment.errorView.setVisibility(View.GONE));

        } else if (novelFragmentMain != null && novelFragmentMain.getActivity() != null)
            novelFragmentMain.getActivity().runOnUiThread(() -> novelFragmentMain.novelFragment.errorView.setVisibility(View.GONE));


        try {
            StaticNovel.novelPage = StaticNovel.formatter.parseNovel(StaticNovel.novelURL);
            if (C && !Database.DatabaseLibrary.inLibrary(StaticNovel.novelURL)) {
                Database.DatabaseLibrary.addToLibrary(StaticNovel.formatter.getID(), StaticNovel.novelPage, StaticNovel.novelURL, com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD.getA());
            }
            for (NovelChapter novelChapter : StaticNovel.novelPage.novelChapters)
                if (C && !Database.DatabaseChapter.inChapters(novelChapter.link))
                    Database.DatabaseChapter.addToChapters(StaticNovel.novelURL, novelChapter);
            System.out.println(StaticNovel.novelChapters);
            if (StaticNovel.novelChapters == null)
                StaticNovel.novelChapters = new ArrayList<>();
            StaticNovel.novelChapters.addAll(StaticNovel.novelPage.novelChapters);

            Log.d("Loaded Novel:", StaticNovel.novelPage.title);
            return true;
        } catch (IOException e) {
            if (loadAll) {
                if (novelFragment != null && novelFragment.getActivity() != null)
                    novelFragment.getActivity().runOnUiThread(() -> {
                        novelFragment.errorView.setVisibility(View.VISIBLE);
                        novelFragment.errorMessage.setText(e.getMessage());
                        novelFragment.errorButton.setOnClickListener(this::refresh);
                    });
            } else if (novelFragmentMain != null && novelFragmentMain.getActivity() != null)
                novelFragmentMain.getActivity().runOnUiThread(() -> {
                    novelFragmentMain.novelFragment.errorView.setVisibility(View.VISIBLE);
                    novelFragmentMain.novelFragment.errorMessage.setText(e.getMessage());
                    novelFragmentMain.novelFragment.errorButton.setOnClickListener(this::refresh);
                });


        }
        return false;
    }

    private void refresh(View view) {
        if (StaticNovel.novelLoader != null && StaticNovel.novelLoader.isCancelled())
            StaticNovel.novelLoader.cancel(true);

        if (StaticNovel.novelLoader == null || StaticNovel.novelLoader.isCancelled())
            StaticNovel.novelLoader = new NovelLoader(novelFragmentMain, loadAll);
        StaticNovel.novelLoader.execute(activity);
    }

    /**
     * Show progress bar
     */
    @Override
    protected void onPreExecute() {
        if (loadAll) {
            assert novelFragment != null;
            novelFragment.progressBar.setVisibility(View.VISIBLE);
        } else {
            assert novelFragmentMain != null;
            novelFragmentMain.swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    protected void onCancelled() {
        C = false;
        onPostExecute(false);
    }

    /**
     * Hides progress and sets data
     *
     * @param aBoolean if completed
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (loadAll) {
            assert novelFragment != null;
            novelFragment.progressBar.setVisibility(View.GONE);
        } else {
            assert novelFragmentMain != null;
            novelFragmentMain.swipeRefreshLayout.setRefreshing(false);
            if (Database.DatabaseLibrary.inLibrary(StaticNovel.novelURL)) {
                try {
                    Database.DatabaseLibrary.updateData(StaticNovel.novelURL, StaticNovel.novelPage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (aBoolean) {
            Statics.mainActionBar.setTitle(StaticNovel.novelPage.title);
            activity.runOnUiThread(() -> {
                assert novelFragment != null;
                if (loadAll)
                    novelFragment.novelFragmentMain.setData();
                else {
                    assert novelFragmentMain != null;
                    novelFragmentMain.setData();
                }
            });
            if (loadAll) {
                if (StaticNovel.chapterLoader != null && StaticNovel.chapterLoader.isCancelled())
                    StaticNovel.chapterLoader.cancel(true);

                if ((StaticNovel.chapterLoader == null || StaticNovel.chapterLoader.isCancelled()) && novelFragment != null)
                    StaticNovel.chapterLoader = new ChapterLoader(novelFragment);
                activity.runOnUiThread(() -> StaticNovel.chapterLoader.execute(activity));
            }
        }
    }
}