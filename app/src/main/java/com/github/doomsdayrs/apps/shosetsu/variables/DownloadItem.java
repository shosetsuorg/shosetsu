package com.github.doomsdayrs.apps.shosetsu.variables;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getChapterURLFromChapterID;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getNovelIDFromChapterID;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getNovelURLfromNovelID;

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
 */

/**
 * Shosetsu
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 * <p>
 * These items symbolize download items and their data
 */
public class DownloadItem {

    public final Formatter formatter;
    @NonNull
    public final String novelName;
    @NonNull
    public final String chapterName;
    @Nullable
    private final String novelURL;
    @Nullable
    public final String chapterURL;
    private final int novelID;
    public final int chapterID;

    //Variables only for download manager

    private String status = "Pending";

    /**
     * Constructor
     *
     * @param formatter   formatter to work with
     * @param novelName   name of the novel
     * @param chapterName name of the chapter
     * @param chapterID   ChapterID
     */
    public DownloadItem(Formatter formatter, String novelName, String chapterName, int chapterID) {
        this.formatter = formatter;
        this.novelName = cleanse(novelName);
        this.chapterName = cleanse(chapterName);
        this.novelID = getNovelIDFromChapterID(chapterID);
        this.novelURL = getNovelURLfromNovelID(novelID);
        this.chapterURL = getChapterURLFromChapterID(chapterID);
        this.chapterID = chapterID;
    }

    /**
     * Returns the status
     *
     * @return Status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the download item
     *
     * @param status status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }


    /**
     * Cleans up the names to be functional in file system and DB
     *
     * @param s string to clean
     * @return cleaned string
     */
    public static String cleanse(String s) {
        //Log.d("Cleaning", s);
        s = s.replaceAll("'", "_").replaceAll("\"", "_");
        // Log.d("Cleaned", s);
        return s;
    }
}
