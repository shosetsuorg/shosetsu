package com.github.doomsdayrs.apps.shosetsu.backend.async;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;

import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapterReader;

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
    NovelFragmentChapterReader novelFragmentChapterReader;

    /**
     * Constructor
     */
    public NovelFragmentChapterViewLoad(NovelFragmentChapterReader novelFragmentChapterReader) {
        this.novelFragmentChapterReader = novelFragmentChapterReader;
    }

    @Override
    protected String doInBackground(NovelFragmentChapterReader... novelFragmentChapterReaders) {
        novelFragmentChapterReader.runOnUiThread(() -> novelFragmentChapterReader.errorView.setVisibility(View.GONE));
        try {
            novelFragmentChapterReader.unformattedText = novelFragmentChapterReader.formatter.getNovelPassage(novelFragmentChapterReader.chapterURL);
            novelFragmentChapterReader.runOnUiThread(novelFragmentChapterReader::setUpReader);
        } catch (Exception e) {
            novelFragmentChapterReader.runOnUiThread(() -> {
                novelFragmentChapterReader.errorView.setVisibility(View.VISIBLE);
                novelFragmentChapterReader.errorMessage.setText(e.getMessage());
                novelFragmentChapterReader.errorButton.setOnClickListener(view -> new NovelFragmentChapterViewLoad(novelFragmentChapterReader).execute());
            });

        }

        return null;
    }

    /**
     * Show progress before start
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (novelFragmentChapterReader != null)
            novelFragmentChapterReader.progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Once complete hide progress
     *
     * @param s null
     */
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (novelFragmentChapterReader.progressBar != null)
            novelFragmentChapterReader.progressBar.setVisibility(View.GONE);
    }
}