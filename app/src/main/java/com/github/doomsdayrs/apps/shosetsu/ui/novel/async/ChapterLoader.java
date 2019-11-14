package com.github.doomsdayrs.apps.shosetsu.ui.novel.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentChapters;

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
 */
public class ChapterLoader extends AsyncTask<Activity, Void, Boolean> {
    private NovelPage novelPage;
    private final String novelURL;
    private final Formatter formatter;

    private NovelFragmentChapters novelFragmentChapters;

    /**
     * Constructor
     */
    public ChapterLoader(NovelPage novelPage, String novelURL, Formatter formatter) {
        this.novelPage = novelPage;
        this.novelURL = novelURL;
        this.formatter = formatter;
    }

    private ChapterLoader(ChapterLoader chapterLoader) {
        this.novelPage = chapterLoader.novelPage;
        this.novelURL = chapterLoader.novelURL;
        this.formatter = chapterLoader.formatter;
        this.novelFragmentChapters = chapterLoader.novelFragmentChapters;
    }

    public ChapterLoader setNovelFragmentChapters(NovelFragmentChapters novelFragmentChapters) {
        this.novelFragmentChapters = novelFragmentChapters;
        return this;
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
        novelPage = null;
        Log.d("ChapLoad", novelURL);

        if (novelFragmentChapters != null) {
            if (novelFragmentChapters.getActivity() != null)
                novelFragmentChapters.getActivity().runOnUiThread(() -> novelFragmentChapters.novelFragment.errorView.setVisibility(View.GONE));

            try {

                int page = 1;
                if (formatter.isIncrementingChapterList()) {
                    novelPage = formatter.parseNovel(docFromURL(novelURL, formatter.hasCloudFlare()), page);
                    int mangaCount = 0;
                    while (page <= novelPage.maxChapterPage && !activity.isDestroyed()) {
                        String s = "Page: " + page + "/" + novelPage.maxChapterPage;
                        novelFragmentChapters.pageCount.post(() -> novelFragmentChapters.pageCount.setText(s));

                        novelPage = formatter.parseNovel(docFromURL(novelURL, formatter.hasCloudFlare()), page);
                        for (NovelChapter novelChapter : novelPage.novelChapters)
                            add(activity, mangaCount, novelChapter);
                        page++;

                        Utilities.wait(300);
                    }
                } else {
                    novelPage = formatter.parseNovel(docFromURL(novelURL, formatter.hasCloudFlare()), page);
                    int mangaCount = 0;
                    for (NovelChapter novelChapter : novelPage.novelChapters)
                        add(activity, mangaCount, novelChapter);
                }
                return true;
            } catch (Exception e) {
                if (novelFragmentChapters != null)
                    if (novelFragmentChapters.getActivity() != null)
                        novelFragmentChapters.getActivity().runOnUiThread(() -> {
                            novelFragmentChapters.novelFragment.errorView.setVisibility(View.VISIBLE);
                            novelFragmentChapters.novelFragment.errorMessage.setText(e.getMessage());
                            novelFragmentChapters.novelFragment.errorButton.setOnClickListener(view -> refresh(view, activity));
                        });

            }
        }
        return false;
    }

    private void add(Activity activity, int mangaCount, NovelChapter novelChapter) {
        //TODO The getNovelID in this method likely will cause slowdowns due to IO
        if (!activity.isDestroyed() && !Database.DatabaseChapter.inChapters(novelChapter.link)) {
            mangaCount++;
            System.out.println("Adding #" + mangaCount + ": " + novelChapter.link);
            Database.DatabaseChapter.addToChapters(getNovelIDFromNovelURL(novelURL), novelChapter);
        }
    }

    private void refresh(View view, Activity activity) {
        new ChapterLoader(this).execute(activity);
    }

    /**
     * Show progress bar
     */
    @Override
    protected void onPreExecute() {
        assert novelFragmentChapters != null;
        novelFragmentChapters.swipeRefreshLayout.setRefreshing(true);
        if (formatter.isIncrementingChapterList())
            novelFragmentChapters.pageCount.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        Log.d("ChapterLoader", "Cancel");
        onPostExecute(false);
    }

    @Override
    protected void onCancelled() {
        Log.d("ChapterLoader", "Cancel");
        onPostExecute(false);
    }

    /**
     * Hides progress and sets data
     *
     * @param result if completed
     */
    @Override
    protected void onPostExecute(Boolean result) {
        assert novelFragmentChapters != null;
        assert novelFragmentChapters.novelFragment.getView() != null;

        novelFragmentChapters.novelFragment.getView().post(() -> {
            novelFragmentChapters.swipeRefreshLayout.setRefreshing(false);
            if (formatter.isIncrementingChapterList())
                novelFragmentChapters.pageCount.setVisibility(View.GONE);
            if (result)
                if (novelFragmentChapters.getActivity() != null)
                    novelFragmentChapters.getActivity().runOnUiThread(novelFragmentChapters::setNovels);
            novelFragmentChapters.resumeRead.setVisibility(View.VISIBLE);
        });
    }
}