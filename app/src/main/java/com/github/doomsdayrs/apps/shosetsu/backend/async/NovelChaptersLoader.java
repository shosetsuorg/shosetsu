package com.github.doomsdayrs.apps.shosetsu.backend.async;

import android.os.AsyncTask;
import android.view.View;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapters;

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
 * 17 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelChaptersLoader extends AsyncTask<Integer, Void, Boolean> {
    private final NovelFragmentChapters novelFragmentChapters;

    public NovelChaptersLoader(NovelFragmentChapters novelFragmentChapters) {
        this.novelFragmentChapters = novelFragmentChapters;
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {
        if (novelFragmentChapters.formatter.isIncrementingChapterList())
            try {
                NovelPage novelPage;
                if (integers.length == 0)
                    novelPage = novelFragmentChapters.formatter.parseNovel(novelFragmentChapters.novelURL);
                else
                    novelPage = novelFragmentChapters.formatter.parseNovel(novelFragmentChapters.novelURL, integers[0]);
                //TODO Difference calculation

                if (!novelPage.novelChapters.get(novelPage.novelChapters.size() - 1).link
                        .equals(NovelFragmentChapters.novelChapters.get(NovelFragmentChapters.novelChapters.size() - 1).link))
                    NovelFragmentChapters.novelChapters.addAll(novelPage.novelChapters);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        return false;
    }

    @Override
    protected void onCancelled() {
        novelFragmentChapters.progressBar.setVisibility(View.GONE);
        super.onCancelled();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        novelFragmentChapters.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        novelFragmentChapters.progressBar.setVisibility(View.GONE);
        if (aBoolean)
            novelFragmentChapters.recyclerView.post(() -> novelFragmentChapters.adapter.notifyDataSetChanged());
    }
}