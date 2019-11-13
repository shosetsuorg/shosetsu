package com.github.doomsdayrs.apps.shosetsu.ui.novel.async;
/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentInfo;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;

/**
 * shosetsu
 * 13 / 11 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelLoaderHolder extends AsyncTask<Activity, Void, Boolean> {
    final NovelFragment novelFragment;
    final NovelFragmentInfo novelFragmentInfo;

    public NovelLoaderHolder(NovelFragment novelFragment, NovelFragmentInfo novelFragmentInfo) {
        this.novelFragment = novelFragment;
        this.novelFragmentInfo = novelFragmentInfo;
        if ((novelFragment == null && novelFragmentInfo == null) || (novelFragment != null && novelFragmentInfo != null)) {
            Log.e("NovelLoaderHolder", "Dual used/null");
            throw new NullPointerException("Dual used/null");
        }
    }


    @Override
    protected void onPreExecute() {
        if (novelFragment != null) {
            novelFragment.progressBar.setVisibility(View.VISIBLE);
        }
        if (novelFragmentInfo != null) {
            novelFragmentInfo.swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    protected Boolean doInBackground(Activity... activities) {
        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        Activity activity = null;
        if (novelFragmentInfo != null)
            activity = novelFragmentInfo.getActivity();
        if (novelFragment != null)
            activity = novelFragment.getActivity();


        if (loadAll) {
            if (novelFragment != null) {
                novelFragment.progressBar.setVisibility(View.GONE);
            }
        } else {
            if (novelFragmentInfo != null) {
                novelFragmentInfo.swipeRefreshLayout.setRefreshing(false);
            }
            if (novelFragment != null && Database.DatabaseNovels.inDatabase(novelFragment.novelID)) {
                try {
                    Database.DatabaseNovels.updateData(novelURL, novelPage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (aBoolean) {
                Statics.mainActionBar.setTitle(novelPage.title);
                activity.runOnUiThread(() -> {
                    if (loadAll)
                        if (novelFragment != null) {
                            novelFragment.novelFragmentInfo.setData();
                        } else {
                            novelFragmentInfo.setData();
                        }
                });
                if (loadAll) {
                    activity.runOnUiThread(() -> new ChapterLoader(novelPage, novelURL, formatter).execute());
                }
            }
        }

    }
}
