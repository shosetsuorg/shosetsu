package com.github.doomsdayrs.apps.shosetsu.backend.async;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapterReader;

import java.io.IOException;
import java.net.SocketTimeoutException;

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
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelFragmentChapterViewLoad extends AsyncTask<NovelFragmentChapterReader, Void, String> {
    /**
     * Reference to the progress bar
     */
    @SuppressLint("StaticFieldLeak")
    private final
    ProgressBar progressBar;

    /**
     * Constructor
     *
     * @param progressBar progress bar to change
     */
    public NovelFragmentChapterViewLoad(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    protected String doInBackground(NovelFragmentChapterReader... novelFragmentChapterReaders) {
        try {
            novelFragmentChapterReaders[0].unformattedText = novelFragmentChapterReaders[0].formatter.getNovelPassage(novelFragmentChapterReaders[0].chapterURL);
            novelFragmentChapterReaders[0].runOnUiThread(() -> novelFragmentChapterReaders[0].setUpReader());
        } catch (SocketTimeoutException ignored) {
            // TODO Add error management here
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Show progress before start
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Once complete hide progress
     *
     * @param s null
     */
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
    }
}