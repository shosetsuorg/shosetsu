package com.github.doomsdayrs.apps.shosetsu.download;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;

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
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class DeleteItem {
    public final Formatter formatter;
    public final String novelName;
    public final String chapterName;
    public final String novelURL;
    public final String chapterURL;

    public DeleteItem(Formatter formatter, String novelName, String chapterName, String novelURL, String chapterURL) {
        this.formatter = formatter;
        this.novelName = novelName;
        this.chapterName = chapterName;
        this.novelURL = novelURL;
        this.chapterURL = chapterURL;
    }
}
