package com.github.doomsdayrs.apps.shosetsu.backend.database

import android.app.Activity
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import android.util.Log
import com.github.doomsdayrs.api.shosetsu.services.core.LuaFormatter
import com.github.doomsdayrs.api.shosetsu.services.core.Novel
import com.github.doomsdayrs.api.shosetsu.services.core.Novel.Chapter
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils
import com.github.doomsdayrs.apps.shosetsu.backend.database.Columns.*
import com.github.doomsdayrs.apps.shosetsu.ui.errorView.ErrorAlert
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem
import com.github.doomsdayrs.apps.shosetsu.variables.Update
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status
import com.github.doomsdayrs.apps.shosetsu.variables.ext.*
import com.github.doomsdayrs.apps.shosetsu.variables.obj.DefaultScrapers.getByID
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard
import org.joda.time.DateTime
import org.joda.time.Days
import java.io.File
import java.util.*

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
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
// TODO cache clearing
//  >Library, remove all where bookmark = 0
//  >Chapters, remove all that are not from a bookmarked library
object Database {
    private const val LOG_IDEN = "DatabaseID"
    private const val LOG_DOWN = "DatabaseDown"
    private const val LOG_NOVE = "DatabaseNovels"
    private const val LOG_CHAP = "DatabaseChapters"
    private const val LOG_UPDA = "DatabaseUpdates"
    private const val LOG_FORM = "DatabaseFormatters"

    /**
     * SQLITEDatabase
     */
    var sqLiteDatabase: SQLiteDatabase? = null

    object DatabaseIdentification {
        /**
         * Gets rid of novel completely
         *
         * @param novelID ID of novel to destroy
         */
        private fun purgeNovel(novelID: Int) {
            sqLiteDatabase!!.execSQL("delete from " + Tables.NOVEL_IDENTIFICATION + " where " + ID + "=" + novelID)
            sqLiteDatabase!!.execSQL("delete from " + Tables.NOVELS + " where " + PARENT_ID + "=" + novelID)
            purgeChaptersOf(novelID)
        }

        /**
         * Gets rid of chapters of novel. To fix issues
         *
         * @param novelID ID of novel
         */
        private fun purgeChaptersOf(novelID: Int) {
            // Deletes chapters from identification
            sqLiteDatabase!!.execSQL("delete from " + Tables.CHAPTER_IDENTIFICATION + " where " + PARENT_ID + "=" + novelID)

            // Removes all chapters from chapters DB
            sqLiteDatabase!!.execSQL("delete from " + Tables.CHAPTERS + " where " + PARENT_ID + "=" + novelID)

            // Removes chapters from updates
            sqLiteDatabase!!.execSQL("delete from " + Tables.UPDATES + " where " + PARENT_ID + "=" + novelID)
        }

        /**
         * Finds and deletes all novels that are unbookmarked
         */
        fun purgeUnSavedNovels() {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + PARENT_ID + " from " + Tables.NOVELS + " where " + BOOKMARKED + "=0", null)
            while (cursor.moveToNext()) {
                val i = cursor.getInt(cursor.getColumnIndex(PARENT_ID.toString()))
                Log.d(LOG_IDEN, "Removing novel $i")
                purgeNovel(i)
            }
            cursor.close()
        }

        fun hasChapter(chapterURL: String): Boolean {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + ID + " from " + Tables.CHAPTER_IDENTIFICATION + " where " + URL + " = '" + chapterURL + "'", null)
            val a = cursor.count
            cursor.close()
            return a > 0
        }

        fun addChapter(novelID: Int, chapterURL: String) {
            try {
                sqLiteDatabase!!.execSQL("insert into " + Tables.CHAPTER_IDENTIFICATION + "(" +
                        PARENT_ID + "," +
                        URL +
                        ")" +
                        "values" +
                        "('" +
                        novelID + "','" +
                        chapterURL +
                        "')")
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }

        fun addNovel(novelURL: String, formatter: Int) {
            try {
                sqLiteDatabase!!.execSQL("insert into " + Tables.NOVEL_IDENTIFICATION + "('" +
                        URL + "'," +
                        FORMATTER_ID +
                        ")" +
                        "values" +
                        "('" +
                        novelURL +
                        "'," +
                        formatter +
                        ")")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * @param url NovelURL
         * @return NovelID
         */
        fun getNovelIDFromNovelURL(url: String): Int {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + ID + " from " + Tables.NOVEL_IDENTIFICATION + " where " + URL + " ='" + url + "'", null)
            return if (cursor.count <= 0) {
                cursor.close()
                -1
            } else {
                cursor.moveToNext()
                val id = cursor.getInt(cursor.getColumnIndex(ID.toString()))
                cursor.close()
                id
            }
        }

        /**
         * @param url ChapterURL
         * @return ChapterID
         */
        fun getChapterIDFromChapterURL(url: String): Int {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + ID + " from " + Tables.CHAPTER_IDENTIFICATION + " where " + URL + " = '" + url + "'", null)
            return if (cursor.count <= 0) {
                cursor.close()
                0
            } else {
                cursor.moveToNext()
                val id = cursor.getInt(cursor.getColumnIndex(ID.toString()))
                cursor.close()
                id
            }
        }

        /**
         * @param id ChapterID
         * @return ChapterURL
         */
        fun getChapterURLFromChapterID(id: Int): String {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + URL + " from " + Tables.CHAPTER_IDENTIFICATION + " where " + ID + " = " + id + "", null)
            if (cursor.count <= 0) {
                cursor.close()
            } else {
                cursor.moveToNext()
                val url = cursor.getString(cursor.getColumnIndex(URL.toString()))
                cursor.close()
                return url
            }
            return ""
        }

        /**
         * @param id ChapterID
         * @return NovelID
         */
        fun getNovelIDFromChapterID(id: Int): Int {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + PARENT_ID + " from " + Tables.CHAPTER_IDENTIFICATION + " where " + ID + " = " + id + "", null)
            return if (cursor.count <= 0) {
                cursor.close()
                0
            } else {
                cursor.moveToNext()
                val parent = cursor.getInt(cursor.getColumnIndex(PARENT_ID.toString()))
                cursor.close()
                parent
            }
        }

        /**
         * @param id Chapter ID
         * @return Chapter URL
         */
        private fun getNovelURLFromChapterID(id: Int): String? {
            return getNovelURLfromNovelID(getNovelIDFromChapterID(id))
        }

        /**
         * @param url Chapter url
         * @return Novel URL
         */
        fun getNovelURLFromChapterURL(url: String): String? {
            return getNovelURLFromChapterID(getChapterIDFromChapterURL(url))
        }

        /**
         * @param id NovelID
         * @return NovelURL
         */
        fun getNovelURLfromNovelID(id: Int): String? {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + URL + " from " + Tables.NOVEL_IDENTIFICATION + " where " + ID + " = " + id + "", null)
            if (cursor.count <= 0) {
                cursor.close()
            } else {
                cursor.moveToNext()
                val url = cursor.getString(cursor.getColumnIndex(URL.toString()))
                cursor.close()
                return url
            }
            return null
        }

        /**
         * Returns Formatter ID via Novel ID
         *
         * @param novelID Novel ID
         * @return Formatter ID
         */
        fun getFormatterIDFromNovelID(novelID: Int): Int {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + FORMATTER_ID + " from " + Tables.NOVEL_IDENTIFICATION + " where " + ID + " = " + novelID + "", null)
            if (cursor.count <= 0) {
                cursor.close()
            } else {
                cursor.moveToNext()
                val id = cursor.getInt(cursor.getColumnIndex(FORMATTER_ID.toString()))
                cursor.close()
                return id
            }
            return -1
        }

        /**
         * Returns Formatter ID via Novel URL
         *
         * @param url Novel URL
         * @return Formatter ID
         */
        fun getFormatterIDFromNovelURL(url: String): Int {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + FORMATTER_ID + " from " + Tables.NOVEL_IDENTIFICATION + " where " + URL + " = '" + url + "'", null)
            if (cursor.count <= 0) {
                cursor.close()
            } else {
                cursor.moveToNext()
                val id = cursor.getInt(cursor.getColumnIndex(FORMATTER_ID.toString()))
                cursor.close()
                return id
            }
            return -1
        }

        /**
         * Returns Formatter ID via ChapterID, this simply compacts a longer line of methods into one.
         *
         * @param id Chapter ID
         * @return Formatter ID
         */
        fun getFormatterIDFromChapterID(id: Int): Int {
            return getFormatterIDFromNovelID(getNovelIDFromChapterID(id))
        }
    }

    object DatabaseDownloads {
        /**
         * Gets downloads that are stored
         *
         * @return DownloadItems to download
         */
        val downloadList: ArrayList<DownloadItem>
            get() {
                val downloadItems = ArrayList<DownloadItem>()
                val cursor = sqLiteDatabase!!.rawQuery("SELECT * from " + Tables.DOWNLOADS + ";", null)
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndex(PARENT_ID.toString()))
                    val nName = cursor.getString(cursor.getColumnIndex(NOVEL_NAME.toString()))
                    val cName = cursor.getString(cursor.getColumnIndex(CHAPTER_NAME.toString()))
                    val formatter = DatabaseIdentification.getFormatterIDFromChapterID(id)
                    downloadItems.add(DownloadItem((getByID(formatter)), nName, cName, id))
                }
                cursor.close()
                return downloadItems
            }

        /**
         * Gets the first download item
         *
         * @return DownloadItem to download
         */
        val firstDownload: DownloadItem?
            get() {
                val cursor = sqLiteDatabase!!.rawQuery("SELECT * from " + Tables.DOWNLOADS + " LIMIT 1;", null)
                return if (cursor.count <= 0) {
                    cursor.close()
                    null
                } else {
                    cursor.moveToNext()
                    val id = cursor.getInt(cursor.getColumnIndex(PARENT_ID.toString()))
                    val nName = cursor.getString(cursor.getColumnIndex(NOVEL_NAME.toString()))
                    val cName = cursor.getString(cursor.getColumnIndex(CHAPTER_NAME.toString()))
                    val formatter = DatabaseIdentification.getFormatterIDFromChapterID(id)
                    cursor.close()
                    DownloadItem(Objects.requireNonNull(getByID(formatter)), nName, cName, id)
                }
            }

        /**
         * Removes download item
         *
         * @param downloadItem download item to remove
         */
        fun removeDownload(downloadItem: DownloadItem) {
            sqLiteDatabase!!.delete(Tables.DOWNLOADS.toString(), PARENT_ID.toString() + "=" + DatabaseIdentification.getChapterIDFromChapterURL(downloadItem.chapterURL) + "", null)
        }

        /**
         * Adds to download list
         *
         * @param downloadItem Download item to add
         */
        fun addToDownloads(downloadItem: DownloadItem) {
            sqLiteDatabase!!.execSQL("insert into " + Tables.DOWNLOADS + " (" +
                    PARENT_ID + "," +
                    NOVEL_NAME + "," +
                    CHAPTER_NAME + "," +
                    PAUSED + ") " +
                    "values (" +
                    DatabaseIdentification.getChapterIDFromChapterURL(downloadItem.chapterURL) + ",'" +
                    downloadItem.novelName.clean() + "','" +
                    downloadItem.chapterName.clean() + "'," + 0 + ")")
        }

        /**
         * Checks if is in download list
         *
         * @param downloadItem download item to check
         * @return if is in list
         */
        fun inDownloads(downloadItem: DownloadItem): Boolean {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + PARENT_ID + " from " + Tables.DOWNLOADS + " where " + PARENT_ID + " = " + DatabaseIdentification.getChapterIDFromChapterURL(downloadItem.chapterURL) + "", null)
            val a = cursor.count
            cursor.close()
            return a > 0
        }

        /**
         * @return count of download items
         */
        val downloadCount: Int
            get() {
                val cursor = sqLiteDatabase!!.rawQuery("select " + PARENT_ID + " from " + Tables.DOWNLOADS, null)
                val a = cursor.count
                cursor.close()
                return a
            }
    }

    object DatabaseChapter {
        //TODO Dev access code
        // --Commented out by Inspection START (12/22/19 11:09 AM):
        //        public static void purgeCache() {
        //            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Tables.CHAPTERS);
        //            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Tables.CHAPTER_IDENTIFICATION);
        //            sqLiteDatabase.execSQL(DBHelper.CHAPTER_IDENTIFICATION_CREATE);
        //            sqLiteDatabase.execSQL(DBHelper.CHAPTERS_CREATE);
        //        }
        // --Commented out by Inspection STOP (12/22/19 11:09 AM)
        /**
         * @param novelID ID of novel
         * @return Count of chapters left to read
         */
        fun getCountOfChaptersUnread(novelID: Int): Int {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + ID + " from " + Tables.CHAPTERS + " where " + PARENT_ID + "=" + novelID + "" + " and " + READ_CHAPTER + "!=" + Status.READ, null)
            val count = cursor.count
            cursor.close()
            return count
        }
        //      public static void updateOrder(int chapterID, int order) {
        //        sqLiteDatabase.execSQL("update " + Tables.CHAPTERS + " set " + Columns.ORDER + "='" + order + "' where " + Columns.ID + "=" + chapterID);
        //   }
        /**
         * Updates the Y coordinate
         * Precondition is the chapter is already in the database.
         *
         * @param chapterID ID to update
         * @param y         integer value scroll
         */
        fun updateY(chapterID: Int, y: Int) {
            sqLiteDatabase!!.execSQL("update " + Tables.CHAPTERS + " set " + Y + "='" + y + "' where " + ID + "=" + chapterID)
        }

        /**
         * Precondition is the chapter is already in the database
         *
         * @param chapterID chapterID to the chapter
         * @return order of chapter
         */
        fun getY(chapterID: Int): Int {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + Y + " from " + Tables.CHAPTERS + " where " + ID + " =" + chapterID, null)
            return if (cursor.count <= 0) {
                cursor.close()
                0
            } else {
                cursor.moveToNext()
                val y = cursor.getInt(cursor.getColumnIndex(Y.toString()))
                cursor.close()
                y
            }
        }

        /**
         * @param chapterID chapter to check
         * @return returns chapter status
         */
        fun getChapterStatus(chapterID: Int): Status {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + READ_CHAPTER + " from " + Tables.CHAPTERS + " where " + ID + " =" + chapterID, null)
            return if (cursor.count <= 0) {
                cursor.close()
                Status.UNREAD
            } else {
                cursor.moveToNext()
                val y = cursor.getInt(cursor.getColumnIndex(READ_CHAPTER.toString()))
                cursor.close()
                if (y == 0) Status.UNREAD else if (y == 1) Status.READING else Status.READ
            }
        }

        // --Commented out by Inspection START (12/22/19 11:09 AM):
        //        public static float getOrder(int chapterID) {
        //            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.ORDER + " from " + Tables.CHAPTERS + " where " + Columns.ID + " =" + chapterID, null);
        //            if (cursor.getCount() <= 0) {
        //                cursor.close();
        //                return -1;
        //            } else {
        //                cursor.moveToNext();
        //                float y = cursor.getFloat(cursor.getColumnIndex(Columns.ORDER.toString()));
        //                cursor.close();
        //                return y;
        //            }
        //        }
        // --Commented out by Inspection STOP (12/22/19 11:09 AM)
        fun getTitle(chapterID: Int): String {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + TITLE + " from " + Tables.CHAPTERS + " where " + ID + " =" + chapterID, null)
            return if (cursor.count <= 0) {
                cursor.close()
                "UNKNOWN"
            } else {
                cursor.moveToNext()
                val y = cursor.getString(cursor.getColumnIndex(TITLE.toString()))
                cursor.close()
                y.checkStringDeserialize()
            }
        }

        /**
         * Sets chapter status
         *
         * @param chapterID chapter to be set
         * @param status    status to be set
         */
        fun setChapterStatus(chapterID: Int, status: Status) {
            sqLiteDatabase!!.execSQL("update " + Tables.CHAPTERS + " set " + READ_CHAPTER + "=" + status + " where " + ID + "=" + chapterID)
            if (status === Status.READ) updateY(chapterID, 0)
        }

        /**
         * Sets bookmark true or false (1 for true, 0 is false)
         *
         * @param chapterID chapterID
         * @param b         1 is true, 0 is false
         */
        fun setBookMark(chapterID: Int, b: Int) {
            sqLiteDatabase!!.execSQL("update " + Tables.CHAPTERS + " set " + BOOKMARKED + "=" + b + " where " + ID + "=" + chapterID)
        }

        /**
         * is this chapter bookmarked?
         *
         * @param chapterID id of chapter
         * @return if bookmarked?
         */
        fun isBookMarked(chapterID: Int): Boolean {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + BOOKMARKED + " from " + Tables.CHAPTERS + " where " + ID + " =" + chapterID, null)
            return if (cursor.count <= 0) {
                cursor.close()
                false
            } else {
                cursor.moveToNext()
                val y = cursor.getInt(cursor.getColumnIndex(BOOKMARKED.toString()))
                cursor.close()
                y == 1
            }
        }

        /**
         * Removes save path from chapter
         *
         * @param chapterID chapter to remove save path of
         */
        fun removePath(chapterID: Int) {
            sqLiteDatabase!!.execSQL("update " + Tables.CHAPTERS + " set " + SAVE_PATH + "=null," + IS_SAVED + "=0 where " + ID + "=" + chapterID)
        }

        /**
         * Adds save path
         *
         * @param chapterID   chapter to update
         * @param chapterPath save path to set
         */
        private fun addSavedPath(chapterID: Int, chapterPath: String) {
            sqLiteDatabase!!.execSQL("update " + Tables.CHAPTERS + " set " + SAVE_PATH + "='" + chapterPath + "'," + IS_SAVED + "=1 where " + ID + "=" + chapterID)
        }

        fun addSavedPath(chapterURL: String, chapterPath: String) {
            addSavedPath(DatabaseIdentification.getChapterIDFromChapterURL(chapterURL), chapterPath)
        }

        /**
         * Is the chapter saved
         *
         * @param chapterID novelURL of the chapter
         * @return true if saved, false otherwise
         */
        fun isSaved(chapterID: Int): Boolean {
            //   Log.d("CheckSave", chapterURL);
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + IS_SAVED + " from " + Tables.CHAPTERS + " where " + ID + "=" + chapterID, null)
            return if (cursor.count <= 0) {
                cursor.close()
                //   Log.d("CheckSave", chapterURL + " FALSE");
                false
            } else {
                cursor.moveToNext()
                val y = cursor.getInt(cursor.getColumnIndex(IS_SAVED.toString()))
                cursor.close()
                //         if (y == 1)
                //          Log.d("CheckSave", chapterURL + " TRUE");
                y == 1
            }
        }


        fun getSavedNovelPath(chapterID: Int): String {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + SAVE_PATH + " from " + Tables.CHAPTERS + " where " + ID + "=" + chapterID, null)
            return if (cursor.count <= 0) {
                cursor.close()
                ""
            } else {
                cursor.moveToNext()
                val savedData = cursor.getString(cursor.getColumnIndex(SAVE_PATH.toString()))
                cursor.close()
                savedData
            }
        }


        /**
         * If the chapter URL is present or not
         *
         * @param chapterURL chapter url
         * @return if present
         */
        fun isNotInChapters(chapterURL: String): Boolean {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + IS_SAVED + " from " + Tables.CHAPTERS + " where " + ID + " =" + DatabaseIdentification.getChapterIDFromChapterURL(chapterURL), null)
            val a = cursor.count
            cursor.close()
            return a <= 0
        }

        fun updateChapter(novelChapter: Chapter) {
            val title = novelChapter.title.checkStringSerialize()
            val release = novelChapter.release.checkStringSerialize()
            Log.d(LOG_CHAP, "Updating data ${novelChapter.link} | ${novelChapter.order}")
            try {
                sqLiteDatabase!!.execSQL("update " + Tables.CHAPTERS +
                        " set " +
                        TITLE + "='" + title + "'," +
                        RELEASE_DATE + "='" + release + "'," +
                        ORDER + "=" + novelChapter.order +
                        " where " + ID + "=" + DatabaseIdentification.getChapterIDFromChapterURL(novelChapter.link))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Adds chapter to database
         *
         * @param novelID      ID of novel
         * @param novelChapter chapterURL
         */
        fun addToChapters(novelID: Int, novelChapter: Chapter) {
            if (!DatabaseIdentification.hasChapter(novelChapter.link)) DatabaseIdentification.addChapter(novelID, novelChapter.link)
            val title = novelChapter.title.checkStringSerialize()
            val release = novelChapter.release.checkStringSerialize()
            Log.d(LOG_CHAP, novelChapter.link + " | " + novelChapter.order)
            try {
                sqLiteDatabase!!.execSQL("insert into " + Tables.CHAPTERS +
                        "(" +
                        ID + "," +
                        PARENT_ID + "," +
                        TITLE + "," +
                        RELEASE_DATE + "," +
                        ORDER + "," +
                        Y + "," +
                        READ_CHAPTER + "," +
                        BOOKMARKED + "," +
                        IS_SAVED +
                        ") " +
                        "values" +
                        "(" +
                        DatabaseIdentification.getChapterIDFromChapterURL(novelChapter.link) + "," +
                        novelID + ",'" +
                        title + "','" +
                        release + "'," +
                        novelChapter.order + "," +
                        0 + "," + 0 + "," + 0 + "," + 0 +
                        ")")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Gets chapters of a novel
         *
         * @param novelID ID to retrieve from
         * @return List of chapters saved of novel
         */
        fun getChapters(novelID: Int): List<Chapter> {
            val cursor = sqLiteDatabase!!.rawQuery("select " + ID + ", " + TITLE + ", " + RELEASE_DATE + ", " + ORDER + " from " + Tables.CHAPTERS + " where " + PARENT_ID + " =" + novelID, null)
            return if (cursor.count <= 0) {
                cursor.close()
                ArrayList()
            } else {
                val novelChapters = ArrayList<Chapter>()
                while (cursor.moveToNext()) {
                    try {
                        val url = DatabaseIdentification.getChapterURLFromChapterID(cursor.getInt(cursor.getColumnIndex(ID.toString())))
                        val novelChapter = Chapter(
                                cursor.getString(RELEASE_DATE).checkStringDeserialize(),
                                cursor.getString(TITLE).checkStringDeserialize(),
                                url,
                                cursor.getDouble(cursor.getColumnIndex(ORDER.toString())))
                        novelChapters.add(novelChapter)
                    } catch (e: RuntimeException) {
                        e.printStackTrace()
                    }
                }
                cursor.close()
                novelChapters.sortWith(Comparator { (_, _, _, order1), (_, _, _, order) -> order1.compareTo(order) })
                novelChapters
            }
        }

        /**
         * Gets chapters of a novel
         *
         * @param novelID ID to retrieve from
         * @return List of chapters saved of novel (ID only)
         */
        fun getChaptersOnlyIDs(novelID: Int): List<Int> {
            val cursor = sqLiteDatabase!!.rawQuery("select " + ID + ", " + ORDER + " from " + Tables.CHAPTERS + " where " + PARENT_ID + " =" + novelID, null)
            return if (cursor.count <= 0) {
                cursor.close()
                ArrayList()
            } else {
                val novelChapters = ArrayList<MicroNovelChapter>()
                while (cursor.moveToNext()) {
                    try {
                        val id = cursor.getInt(cursor.getColumnIndex(ID.toString()))
                        val novelChapter = MicroNovelChapter()
                        novelChapter.id = id
                        novelChapter.order = cursor.getDouble(cursor.getColumnIndex(ORDER.toString()))
                        novelChapters.add(novelChapter)
                    } catch (e: RuntimeException) {
                        e.printStackTrace()
                    }
                }
                cursor.close()
                novelChapters.sortWith(Comparator { novelChapter: MicroNovelChapter, t1: MicroNovelChapter -> java.lang.Double.compare(novelChapter.order, t1.order) })
                val integers = ArrayList<Int>()
                for (novelChapter in novelChapters) integers.add(novelChapter.id)
                integers
            }
        }

        /**
         * Gets a chapter by it's URL
         *
         * @param chapterID id of chapter
         * @return NovelChapter of said chapter
         */
        fun getChapter(chapterID: Int): Chapter? {
            val cursor = sqLiteDatabase!!.rawQuery("select " + TITLE + "," + ID + "," + RELEASE_DATE + "," + ORDER + " from " + Tables.CHAPTERS + " where " + ID + " =" + chapterID, null)
            return if (cursor.count <= 0) {
                cursor.close()
                null
            } else {
                cursor.moveToNext()
                val novelChapter = Chapter()
                val title = cursor.getString(TITLE).checkStringDeserialize()
                val link = DatabaseIdentification.getChapterURLFromChapterID(cursor.getInt(cursor.getColumnIndex(ID.toString())))
                val release = cursor.getString(RELEASE_DATE).checkStringDeserialize()
                val order = cursor.getDouble(cursor.getColumnIndex(ORDER.toString()))
                novelChapter.title = title
                novelChapter.link = link
                novelChapter.release = release
                novelChapter.order = order
                novelChapter
            }
        }

        private class MicroNovelChapter {
            var order = 0.0
            var id = 0
        }
    }

    object DatabaseNovels {
        /**
         * Bookmarks the novel
         *
         * @param novelID novelID of the novel
         */
        fun bookMark(novelID: Int) {
            sqLiteDatabase!!.execSQL("update " + Tables.NOVELS + " set " + BOOKMARKED + "=1 where " + PARENT_ID + "=" + novelID)
        }

        /**
         * UnBookmarks the novel
         *
         * @param novelID id
         */
        fun unBookmark(novelID: Int) {
            sqLiteDatabase!!.execSQL("update " + Tables.NOVELS + " set " + BOOKMARKED + "=0 where " + PARENT_ID + "=" + novelID)
        }

        fun isBookmarked(novelID: Int): Boolean {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + BOOKMARKED + " from " + Tables.NOVELS + " where " + PARENT_ID + "=" + novelID, null)
            if (cursor.count <= 0) {
                cursor.close()
                return false
            }
            cursor.moveToNext()
            println(Arrays.toString(cursor.columnNames))
            val a = cursor.getInt(cursor.getColumnIndex("bookmarked"))
            cursor.close()
            return a > 0
        }

        fun setReaderType(novelID: Int, reader: Int) {
            sqLiteDatabase!!.execSQL("update " + Tables.NOVELS + " set " + READER_TYPE + "=" + reader + " where " + PARENT_ID + "=" + novelID)
        }

        /**
         * Gets reader type for novel
         *
         * @param novelID novelID
         * @return -2 is no such novel, -1 is default, 0 is the same as -1, and 1+ is a specific reading type
         */
        fun getReaderType(novelID: Int): Int {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + READER_TYPE + " from " + Tables.NOVELS + " where " + PARENT_ID + "=" + novelID, null)
            if (cursor.count <= 0) {
                cursor.close()
                return -2
            }
            cursor.moveToNext()
            println(Arrays.toString(cursor.columnNames))
            val a = cursor.getInt(cursor.getColumnIndex(READER_TYPE.toString()))
            cursor.close()
            return a
        }

        fun addToLibrary(activity: Activity, formatter: Int, novelPage: Novel.Info, novelURL: String, readingStatus: Int) {
            DatabaseIdentification.addNovel(novelURL, formatter)
            val imageURL = novelPage.imageURL
            try {
                sqLiteDatabase!!.execSQL("insert into " + Tables.NOVELS + "(" +
                        PARENT_ID + "," +
                        BOOKMARKED + "," +
                        READING_STATUS + "," +
                        READER_TYPE + "," +
                        TITLE + "," +
                        IMAGE_URL + "," +
                        DESCRIPTION + "," +
                        GENRES + "," +
                        AUTHORS + "," +
                        STATUS + "," +
                        TAGS + "," +
                        ARTISTS + "," +
                        LANGUAGE +
                        ")" + "values" + "(" +
                        DatabaseIdentification.getNovelIDFromNovelURL(novelURL) + "," +
                        0 + "," +
                        readingStatus + "," +
                        -1 + "," +
                        "'" + novelPage.title.checkStringSerialize() + "'," +
                        "'" + imageURL + "'," +
                        "'" + novelPage.description.checkStringSerialize() + "'," +
                        "'" + novelPage.genres.convertArrayToString().checkStringSerialize() + "'," +
                        "'" + novelPage.authors.convertArrayToString().checkStringSerialize() + "'," +
                        "'" + novelPage.status.title + "'," +
                        "'" + novelPage.tags.convertArrayToString().checkStringSerialize() + "'," +
                        "'" + novelPage.artists.convertArrayToString().checkStringSerialize() + "'," +
                        "'" + novelPage.language.checkStringSerialize() + "')"
                )
            } catch (e: Exception) {
                ErrorAlert(activity)
                        .setMessage(e.message)
                        .setError(e)
                        .runOnUI()
            }
        }
        // --Commented out by Inspection START (12/22/19 11:09 AM):
        //        /**
        //         * @param novelURL url of novel to remove
        //         * @return if successful
        //         */
        //        public static boolean removeFromLibrary(@NotNull String novelURL) {
        //            boolean a = sqLiteDatabase.delete(Tables.NOVELS.toString(), Columns.PARENT_ID + "=" + getNovelIDFromNovelURL(novelURL), null) > 0;
        //            boolean b = sqLiteDatabase.delete(Tables.NOVEL_IDENTIFICATION.toString(), Columns.ID + "=" + getNovelIDFromNovelURL(novelURL), null) > 0;
        //            return a && b;
        //        }
        // --Commented out by Inspection STOP (12/22/19 11:09 AM)
        /**
         * Is a novel in the library or not
         *
         * @param novelID Novel novelID
         * @return yes or no
         */
        fun isNotInNovels(novelID: Int): Boolean {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + ID + " from " + Tables.NOVEL_IDENTIFICATION + " where " + ID + " ='" + novelID + "'", null)
            val i = cursor.count
            cursor.close()
            return i <= 0
        }

        fun isNotInNovels(novelURL: String): Boolean {
            return -1 == DatabaseIdentification.getNovelIDFromNovelURL(novelURL)
        }

        // --Commented out by Inspection START (12/22/19 11:09 AM):
        //        /**
        //         * Get's the entire library to be listed
        //         *
        //         * @return the library
        //         */
        //        @NonNull
        //        public static ArrayList<NovelCard> getLibrary() {
        //            Log.d("DL", "Getting");
        //            Cursor cursor = sqLiteDatabase.query(Tables.NOVELS.toString(),
        //                    new String[]{Columns.PARENT_ID.toString(), Columns.TITLE.toString(), Columns.IMAGE_URL.toString()},
        //                    Columns.BOOKMARKED + "=1", null, null, null, null);
        //
        //            ArrayList<NovelCard> novelCards = new ArrayList<>();
        //            if (cursor.getCount() <= 0) {
        //                cursor.close();
        //                return new ArrayList<>();
        //            } else {
        //                while (cursor.moveToNext()) {
        //                    try {
        //                        int parent = cursor.getInt(cursor.getColumnIndex(Columns.PARENT_ID.toString()));
        //                        novelCards.add(new NovelCard(
        //                                checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString()))),
        //                                parent, DatabaseIdentification.getNovelURLfromNovelID(parent),
        //                                cursor.getString(cursor.getColumnIndex(Columns.IMAGE_URL.toString())),
        //                                DatabaseIdentification.getFormatterIDFromNovelID(parent)
        //                        ));
        //                    } catch (Exception e) {
        //                        e.printStackTrace();
        //                    }
        //                }
        //                cursor.close();
        //                return novelCards;
        //            }
        //        }
        // --Commented out by Inspection STOP (12/22/19 11:09 AM)
        val intLibrary: ArrayList<Int>
            get() {
                Log.d(LOG_DOWN, "Getting")
                val cursor = sqLiteDatabase!!.query(Tables.NOVELS.toString(), arrayOf(PARENT_ID.toString()), BOOKMARKED.toString() + "=1", null, null, null, null)
                val novelCards = ArrayList<Int>()
                return if (cursor.count <= 0) {
                    cursor.close()
                    ArrayList()
                } else {
                    while (cursor.moveToNext()) {
                        val parent = cursor.getInt(cursor.getColumnIndex(PARENT_ID.toString()))
                        novelCards.add(parent)
                    }
                    cursor.close()
                    novelCards
                }
            }

        fun getNovel(novelID: Int): NovelCard {
            Log.d(LOG_DOWN, "Getting")
            val cursor = sqLiteDatabase!!.query(Tables.NOVELS.toString(), arrayOf(PARENT_ID.toString(), TITLE.toString(), IMAGE_URL.toString()),
                    "$BOOKMARKED=1 and $PARENT_ID=$novelID", null, null, null, null)
            if (cursor.count <= 0) {
                cursor.close()
            } else {
                cursor.moveToNext()
                try {
                    val novelCard = NovelCard(
                            cursor.getString(TITLE).checkStringDeserialize(),
                            novelID, DatabaseIdentification.getNovelURLfromNovelID(novelID)
                            ?: "",
                            cursor.getString(cursor.getColumnIndex(IMAGE_URL.toString())),
                            DatabaseIdentification.getFormatterIDFromNovelID(novelID)
                    )
                    cursor.close()
                    return novelCard
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }
            }
            return NovelCard("", -2, "", "", -1)
        }

        fun getNovelTitle(novelID: Int): String {
            Log.d(LOG_DOWN, "Getting")
            val cursor = sqLiteDatabase!!.query(Tables.NOVELS.toString(), arrayOf(TITLE.toString()),
                    "$BOOKMARKED=1 and $PARENT_ID=$novelID", null, null, null, null)
            if (cursor.count <= 0) {
                cursor.close()
                return "unknown"
            } else {
                cursor.moveToNext()
                try {
                    val title = cursor.getString(TITLE).checkStringDeserialize()
                    cursor.close()
                    return title
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }
            }
            return "unknown"
        }

        /**
         * Gets saved novelPage
         *
         * @param novelID novel to retrieve
         * @return Saved novelPage
         */
        fun getNovelPage(novelID: Int): Novel.Info {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " +
                    TITLE + "," +
                    IMAGE_URL + "," +
                    DESCRIPTION + "," +
                    GENRES + "," +
                    AUTHORS + "," +
                    STATUS + "," +
                    TAGS + "," +
                    ARTISTS + "," +
                    LANGUAGE + "," +
                    MAX_CHAPTER_PAGE +
                    " from " + Tables.NOVELS + " where " + PARENT_ID + "=" + novelID, null)
            if (cursor.count <= 0) {
                cursor.close()
                return Novel.Info()
            } else {
                cursor.moveToNext()
                val novelPage = Novel.Info()
                novelPage.title = cursor.getString(TITLE).checkStringDeserialize()
                novelPage.imageURL = cursor.getString(IMAGE_URL) ?: ""
                novelPage.description = cursor.getString(DESCRIPTION).checkStringDeserialize()
                novelPage.genres = cursor.getString(GENRES).checkStringDeserialize().convertStringToArray()
                novelPage.authors = cursor.getString(AUTHORS).checkStringDeserialize().convertStringToArray()
                novelPage.status = cursor.getString(STATUS).convertStringToStati()
                novelPage.tags = cursor.getString(TAGS).checkStringDeserialize().convertStringToArray()
                novelPage.artists = cursor.getString(ARTISTS).checkStringDeserialize().convertStringToArray()
                novelPage.language = cursor.getString(LANGUAGE).checkStringDeserialize()
                cursor.close()
                return novelPage
            }
        }

        // --Commented out by Inspection START (12/22/19 11:09 AM):
        //        public static void setStatus(int novelID, @NotNull Status status) {
        //            sqLiteDatabase.execSQL("update " + Tables.NOVELS + " set " + Columns.READING_STATUS + "=" + status + " where " + Columns.PARENT_ID + "=" + novelID);
        //        }
        // --Commented out by Inspection STOP (12/22/19 11:09 AM)
        fun getNovelStatus(novelID: Int): Status {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + READING_STATUS + " from " + Tables.NOVELS + " where " + PARENT_ID + " =" + novelID, null)
            return if (cursor.count <= 0) {
                cursor.close()
                Status.UNREAD
            } else {
                cursor.moveToNext()
                val y = cursor.getInt(cursor.getColumnIndex(READING_STATUS.toString()))
                cursor.close()
                if (y == 0) Status.UNREAD else if (y == 1) Status.READING else if (y == 2) Status.READ else if (y == 3) Status.ONHOLD else Status.DROPPED
            }
        }

        fun updateNovel(novelURL: String, novelPage: Novel.Info) {
            val imageURL = novelPage.imageURL
            sqLiteDatabase!!.execSQL("update " + Tables.NOVELS + " set " +
                    TITLE + "='" + novelPage.title.checkStringSerialize() + "'," +
                    IMAGE_URL + "='" + imageURL + "'," +
                    DESCRIPTION + "='" + novelPage.description.checkStringSerialize() + "'," +
                    GENRES + "='" + novelPage.genres.convertArrayToString().checkStringSerialize() + "'," +
                    AUTHORS + "='" + novelPage.authors.convertArrayToString().checkStringSerialize() + "'," +
                    STATUS + "='" + novelPage.status.title + "'," +
                    TAGS + "='" + novelPage.tags.convertArrayToString().checkStringSerialize() + "'," +
                    ARTISTS + "='" + novelPage.artists.convertArrayToString().checkStringSerialize() + "'," +
                    LANGUAGE + "='" + novelPage.language.checkStringSerialize() + "'" +
                    " where " + PARENT_ID + "=" + DatabaseIdentification.getNovelIDFromNovelURL(novelURL))
        }

        fun migrateNovel(activity: Activity, oldID: Int, newURL: String, formatterID: Int, newNovel: Novel.Info, status: Int) {
            unBookmark(oldID)
            if (isNotInNovels(newURL)) addToLibrary(activity, formatterID, newNovel, newURL, status)
            bookMark(DatabaseIdentification.getNovelIDFromNovelURL(newURL))
        }
    }

    object DatabaseUpdates {
        fun trimDate(date: DateTime): DateTime {
            val cal = Calendar.getInstance()
            cal.clear() // as per BalusC comment.
            cal.time = date.toDate()
            cal[Calendar.HOUR_OF_DAY] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.SECOND] = 0
            cal[Calendar.MILLISECOND] = 0
            return DateTime(cal.timeInMillis)
        }

        fun getTotalDays(): Int {
            val firstDay = DateTime(getStartingDay())
            val latest = DateTime(getLatestDay())
            return Days.daysBetween(firstDay, latest).days
        }

        fun getStartingDay(): Long {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + TIME + " FROM " + Tables.UPDATES + " ORDER BY ROWID ASC LIMIT 1", null)
            return if (cursor.count <= 0) {
                cursor.close()
                0
            } else {
                cursor.moveToNext()
                val day = cursor.getLong(cursor.getColumnIndex(TIME.toString()))
                cursor.close()
                trimDate(DateTime(day)).millis
            }
        }

        private fun getLatestDay(): Long {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + TIME + " FROM " + Tables.UPDATES + " ORDER BY ROWID DESC LIMIT 1", null)
            return if (cursor.count <= 0) {
                cursor.close()
                0
            } else {
                cursor.moveToNext()
                val day = cursor.getLong(cursor.getColumnIndex(TIME.toString()))
                cursor.close()
                trimDate(DateTime(day)).millis
            }
        }

        /**
         * Gets count on day
         *
         * @param date1 first
         * @param date2 second
         */
        @Throws(IncorrectDateException::class)
        fun getCountBetween(date1: Long, date2: Long): Int {
            if (date2 <= date1) throw IncorrectDateException("Dates implemented wrongly")
            val cursor = sqLiteDatabase!!.rawQuery(
                    "SELECT " + TIME + " from " + Tables.UPDATES +
                            " where " + TIME + "<" + date2 + " and " + TIME + ">=" + date1, null)
            val c = cursor.count
            cursor.close()
            return c
        }

        /**
         * Works as long as date2 is after date1
         *
         * @param date1 first
         * @param date2 second
         */
        @Throws(IncorrectDateException::class)
        fun getTimeBetween(date1: Long, date2: Long): ArrayList<Update> {
            if (date2 <= date1) throw IncorrectDateException("Dates implemented wrongly")
            Log.d(LOG_UPDA, "Getting dates between [" + DateTime(date1) + "] and [" + DateTime(date2) + "]")
            val cursor = sqLiteDatabase!!.rawQuery(
                    "SELECT " + ID + "," + PARENT_ID + "," + TIME + " from " + Tables.UPDATES +
                            " where " + TIME + "<" + date2 + " and " + TIME + ">=" + date1, null)
            val novelCards = ArrayList<Update>()
            return if (cursor.count <= 0) {
                cursor.close()
                ArrayList()
            } else {
                while (cursor.moveToNext()) {
                    novelCards.add(
                            Update(cursor.getInt(cursor.getColumnIndex(ID.toString())),
                                    cursor.getInt(cursor.getColumnIndex(PARENT_ID.toString())),
                                    cursor.getLong(cursor.getColumnIndex(TIME.toString())))
                    )
                }
                cursor.close()
                novelCards
            }
        }

        fun addToUpdates(novelID: Int, chapterURL: String, time: Long) {
            sqLiteDatabase!!.execSQL("insert into " + Tables.UPDATES + "(" + ID + "," + PARENT_ID + "," + TIME + ") values(" +
                    DatabaseIdentification.getChapterIDFromChapterURL(chapterURL) + "," +
                    novelID + "," +
                    time + ")")
        } // --Commented out by Inspection START (12/22/19 11:10 AM):

        //        public static boolean removeNovelFromUpdates(int novelID) {
        //            return sqLiteDatabase.delete(Tables.UPDATES.toString(), Columns.PARENT_ID + "=" + novelID, null) > 0;
        //        }
        // --Commented out by Inspection STOP (12/22/19 11:10 AM)
        // --Commented out by Inspection START (12/22/19 11:10 AM):
        //        public static boolean removeFromUpdates(@NotNull String chapterURL) {
        //            return sqLiteDatabase.delete(Tables.UPDATES.toString(), Columns.ID + "=" + getChapterIDFromChapterURL(chapterURL), null) > 0;
        //        }
        // --Commented out by Inspection STOP (12/22/19 11:10 AM)
        /*
      public static ArrayList<Update> getAll() {
            Log.d("DL", "Getting");
            Cursor cursor = sqLiteDatabase.query(Tables.UPDATES.toString(),
                    new String[]{Columns.NOVEL_URL.toString(), Columns.CHAPTER_URL.toString(), Columns.TIME.toString()}, null, null, null, null, null);

            ArrayList<Update> novelCards = new ArrayList<>();
            if (cursor.getCount() <= 0) {
                cursor.close();
                return new ArrayList<>();
            } else {
                while (cursor.moveToNext()) {
                    novelCards.add(new Update(
                            cursor.getString(cursor.getColumnIndex(Columns.NOVEL_URL.toString())),
                            cursor.getString(cursor.getColumnIndex(Columns.CHAPTER_URL.toString())),
                            cursor.getLong(cursor.getColumnIndex(Columns.TIME.toString()))
                    ));
                }
                cursor.close();
                return novelCards;
            }
        }
        */
        internal class IncorrectDateException(message: String?) : Exception(message)
    }

    object DatabaseFormatters {
        fun addToFormatterList(name: String?, id: Int, md5: String, hasRepo: Boolean, repo: String) {
            var i = 0
            if (hasRepo) i = 1
            sqLiteDatabase!!.execSQL("insert into " + Tables.FORMATTERS + "(" + FORMATTER_NAME + "," + FORMATTER_ID + "," + MD5 + "," + HAS_CUSTOM_REPO + "," + CUSTOM_REPO + ") values('" +
                    name.checkStringSerialize() + "'," +
                    id + ",'" +
                    md5 + "'," +
                    i + ",'" +
                    repo + "')")
        }

        fun removeFormatterFromList(name: String?) {
            sqLiteDatabase!!.execSQL("delete from " + Tables.FORMATTERS + " where " + FORMATTER_NAME + "=" + name.checkStringSerialize())
        }

        fun removeFormatterFromList(id: Int) {
            sqLiteDatabase!!.execSQL("delete from " + Tables.FORMATTERS + " where " + FORMATTER_ID + "=" + id)
        }

        fun getMD5Sum(formatterID: Int): String {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + MD5 + " FROM " + Tables.FORMATTERS + " where " + FORMATTER_ID + "=" + formatterID, null)
            return if (cursor.count <= 0) {
                cursor.close()
                ""
            } else {
                cursor.moveToNext()
                val string = cursor.getString(cursor.getColumnIndex(MD5.toString()))
                cursor.close()
                string
            }
        }

        fun getFormatterName(formatterID: Int): String {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + FORMATTER_NAME + " FROM " + Tables.FORMATTERS + " where " + FORMATTER_ID + "=" + formatterID, null)
            return if (cursor.count <= 0) {
                cursor.close()
                ""
            } else {
                cursor.moveToNext()
                val string = cursor.getString(cursor.getColumnIndex(FORMATTER_NAME.toString()))
                cursor.close()
                string.checkStringDeserialize()
            }
        }

        fun hasCustomRepo(formatterID: Int): Boolean {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + HAS_CUSTOM_REPO + " FROM " + Tables.FORMATTERS + " where " + FORMATTER_ID + "=" + formatterID, null)
            return if (cursor.count <= 0) {
                cursor.close()
                false
            } else {
                cursor.moveToNext()
                val i = cursor.getInt(cursor.getColumnIndex(HAS_CUSTOM_REPO.toString()))
                cursor.close()
                i == 1
            }
        }

        fun getCustomRepo(formatterID: Int): String {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + CUSTOM_REPO + " FROM " + Tables.FORMATTERS + " where " + FORMATTER_ID + "=" + formatterID, null)
            return if (cursor.count <= 0) {
                cursor.close()
                ""
            } else {
                cursor.moveToNext()
                val string = cursor.getString(cursor.getColumnIndex(CUSTOM_REPO.toString()))
                cursor.close()
                string
            }
        }

        fun getFormatterFromSystem(formatterID: Int): LuaFormatter? {
            val cursor = sqLiteDatabase!!.rawQuery("SELECT " + FORMATTER_NAME + " FROM " + Tables.FORMATTERS + " where " + FORMATTER_ID + "=" + formatterID, null)
            return if (cursor.count <= 0) {
                cursor.close()
                null
            } else {
                cursor.moveToNext()
                val string = cursor.getString(cursor.getColumnIndex(FORMATTER_NAME.toString()))
                cursor.close()
                LuaFormatter(File(Environment.getExternalStorageState() + FormatterUtils.scriptDirectory + FormatterUtils.sourceFolder + string + ".lua"))
            }
        }
    }
}