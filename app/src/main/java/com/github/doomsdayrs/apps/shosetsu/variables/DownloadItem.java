package com.github.doomsdayrs.apps.shosetsu.variables;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;

/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Shosetsu is distributed in the hope that it will be useful,
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

/**
 * These items symbolize download items and their data
 */
public class DownloadItem {

    public final Formatter formatter;
    public final String novelName;
    public final String chapterName;
    public final String novelURL;
    public final String chapterURL;

    //Variables only for download manager

    private String status = "Pending";

    /**
     * Constructor
     *
     * @param formatter   formatter to work with
     * @param novelName   name of the novel
     * @param chapterName name of the chapter
     * @param novelURL    NovelURL
     * @param chapterURL  ChapterURL
     */
    public DownloadItem(Formatter formatter, String novelName, String chapterName, String novelURL, String chapterURL) {
        this.formatter = formatter;
        this.novelName = cleanse(novelName);
        this.chapterName = cleanse(chapterName);
        this.novelURL = novelURL;
        this.chapterURL = chapterURL;
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
