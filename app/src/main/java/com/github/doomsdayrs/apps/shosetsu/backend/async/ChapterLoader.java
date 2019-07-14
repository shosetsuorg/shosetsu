package com.github.doomsdayrs.apps.shosetsu.backend.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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


/**
 * This task loads a novel for the novel fragment
 */
public class ChapterLoader extends AsyncTask<Activity, Void, Boolean> {
    // References
    private final NovelFragment novelFragment;
    private final NovelFragmentChapters novelFragmentChapters;

    @SuppressLint("StaticFieldLeak")
    private Activity activity;

    /**
     * Constructor
     *
     * @param novelFragment reference to the fragment
     */
    public ChapterLoader(NovelFragment novelFragment) {
        this.novelFragment = novelFragment;
        novelFragmentChapters = null;
    }

    public ChapterLoader(NovelFragmentChapters chapters) {
        novelFragment = null;
        novelFragmentChapters = chapters;
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
        Log.d("ChapLoad", StaticNovel.novelURL);
        if (novelFragment != null) {
            if (novelFragment.getActivity() != null)
                novelFragment.getActivity().runOnUiThread(() -> novelFragment.errorView.setVisibility(View.GONE));
        } else if (novelFragmentChapters != null)
            if (novelFragmentChapters.getActivity() != null)
                novelFragmentChapters.getActivity().runOnUiThread(() -> novelFragmentChapters.novelFragment.errorView.setVisibility(View.GONE));

        try {
            if (StaticNovel.novelChapters == null)
                StaticNovel.novelChapters = new ArrayList<>();

            int page = 1;
            StaticNovel.novelPage = StaticNovel.formatter.parseNovel(StaticNovel.novelURL, page);
            if (StaticNovel.formatter.isIncrementingChapterList()) {
                int mangaCount = 0;

                while (page <= StaticNovel.novelPage.maxChapterPage) {
                    StaticNovel.novelPage = StaticNovel.formatter.parseNovel(StaticNovel.novelURL, page);
                    for (NovelChapter novelChapter : StaticNovel.novelPage.novelChapters)
                        if (!Database.DatabaseChapter.inChapters(novelChapter.link)) {
                            mangaCount++;
                            System.out.println("Adding #" + mangaCount + ": " + novelChapter.link);

                            StaticNovel.novelChapters.add(novelChapter);
                            Database.DatabaseChapter.addToChapters(StaticNovel.novelURL, novelChapter);
                        }
                    page++;

                    try {
                        TimeUnit.MILLISECONDS.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        } catch (IOException e) {
            if (novelFragment != null) {
                if (novelFragment.getActivity() != null)
                    novelFragment.getActivity().runOnUiThread(() -> {
                        novelFragment.errorView.setVisibility(View.VISIBLE);
                        novelFragment.errorMessage.setText(e.getMessage());
                        novelFragment.errorButton.setOnClickListener(view -> new ChapterLoader(novelFragment).execute(voids));
                    });
            } else if (novelFragmentChapters != null)
                if (novelFragmentChapters.getActivity() != null)
                    novelFragmentChapters.getActivity().runOnUiThread(() -> {
                        novelFragmentChapters.novelFragment.errorView.setVisibility(View.VISIBLE);
                        novelFragmentChapters.novelFragment.errorMessage.setText(e.getMessage());
                        novelFragmentChapters.novelFragment.errorButton.setOnClickListener(view -> new ChapterLoader(novelFragmentChapters).execute(voids));
                    });

        }
        return false;
    }

    /**
     * Show progress bar
     */
    @Override
    protected void onPreExecute() {
        if (novelFragment != null)
            novelFragment.novelFragmentChapters.progressBar.setVisibility(View.VISIBLE);
        else {
            assert novelFragmentChapters != null;
            novelFragmentChapters.swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    protected void onCancelled() {
        if (novelFragment != null)
            novelFragment.novelFragmentChapters.progressBar.setVisibility(View.GONE);
        else {
            assert novelFragmentChapters != null;
            novelFragmentChapters.swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Hides progress and sets data
     *
     * @param aBoolean if completed
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (novelFragment != null)
            novelFragment.novelFragmentChapters.progressBar.setVisibility(View.GONE);
        else {
            assert novelFragmentChapters != null;
            novelFragmentChapters.swipeRefreshLayout.setRefreshing(false);
        }
        if (aBoolean) {
            if (novelFragment != null) {
                activity.runOnUiThread(() -> novelFragment.novelFragmentMain.setData());
                activity.runOnUiThread(() -> novelFragment.novelFragmentChapters.setNovels());
            } else
                activity.runOnUiThread(novelFragmentChapters::setNovels);


        }
    }
}