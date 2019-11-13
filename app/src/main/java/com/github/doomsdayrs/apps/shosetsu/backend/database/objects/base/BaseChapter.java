package com.github.doomsdayrs.apps.shosetsu.backend.database.objects.base;

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
 */public class BaseChapter extends Base {
    public final String CHAPTER_URL;

    protected BaseChapter(String novel_url, String chapter_url) {
        super(novel_url);
        CHAPTER_URL = chapter_url;
    }

    @Override
    public String toString() {
        return "BaseChapter{" +
                "CHAPTER_URL='" + CHAPTER_URL + '\'' +
                ", NOVEL_URL='" + NOVEL_URL + '\'' +
                '}';
    }
}
