package com.github.doomsdayrs.apps.shosetsu.backend.async;

import android.os.AsyncTask;
import android.view.View;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapters;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Foobar is distributed in the hope that it will be useful,
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
public class NovelChaptersLoader extends AsyncTask<Integer, Void, Boolean> {
    // Reference
    private final NovelFragmentChapters novelFragmentChapters;

    /**
     * Constructor
     *
     * @param novelFragmentChapters reference
     */
    public NovelChaptersLoader(NovelFragmentChapters novelFragmentChapters) {
        this.novelFragmentChapters = novelFragmentChapters;
    }

    /**
     * Loads chapters of a novel
     *
     * @param integers position 0 is the page to load
     * @return if completed
     */
    @Override
    protected Boolean doInBackground(Integer... integers) {
        if (novelFragmentChapters.formatter.isIncrementingChapterList())
            try {
                NovelPage novelPage;
                if (integers.length == 0)
                    novelPage = novelFragmentChapters.formatter.parseNovel(novelFragmentChapters.novelURL);
                else
                    novelPage = novelFragmentChapters.formatter.parseNovel(novelFragmentChapters.novelURL, integers[0]);
                boolean foundDif = false;
                int increment = 1;

                // Loops till there is a difference (this is for the fact that chapters are loaded if there
                while (!foundDif) {
                    for (NovelChapter novelChapter : novelPage.novelChapters) {
                        if (!Database.DatabaseChapter.inChapters(novelChapter.link)) {
                            NovelFragmentChapters.novelChapters.add(novelChapter);
                            foundDif = true;
                        }
                    }
                    if (!foundDif) {
                        TimeUnit.MILLISECONDS.sleep(100);
                        novelPage = novelFragmentChapters.formatter.parseNovel(novelFragmentChapters.novelURL, integers[0] + increment);
                        novelFragmentChapters.currentMaxPage++;
                        increment++;
                    }
                }

                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        return false;
    }

    /**
     * Ends progress bar
     */
    @Override
    protected void onCancelled() {
        novelFragmentChapters.progressBar.setVisibility(View.GONE);
        super.onCancelled();
    }

    /**
     * Starts the loading action
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        novelFragmentChapters.progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Once done remove progress bar
     *
     * @param aBoolean result of doInBackground
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        novelFragmentChapters.progressBar.setVisibility(View.GONE);
        if (aBoolean)
            if (NovelFragmentChapters.recyclerView != null)
                if (NovelFragmentChapters.adapter != null)
                    NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
    }
}