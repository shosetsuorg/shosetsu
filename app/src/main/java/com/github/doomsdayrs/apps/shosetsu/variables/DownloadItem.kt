package com.github.doomsdayrs.apps.shosetsu.variables

import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification

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
 *
 *
 * These items symbolize download items and their data
 */
class DownloadItem(val formatter: Formatter, novelName: String, chapterName: String, chapterID: Int) {
    @JvmField
    val novelName: String
    @JvmField
    val chapterName: String
    @JvmField
    val chapterURL: String?
    val chapterID: Int
    /**
     * Returns the status
     *
     * @return Status
     */
    /**
     * Sets the status of the download item
     *
     * @param status status to set
     */
    //Variables only for download manager
    var status = "Pending"

    companion object {
        /**
         * Cleans up the names to be functional in file system and DB
         *
         * @param s string to clean
         * @return cleaned string
         */
        @JvmStatic
        fun cleanse(s: String): String { //Log.d("Cleaning", s);
            var s = s
            s = s.replace("'".toRegex(), "_").replace("\"".toRegex(), "_")
            // Log.d("Cleaned", s);
            return s
        }
    }

    /**
     * Constructor
     *
     * @param formatter   formatter to work with
     * @param novelName   name of the novel
     * @param chapterName name of the chapter
     * @param chapterID   ChapterID
     */
    init {
        this.novelName = cleanse(novelName)
        this.chapterName = cleanse(chapterName)
        val novelID = DatabaseIdentification.getNovelIDFromChapterID(chapterID)
        val novelURL = DatabaseIdentification.getNovelURLfromNovelID(novelID)
        chapterURL = DatabaseIdentification.getChapterURLFromChapterID(chapterID)
        this.chapterID = chapterID
    }
}