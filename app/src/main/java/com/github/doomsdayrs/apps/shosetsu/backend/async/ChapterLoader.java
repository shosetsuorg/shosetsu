package com.github.doomsdayrs.apps.shosetsu.backend.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;

import java.io.IOException;
import java.net.SocketTimeoutException;
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


/**
 * This task loads a novel for the novel fragment
 */
public class ChapterLoader extends AsyncTask<Activity, Void, Boolean> {
    // References
    private final NovelFragment novelFragment;
    @SuppressLint("StaticFieldLeak")
    private Activity activity;

    /**
     * Constructor
     *
     * @param novelFragment reference to the fragment
     */
    public ChapterLoader(NovelFragment novelFragment) {
        this.novelFragment = novelFragment;
    }


    /**
     * Background process
     *
     * @param voids activity to work with
     * @return if completed
     * TODO Split chapter loading off to another task
     * TODO On the other task mentioned above, load all the chapters for this novel.
     */
    @Override
    protected Boolean doInBackground(Activity... voids) {
        this.activity = voids[0];
        StaticNovel.novelPage = null;
        Log.d("ChapLoad", StaticNovel.novelURL);
        try {
            int page = Database.DatabaseLibrary.getMaxPage(StaticNovel.novelURL);

            if (StaticNovel.formatter.isIncrementingChapterList()) {
                boolean foundDif = false;

                while (!foundDif) {
                    StaticNovel.novelPage = StaticNovel.formatter.parseNovel(StaticNovel.novelURL, page);
                    int a = 0;
                    for (NovelChapter novelChapter : StaticNovel.novelPage.novelChapters)
                        if (!Database.DatabaseChapter.inChapters(novelChapter.link)) {
                            a++;
                            System.out.println("Adding #" + a + ": " + novelChapter.link);
                            StaticNovel.novelChapters.add(novelChapter);
                            Database.DatabaseChapter.addToChapters(StaticNovel.novelURL, novelChapter);
                        }

                    if (a == 0) {
                        System.out.println("Completed loading chapters");
                        foundDif = true;
                    }

                    if (a > 0) {
                        a = 0;
                        page++;
                        if (Database.DatabaseLibrary.inLibrary(StaticNovel.novelURL))
                            Database.DatabaseLibrary.setMaxPage(StaticNovel.novelURL, page);

                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return true;
        } catch (SocketTimeoutException e) {
            activity.runOnUiThread(() -> Toast.makeText(novelFragment.getContext(), "Timeout", Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Show progress bar
     */
    @Override
    protected void onPreExecute() {
        novelFragment.novelFragmentChapters.progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Hides progress and sets data
     *
     * @param aBoolean if completed
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        novelFragment.novelFragmentChapters.progressBar.setVisibility(View.GONE);
        if (aBoolean) {
            activity.runOnUiThread(() -> novelFragment.novelFragmentMain.setData());
            activity.runOnUiThread(() -> novelFragment.novelFragmentChapters.setNovels());
        }
    }
}