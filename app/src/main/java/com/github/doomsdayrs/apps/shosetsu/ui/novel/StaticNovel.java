package com.github.doomsdayrs.apps.shosetsu.ui.novel;

import android.annotation.SuppressLint;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.async.ChapterLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.async.NovelLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
public class StaticNovel {
    //TODO fix error with chapters here
    /**
     * Global variable of the current loaded novel
     */
    public static String novelURL;
    public static NovelPage novelPage;

    public static List<NovelChapter> novelChapters = new ArrayList<>();
    public static Formatter formatter;
    public static Status status = Status.UNREAD;
    @SuppressLint("StaticFieldLeak")
    public static NovelLoader novelLoader = null;
    @SuppressLint("StaticFieldLeak")
    public static ChapterLoader chapterLoader = null;

    /**
     * @param chapterURL Current chapter URL
     * @return chapter after the input, returns the current chapter if no more
     */
    public static NovelChapter getNextChapter(@NotNull String chapterURL) {
        if (novelChapters != null && novelChapters.size() != 0)
            for (int x = 0; x < novelChapters.size(); x++) {
                if (novelChapters.get(x).link.equalsIgnoreCase(chapterURL)) {

                    if (NovelFragmentChapters.reversed) {
                        if (x - 1 != -1)
                            return novelChapters.get(x - 1);
                        else return novelChapters.get(x);
                    } else {
                        if (x + 1 != novelChapters.size())
                            return novelChapters.get(x + 1);
                        else return novelChapters.get(x);
                    }
                }
            }
        return null;
    }

    /**
     * @return position of last read chapter, reads array from reverse. If -1 then the array is null, if -2 the array is empty, else if not found plausible chapter returns the first.
     */
    public static int lastRead() {
        if (StaticNovel.novelChapters != null) {
            if (StaticNovel.novelChapters.size() != 0) {
                if (!NovelFragmentChapters.reversed) {
                    for (int x = novelChapters.size() - 1; x >= 0; x--) {
                        Status status = Database.DatabaseChapter.getStatus(novelChapters.get(x).link);
                        switch (status) {
                            default:
                                break;
                            case READ:
                                return x + 1;
                            case READING:
                                return x;
                        }
                    }
                } else {
                    for (int x = 0; x < novelChapters.size(); x++) {
                        Status status = Database.DatabaseChapter.getStatus(novelChapters.get(x).link);
                        switch (status) {
                            default:
                                break;
                            case READ:
                                return x - 1;
                            case READING:
                                return x;
                        }
                    }
                }
                return 0;
            } else return -2;
        } else return -1;
    }


    public static void destroy() {
        formatter = null;
        novelChapters = null;
        novelURL = null;
        novelPage = null;

        if (novelLoader != null) {
            if (!novelLoader.isCancelled()) {
                novelLoader.setC(false);
                novelLoader.cancel(true);
            }
            novelLoader = null;
        }
        if (novelLoader != null) {
            if (!novelLoader.isCancelled()) {
                novelLoader.setC(false);
                novelLoader.cancel(true);
            }
            novelLoader = null;
        }

        if (chapterLoader != null) {
            if (!chapterLoader.isCancelled()) {
                chapterLoader.setC(false);
                chapterLoader.cancel(true);
            }
            chapterLoader = null;
        }
        status = Status.UNREAD;
    }
}
