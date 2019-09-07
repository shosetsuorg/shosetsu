package com.github.doomsdayrs.apps.shosetsu.backend.database.objects;

import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.base.Base;

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
public class DBNovel extends Base {
    static final long serialVersionUID = 2004;


    public final boolean BOOKMARKED;

    /**
     * Serialized NovelPage object in string form, must be deserialized for use
     */
    public final String NOVEL_PAGE;

    public final int FORMATTER_ID;
    public final int STATUS;

    public DBNovel(String novel_url, boolean bookmarked, String novel_page, int formatter_id, int status) {
        super(novel_url);
        BOOKMARKED = bookmarked;
        NOVEL_PAGE = novel_page;
        FORMATTER_ID = formatter_id;
        STATUS = status;
    }

    @Override
    public String toString() {
        return "Library{" +
                "BOOKMARKED=" + BOOKMARKED +
                ", NOVEL_PAGE='" + NOVEL_PAGE + '\'' +
                ", FORMATTER_ID=" + FORMATTER_ID +
                ", STATUS=" + STATUS +
                ", NOVEL_URL='" + NOVEL_URL + '\'' +
                '}';
    }
}
