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
public class Update extends BaseChapter {
    private final long time;
    public final int chapterID;
    private final int novelID;

    public Update(String novel_url, String chapter_url, long time, int chapterID, int novelID) {
        super(novel_url, chapter_url);
        this.time = time;
        this.chapterID = chapterID;
        this.novelID = novelID;
    }
}
