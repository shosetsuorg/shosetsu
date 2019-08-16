package com.github.doomsdayrs.apps.shosetsu.backend.database.objects;

import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.base.BaseChapter;

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
 * shosetsu
 * 27 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class DBChapter extends BaseChapter {
    public final String SAVED_DATA;
    public final int Y;
    public final int READ_CHAPTER;
    public final boolean BOOKMARKED;
    public final boolean IS_SAVED;
    public final String SAVE_PATH;

    public DBChapter(String novel_url, String chapter_url, String saved_data, int y, int read_chapter, boolean bookmarked, boolean is_saved, String save_path) {
        super(novel_url, chapter_url);
        SAVED_DATA = saved_data;
        Y = y;
        READ_CHAPTER = read_chapter;
        BOOKMARKED = bookmarked;
        IS_SAVED = is_saved;
        SAVE_PATH = save_path;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "SAVED_DATA='" + SAVED_DATA + '\'' +
                ", Y='" + Y + '\'' +
                ", READ_CHAPTER=" + READ_CHAPTER +
                ", BOOKMARKED=" + BOOKMARKED +
                ", IS_SAVED=" + IS_SAVED +
                ", SAVE_PATH='" + SAVE_PATH + '\'' +
                ", CHAPTER_URL='" + CHAPTER_URL + '\'' +
                ", NOVEL_URL='" + NOVEL_URL + '\'' +
                '}';
    }
}
