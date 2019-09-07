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
 */public class DBDownloadItem extends BaseChapter {
    static final long serialVersionUID = 2003;


    public final int FORMATTER_ID;
    public final String NOVEL_NAME;
    public final String CHAPTER_NAME;
    public final boolean PAUSED;

    public DBDownloadItem(String novel_url, String chapter_url, int formatter_id, String novel_name, String chapter_name, boolean paused) {
        super(novel_url, chapter_url);
        FORMATTER_ID = formatter_id;
        NOVEL_NAME = novel_name;
        CHAPTER_NAME = chapter_name;
        PAUSED = paused;
    }


    @Override
    public String toString() {
        return "Download{" +
                "FORMATTER_ID=" + FORMATTER_ID +
                ", NOVEL_NAME='" + NOVEL_NAME + '\'' +
                ", CHAPTER_NAME='" + CHAPTER_NAME + '\'' +
                ", PAUSED=" + PAUSED +
                ", CHAPTER_URL='" + CHAPTER_URL + '\'' +
                ", NOVEL_URL='" + NOVEL_URL + '\'' +
                '}';
    }
}
