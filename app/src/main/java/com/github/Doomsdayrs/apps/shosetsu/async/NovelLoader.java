package com.github.Doomsdayrs.apps.shosetsu.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.Doomsdayrs.apps.shosetsu.fragment.novel.NovelFragment;

import java.io.IOException;
import java.net.SocketTimeoutException;

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
public class NovelLoader extends AsyncTask<Activity, Void, Boolean> {
    private NovelFragment novelFragment;
    private Activity activity;

    public NovelLoader(NovelFragment novelFragment) {
        this.novelFragment = novelFragment;
    }


    @Override
    protected Boolean doInBackground(Activity... voids) {
        this.activity = voids[0];

        Log.d("Loading", "Novel");
        try {
            novelFragment.novelPage = novelFragment.formatter.parseNovel(novelFragment.url);
            novelFragment.novelFragmentMain.novelPage = novelFragment.novelPage;
            Log.d("Loaded Novel:", novelFragment.novelPage.title);

            return true;
        } catch (SocketTimeoutException e) {
            activity.runOnUiThread(() -> Toast.makeText(novelFragment.getContext(), "Timeout", Toast.LENGTH_SHORT).show());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPreExecute() {
        novelFragment.progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        novelFragment.progressBar.setVisibility(View.GONE);
        if (aBoolean) {
            activity.runOnUiThread(() -> novelFragment.novelFragmentMain.setData());
            activity.runOnUiThread(() -> novelFragment.novelFragmentChapters.setNovels(novelFragment.novelFragmentChapters.novelChapters));
        }
    }
}