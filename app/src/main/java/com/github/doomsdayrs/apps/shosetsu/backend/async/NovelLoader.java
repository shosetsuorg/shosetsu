package com.github.doomsdayrs.apps.shosetsu.backend.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;

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
    private final NovelFragment novelFragment;
    @SuppressLint("StaticFieldLeak")
    private Activity activity;

    public NovelLoader(NovelFragment novelFragment) {
        this.novelFragment = novelFragment;
    }


    @Override
    protected Boolean doInBackground(Activity... voids) {
        this.activity = voids[0];
        StaticNovel.novelPage = null;
        Log.d("Loading", novelFragment.url);
        try {
            StaticNovel.novelPage = novelFragment.formatter.parseNovel(novelFragment.url);
            Log.d("Loaded Novel:", StaticNovel.novelPage.title);
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
    protected void onPostExecute(Boolean aBoolean) {
        novelFragment.progressBar.setVisibility(View.GONE);
        if (aBoolean) {
            Statics.mainActionBar.setTitle(StaticNovel.novelPage.title);
            activity.runOnUiThread(() -> novelFragment.novelFragmentMain.setData());
            activity.runOnUiThread(() -> novelFragment.novelFragmentChapters.setNovels());
        }
    }
}