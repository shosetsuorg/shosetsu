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
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentChapters;

import java.io.IOException;
import java.util.ArrayList;
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


/**
 * This task loads a novel for the novel fragment
 */
public class ChapterLoader extends AsyncTask<Activity, Void, Boolean> {
    // References
    private final NovelFragmentChapters novelFragmentChapters;
    private boolean C = true;

    @SuppressLint("StaticFieldLeak")
    private Activity activity;

    /**
     * Constructor
     *
     * @param novelFragment reference to the fragment
     */
    ChapterLoader(NovelFragment novelFragment) {
        novelFragmentChapters = novelFragment.novelFragmentChapters;
    }

    public ChapterLoader(NovelFragmentChapters chapters) {
        novelFragmentChapters = chapters;
    }

    public void setC(boolean c) {
        C = c;
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

        if (novelFragmentChapters != null)
            if (novelFragmentChapters.getActivity() != null)
                novelFragmentChapters.getActivity().runOnUiThread(() -> novelFragmentChapters.novelFragment.errorView.setVisibility(View.GONE));

        try {
            if (StaticNovel.novelChapters == null)
                StaticNovel.novelChapters = new ArrayList<>();

            int page = 1;
            StaticNovel.novelPage = StaticNovel.formatter.parseNovel(StaticNovel.novelURL, page);
            if (StaticNovel.formatter.isIncrementingChapterList()) {
                int mangaCount = 0;
                while (page <= StaticNovel.novelPage.maxChapterPage && C) {
                    if (novelFragmentChapters != null) {
                        String s = "Page: " + page + "/" + StaticNovel.novelPage.maxChapterPage;
                        novelFragmentChapters.pageCount.post(() -> novelFragmentChapters.pageCount.setText(s));
                    }
                    StaticNovel.novelPage = StaticNovel.formatter.parseNovel(StaticNovel.novelURL, page);
                    for (NovelChapter novelChapter : StaticNovel.novelPage.novelChapters)
                        if (C && !Database.DatabaseChapter.inChapters(novelChapter.link)) {
                            mangaCount++;
                            System.out.println("Adding #" + mangaCount + ": " + novelChapter.link);

                            StaticNovel.novelChapters.add(novelChapter);
                            Database.DatabaseChapter.addToChapters(StaticNovel.novelURL, novelChapter);
                        }
                    page++;

                    try {
                        TimeUnit.MILLISECONDS.sleep(300);
                    } catch (InterruptedException e) {
                        if (e.getMessage() != null)
                            Log.e("Error", e.getMessage());
                    }
                }
            } else {
                StaticNovel.novelPage = StaticNovel.formatter.parseNovel(StaticNovel.novelURL, page);
                int mangaCount = 0;
                for (NovelChapter novelChapter : StaticNovel.novelPage.novelChapters)
                    if (C && !Database.DatabaseChapter.inChapters(novelChapter.link)) {
                        mangaCount++;
                        System.out.println("Adding #" + mangaCount + ": " + novelChapter.link);

                        StaticNovel.novelChapters.add(novelChapter);
                        Database.DatabaseChapter.addToChapters(StaticNovel.novelURL, novelChapter);
                    }
            }
            return true;
        } catch (IOException e) {
            if (novelFragmentChapters != null)
                if (novelFragmentChapters.getActivity() != null)
                    novelFragmentChapters.getActivity().runOnUiThread(() -> {
                        novelFragmentChapters.novelFragment.errorView.setVisibility(View.VISIBLE);
                        novelFragmentChapters.novelFragment.errorMessage.setText(e.getMessage());
                        novelFragmentChapters.novelFragment.errorButton.setOnClickListener(this::refresh);
                    });

        }
        return false;
    }


    private void refresh(View view) {
        if (StaticNovel.chapterLoader != null && StaticNovel.chapterLoader.isCancelled())
            StaticNovel.chapterLoader.cancel(true);

        if (StaticNovel.chapterLoader == null || StaticNovel.chapterLoader.isCancelled())

            if (novelFragmentChapters != null && novelFragmentChapters.getActivity() != null)
                StaticNovel.chapterLoader = new ChapterLoader(novelFragmentChapters);
            else throw new NullPointerException("WHEREI SSS ITTTT");

        StaticNovel.chapterLoader.execute(activity);
    }

    /**
     * Show progress bar
     */
    @Override
    protected void onPreExecute() {

        if (novelFragmentChapters != null) {
            novelFragmentChapters.swipeRefreshLayout.setRefreshing(true);
            if (StaticNovel.formatter.isIncrementingChapterList())
                novelFragmentChapters.pageCount.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        Log.d("ChapterLoader", "Cancel");
        C = false;
        onPostExecute(false);
    }

    @Override
    protected void onCancelled() {
        Log.d("ChapterLoader", "Cancel");
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
        if (novelFragmentChapters != null) {
            novelFragmentChapters.swipeRefreshLayout.setRefreshing(false);
            if (StaticNovel.formatter.isIncrementingChapterList())
                novelFragmentChapters.pageCount.setVisibility(View.GONE);
            if (aBoolean)
                activity.runOnUiThread(novelFragmentChapters::setNovels);
            novelFragmentChapters.resumeRead.setVisibility(View.VISIBLE);
        }

    }
}