package com.github.doomsdayrs.apps.shosetsu.ui.reader.async;

import android.annotation.SuppressLint;
import android.app.Activity;
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
    ChapterView chapterView;

    /**
     * Constructor
     */
    public ReaderViewLoader(ChapterView chapterReader) {
        this.chapterView = chapterReader;
    }

    @Nullable
    @Override
    protected String doInBackground(ChapterReader... chapterReaders) {
        Activity activity = chapterView.getActivity();
        activity.runOnUiThread(() -> chapterView.errorView.errorView.setVisibility(View.GONE));
        try {
            chapterView.unformattedText = chapterView.chapterReader.formatter.getNovelPassage(docFromURL(chapterView.chapterURL, chapterView.chapterReader.formatter.getHasCloudFlare()));
            activity.runOnUiThread(chapterView::setUpReader);
            activity.runOnUiThread(() ->
                    chapterView.scrollView.post(() ->
                            chapterView.scrollView.scrollTo(0, Database.DatabaseChapter.getY(chapterView.chapterID)))
            );
            activity.runOnUiThread(() -> chapterView.ready = true);
        } catch (Exception e) {
            activity.runOnUiThread(() -> {
                chapterView.errorView.errorView.setVisibility(View.VISIBLE);
                chapterView.errorView.errorMessage.setText(e.getMessage());
                chapterView.errorView.errorButton.setOnClickListener(view -> new ReaderViewLoader(chapterView).execute());
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
        if (chapterView != null)
            chapterView.progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Once complete hide progress
     *
     * @param s null
     */
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (chapterView.progressBar != null)
            chapterView.progressBar.setVisibility(View.GONE);

        if (chapterView.chapterReader.getSupportActionBar() != null)
            chapterView.chapterReader.getSupportActionBar().setTitle(chapterView.title);
    }
}