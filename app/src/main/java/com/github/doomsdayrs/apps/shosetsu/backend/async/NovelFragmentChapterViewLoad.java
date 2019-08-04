package com.github.doomsdayrs.apps.shosetsu.backend.async;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;

import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.ChapterReader;

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
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelFragmentChapterViewLoad extends AsyncTask<ChapterReader, Void, String> {
    /**
     * Reference to the progress bar
     */
    @SuppressLint("StaticFieldLeak")
    private final
    ChapterReader chapterReader;

    /**
     * Constructor
     */
    public NovelFragmentChapterViewLoad(ChapterReader chapterReader) {
        this.chapterReader = chapterReader;
    }

    @Override
    protected String doInBackground(ChapterReader... chapterReaders) {
        chapterReader.runOnUiThread(() -> chapterReader.errorView.setVisibility(View.GONE));
        try {
            chapterReader.unformattedText = chapterReader.formatter.getNovelPassage(chapterReader.chapterURL);
            chapterReader.runOnUiThread(chapterReader::setUpReader);
            chapterReader.runOnUiThread(() ->
                    chapterReader.scrollView.post(() ->
                            chapterReader.scrollView.scrollTo(0, Database.DatabaseChapter.getY(chapterReader.chapterURL)))
            );
            chapterReader.runOnUiThread(() -> chapterReader.ready = true);
        } catch (Exception e) {
            chapterReader.runOnUiThread(() -> {
                chapterReader.errorView.setVisibility(View.VISIBLE);
                chapterReader.errorMessage.setText(e.getMessage());
                chapterReader.errorButton.setOnClickListener(view -> new NovelFragmentChapterViewLoad(chapterReader).execute());
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
        if (chapterReader != null)
            chapterReader.progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Once complete hide progress
     *
     * @param s null
     */
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (chapterReader.progressBar != null)
            chapterReader.progressBar.setVisibility(View.GONE);

        if (chapterReader.getSupportActionBar() != null)
            chapterReader.getSupportActionBar().setTitle(chapterReader.title);
    }
}