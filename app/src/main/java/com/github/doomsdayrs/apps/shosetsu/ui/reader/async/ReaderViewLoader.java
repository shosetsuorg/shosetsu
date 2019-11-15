package com.github.doomsdayrs.apps.shosetsu.ui.reader.async;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;

import androidx.annotation.Nullable;

import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader;

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
 * Shosetsu
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class ReaderViewLoader extends AsyncTask<ChapterReader, Void, String> {
    /**
     * Reference to the progress bar
     */
    @SuppressLint("StaticFieldLeak")
    private final
    ChapterReader chapterReader;

    /**
     * Constructor
     */
    public ReaderViewLoader(ChapterReader chapterReader) {
        this.chapterReader = chapterReader;
    }

    @Nullable
    @Override
    protected String doInBackground(ChapterReader... chapterReaders) {
        chapterReader.runOnUiThread(() -> chapterReader.errorView.setVisibility(View.GONE));
        try {
            chapterReader.unformattedText = chapterReader.formatter.getNovelPassage(docFromURL(chapterReader.chapterURL, chapterReader.formatter.hasCloudFlare()));
            chapterReader.runOnUiThread(chapterReader::setUpReader);
            chapterReader.runOnUiThread(() ->
                    chapterReader.scrollView.post(() ->
                            chapterReader.scrollView.scrollTo(0, Database.DatabaseChapter.getY(chapterReader.chapterID)))
            );
            chapterReader.runOnUiThread(() -> chapterReader.ready = true);
        } catch (Exception e) {
            chapterReader.runOnUiThread(() -> {
                chapterReader.errorView.setVisibility(View.VISIBLE);
                chapterReader.errorMessage.setText(e.getMessage());
                chapterReader.errorButton.setOnClickListener(view -> new ReaderViewLoader(chapterReader).execute());
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