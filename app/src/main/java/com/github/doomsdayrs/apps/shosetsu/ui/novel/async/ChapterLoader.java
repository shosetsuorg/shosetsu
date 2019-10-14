package com.github.doomsdayrs.apps.shosetsu.ui.novel.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentChapters;

import java.io.IOException;
import java.util.ArrayList;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getNovelIDFromNovelURL;

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
            if (StaticNovel.formatter.isIncrementingChapterList()) {
                StaticNovel.novelPage = StaticNovel.formatter.parseNovel(StaticNovel.novelURL, page);
                int mangaCount = 0;
                while (page <= StaticNovel.novelPage.maxChapterPage && C) {
                    if (novelFragmentChapters != null) {
                        String s = "Page: " + page + "/" + StaticNovel.novelPage.maxChapterPage;
                        novelFragmentChapters.pageCount.post(() -> novelFragmentChapters.pageCount.setText(s));
                    }
                    StaticNovel.novelPage = StaticNovel.formatter.parseNovel(StaticNovel.novelURL, page);
                    for (NovelChapter novelChapter : StaticNovel.novelPage.novelChapters)
                        add(mangaCount, novelChapter);
                    page++;

                    Utilities.wait(300);
                }
            } else {
                StaticNovel.novelPage = StaticNovel.formatter.parseNovel(StaticNovel.novelURL, page);
                int mangaCount = 0;
                for (NovelChapter novelChapter : StaticNovel.novelPage.novelChapters)
                    add(mangaCount, novelChapter);
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

    private void add(int mangaCount, NovelChapter novelChapter) {
        //TODO The getNovelID in this method likely will cause slowdowns due to IO
        if (C && !Database.DatabaseChapter.inChapters(novelChapter.link)) {
            mangaCount++;
            System.out.println("Adding #" + mangaCount + ": " + novelChapter.link);
            StaticNovel.novelChapters.add(novelChapter);
            Database.DatabaseChapter.addToChapters(getNovelIDFromNovelURL(StaticNovel.novelURL), novelChapter);
        }
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