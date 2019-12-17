package com.github.doomsdayrs.apps.shosetsu.backend;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.doomsdayrs.apps.shosetsu.ui.updates.async.ChapterUpdater;

import java.util.ArrayList;

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
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class Update_Manager {
    @Nullable
    private static ChapterUpdater chapterUpdater = null;

    public static void init(@NonNull ArrayList<Integer> novelCards, @NonNull Context context) {
        if (chapterUpdater == null) {
            chapterUpdater = new ChapterUpdater(novelCards, context);
            chapterUpdater.execute();
        } else {
            if (chapterUpdater.isCancelled() || chapterUpdater.getStatus().equals(AsyncTask.Status.FINISHED)) {
                chapterUpdater = new ChapterUpdater(novelCards, context);
                chapterUpdater.execute();
            }
            if (chapterUpdater.getStatus().equals(AsyncTask.Status.PENDING))
                chapterUpdater.execute();
        }
    }
}
