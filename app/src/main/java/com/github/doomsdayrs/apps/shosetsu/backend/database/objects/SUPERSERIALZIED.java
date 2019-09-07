package com.github.doomsdayrs.apps.shosetsu.backend.database.objects;

import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;

import java.io.IOException;
import java.io.Serializable;
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
 * shosetsu
 * 27 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class SUPERSERIALZIED implements Serializable {
    static final long serialVersionUID = 2006;


    public final ArrayList<DBNovel> libraries = new ArrayList<>();
    public final ArrayList<DBChapter> DBChapters = new ArrayList<>();
    public final ArrayList<DBDownloadItem> DBDownloadItems = new ArrayList<>();

    @Deprecated
    public final ArrayList<Update> updates = new ArrayList<>();
    public final SettingsSerialized settingsSerialized = new SettingsSerialized();

    @Override
    public String toString() {
        return "SUPERSERIALZIED{" +
                "libraries=" + libraries +
                ", chapters=" + DBChapters +
                ", downloads=" + DBDownloadItems +
                ", updates=" + updates +
                ", settings=" + settingsSerialized +
                '}';
    }

    public String serialize() {
        try {
            return Database.serialize(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "FUCK_UP";
    }
}
