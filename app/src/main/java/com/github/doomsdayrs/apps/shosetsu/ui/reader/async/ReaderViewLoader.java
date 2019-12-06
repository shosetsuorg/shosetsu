package com.github.doomsdayrs.apps.shosetsu.ui.reader.async;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;

import androidx.annotation.Nullable;

import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterView;

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
    ChapterView chapterReader;

    /**
     * Constructor
     */
    public ReaderViewLoader(ChapterView chapterReader) {
        this.chapterReader = chapterReader;
    }

    @Nullable
    @Override
    protected String doInBackground(ChapterReader... chapterReaders) {
        chapterReader.chapterReader.runOnUiThread(() -> chapterReader.errorView.errorView.setVisibility(View.GONE));
        try {
            chapterReader.unformattedText = chapterReader.chapterReader.formatter.getNovelPassage(docFromURL(chapterReader.chapterURL, chapterReader.chapterReader.formatter.hasCloudFlare()));
            chapterReader.chapterReader.runOnUiThread(chapterReader::setUpReader);
            chapterReader.chapterReader.runOnUiThread(() ->
                    chapterReader.scrollView.post(() ->
                            chapterReader.scrollView.scrollTo(0, Database.DatabaseChapter.getY(chapterReader.chapterID)))
            );
            chapterReader.chapterReader.runOnUiThread(() -> chapterReader.ready = true);
        } catch (Exception e) {
            chapterReader.chapterReader.runOnUiThread(() -> {
                chapterReader.errorView.errorView.setVisibility(View.VISIBLE);
                chapterReader.errorView.errorMessage.setText(e.getMessage());
                chapterReader.errorView.errorButton.setOnClickListener(view -> new ReaderViewLoader(chapterReader).execute());
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

        if (chapterReader.chapterReader.getSupportActionBar() != null)
            chapterReader.chapterReader.getSupportActionBar().setTitle(chapterReader.title);
    }
}