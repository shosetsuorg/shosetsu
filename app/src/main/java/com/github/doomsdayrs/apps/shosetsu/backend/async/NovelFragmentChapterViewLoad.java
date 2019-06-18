package com.github.doomsdayrs.apps.shosetsu.backend.async;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapterView;

import java.io.IOException;

/**
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
public class NovelFragmentChapterViewLoad extends AsyncTask<NovelFragmentChapterView, Void, String> {
    @SuppressLint("StaticFieldLeak")
    ProgressBar progressBar;

    public NovelFragmentChapterViewLoad(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    protected String doInBackground(NovelFragmentChapterView... novelFragmentChapterViews) {
        try {
            novelFragmentChapterViews[0].text = novelFragmentChapterViews[0].formatter.getNovelPassage(novelFragmentChapterViews[0].URL).replaceAll("\n", "\n\n");
            novelFragmentChapterViews[0].runOnUiThread(() -> novelFragmentChapterViews[0].textView.setText(novelFragmentChapterViews[0].text));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(View.GONE);
    }
}