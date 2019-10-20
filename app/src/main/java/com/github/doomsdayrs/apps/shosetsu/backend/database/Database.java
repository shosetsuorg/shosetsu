package com.github.doomsdayrs.apps.shosetsu.backend.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.Update;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.checkStringDeserialize;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.checkStringSerialize;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.convertArrayToString;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.convertStringToArray;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.convertStringToStati;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.addNovel;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getChapterIDFromChapterURL;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getChapterURLFromChapterID;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getFormatterIDFromChapterID;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getNovelIDFromNovelURL;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getNovelURLFromChapterID;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.hasChapter;

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

public class Database {
    /**
     * SQLITEDatabase
     */
    public static SQLiteDatabase sqLiteDatabase;


    /**
     * Tables to work with
     */
    public enum Tables {
        NOVEL_IDENTIFICATION("novel_identification"),
        CHAPTER_IDENTIFICATION("chapter_identification"),
        NOVELS("novels"),
        UPDATES("updates"),
        DOWNLOADS("downloads"),
        CHAPTERS("chapters");

        final String TABLE;

        Tables(String table) {
            this.TABLE = table;
            System.currentTimeMillis();
        }

        @NotNull
        @Override
        public String toString() {
            return TABLE;
        }
    }

    /**
     * Columns to work with
     */
    public enum Columns {
        URL("url"),
        PARENT_ID("parent_id"),
        ID("id"),
        READER_TYPE("reader_type"),

        TITLE("title"),
        IMAGE_URL("image_url"),
        DESCRIPTION("description"),
        GENRES("genres"),
        AUTHORS("authors"),
        STATUS("status"),
        TAGS("tags"),
        ARTISTS("artists"),
        LANGUAGE("language"),
        MAX_CHAPTER_PAGE("max_chapter_page"),

        RELEASE_DATE("release_date"),


        FORMATTER_ID("formatterID"),
        READ_CHAPTER("read"),
        Y("y"),
        BOOKMARKED("bookmarked"),
        IS_SAVED("isSaved"),
        SAVE_PATH("savePath"),
        NOVEL_NAME("novelName"),
        CHAPTER_NAME("chapterName"),
        PAUSED("paused"),
        READING_STATUS("reading_status"),
        TIME("time");
        final String COLUMN;

        Columns(String column) {
            this.COLUMN = column;
        }

        @NotNull
        @Override
        public String toString() {
            return COLUMN;
        }
    }

    public static class DatabaseIdentification {

        public static boolean hasChapter(String chapterURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.ID + " from " + Tables.CHAPTER_IDENTIFICATION + " where " + Columns.URL + " = '" + chapterURL + "'", null);
            int a = cursor.getCount();
            cursor.close();
            return !(a <= 0);
        }

        public static void addChapter(int novelID, String chapterURL) {
            try {
                sqLiteDatabase.execSQL("insert into " + Tables.CHAPTER_IDENTIFICATION + "(" +
                        Columns.PARENT_ID + "," +
                        Columns.URL +
                        ")" +
                        "values" +
                        "('" +
                        novelID + "','" +
                        chapterURL +
                        "')");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void addNovel(String novelURL, int formatter) {
            try {
                sqLiteDatabase.execSQL("insert into " + Tables.NOVEL_IDENTIFICATION + "('" +
                        Columns.URL + "'," +
                        Columns.FORMATTER_ID +
                        ")" +
                        "values" +
                        "('" +
                        novelURL +
                        "'," +
                        formatter +
                        ")");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * @param url NovelURL
         * @return NovelID
         */
        public static int getNovelIDFromNovelURL(String url) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.ID + " from " + Tables.NOVEL_IDENTIFICATION + " where " + Columns.URL + " ='" + url + "'", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return 0;
            } else {
                cursor.moveToNext();
                int id = cursor.getInt(cursor.getColumnIndex(Columns.ID.toString()));
                cursor.close();
                return id;
            }
        }

        /**
         * @param url ChapterURL
         * @return ChapterID
         */
        public static int getChapterIDFromChapterURL(String url) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.ID + " from " + Tables.CHAPTER_IDENTIFICATION + " where " + Columns.URL + " = '" + url + "'", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return 0;
            } else {
                cursor.moveToNext();
                int id = cursor.getInt(cursor.getColumnIndex(Columns.ID.toString()));
                cursor.close();
                return id;
            }
        }


        /**
         * @param id ChapterID
         * @return ChapterURL
         */
        public static String getChapterURLFromChapterID(int id) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.URL + " from " + Tables.CHAPTER_IDENTIFICATION + " where " + Columns.ID + " = " + id + "", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
            } else {
                cursor.moveToNext();
                String url = cursor.getString(cursor.getColumnIndex(Columns.URL.toString()));
                cursor.close();
                return url;
            }
            return null;
        }

        /**
         * @param id ChapterID
         * @return NovelID
         */
        public static int getNovelIDFromChapterID(int id) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.PARENT_ID + " from " + Tables.CHAPTER_IDENTIFICATION + " where " + Columns.ID + " = " + id + "", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return 0;
            } else {
                cursor.moveToNext();
                int parent = cursor.getInt(cursor.getColumnIndex(Columns.PARENT_ID.toString()));
                cursor.close();
                return parent;
            }
        }

        /**
         * @param id Chapter ID
         * @return Chapter URL
         */
        public static String getNovelURLFromChapterID(int id) {
            return getNovelURLfromNovelID(getNovelIDFromChapterID(id));
        }

        /**
         * @param url Chapter url
         * @return Novel URL
         */
        public static String getNovelURLFromChapterURL(String url) {
            return getNovelURLFromChapterID(getChapterIDFromChapterURL(url));
        }

        /**
         * @param id NovelID
         * @return NovelURL
         */
        public static String getNovelURLfromNovelID(int id) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.URL + " from " + Tables.NOVEL_IDENTIFICATION + " where " + Columns.ID + " = " + id + "", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
            } else {
                cursor.moveToNext();
                String url = cursor.getString(cursor.getColumnIndex(Columns.URL.toString()));
                cursor.close();
                return url;
            }
            return null;
        }


        /**
         * Returns Formatter ID via Novel ID
         *
         * @param id Novel ID
         * @return Formatter ID
         */
        public static int getFormatterIDFromNovelID(int id) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.FORMATTER_ID + " from " + Tables.NOVEL_IDENTIFICATION + " where " + Columns.ID + " = " + id + "", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
            } else {
                cursor.moveToNext();
                int ID = cursor.getInt(cursor.getColumnIndex(Columns.FORMATTER_ID.toString()));
                cursor.close();
                return ID;
            }
            return -1;
        }

        /**
         * Returns Formatter ID via Novel URL
         *
         * @param url Novel URL
         * @return Formatter ID
         */
        public static int getFormatterIDFromNovelURL(String url) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.FORMATTER_ID + " from " + Tables.NOVEL_IDENTIFICATION + " where " + Columns.URL + " = '" + url + "'", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
            } else {
                cursor.moveToNext();
                int ID = cursor.getInt(cursor.getColumnIndex(Columns.FORMATTER_ID.toString()));
                cursor.close();
                return ID;
            }
            return -1;
        }

        /**
         * Returns Formatter ID via ChapterID, this simply compacts a longer line of methods into one.
         *
         * @param id Chapter ID
         * @return Formatter ID
         */
        public static int getFormatterIDFromChapterID(int id) {
            return getFormatterIDFromNovelID(getNovelIDFromChapterID(id));
        }
    }

    public static class DatabaseDownloads {
        /**
         * Gets downloads that are stored
         *
         * @return DownloadItems to download
         */
        public static List<DownloadItem> getDownloadList() {
            ArrayList<DownloadItem> downloadItems = new ArrayList<>();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from " + Tables.DOWNLOADS + ";", null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(Columns.PARENT_ID.toString()));
                String nName = cursor.getString(cursor.getColumnIndex(Columns.NOVEL_NAME.toString()));
                String cName = cursor.getString(cursor.getColumnIndex(Columns.CHAPTER_NAME.toString()));
                int formatter = cursor.getInt(cursor.getColumnIndex(Columns.FORMATTER_ID.toString()));
                downloadItems.add(new DownloadItem(DefaultScrapers.getByID(formatter), nName, cName, DatabaseIdentification.getNovelURLFromChapterID(id), DatabaseIdentification.getChapterURLFromChapterID(id)));
            }
            cursor.close();

            return downloadItems;
        }

        /**
         * Gets the first download item
         *
         * @return DownloadItem to download
         */
        public static DownloadItem getFirstDownload() {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from " + Tables.DOWNLOADS + " LIMIT 1;", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            } else {
                cursor.moveToNext();
                int id = cursor.getInt(cursor.getColumnIndex(Columns.PARENT_ID.toString()));
                String nName = cursor.getString(cursor.getColumnIndex(Columns.NOVEL_NAME.toString()));
                String cName = cursor.getString(cursor.getColumnIndex(Columns.CHAPTER_NAME.toString()));
                int formatter = getFormatterIDFromChapterID(id);
                cursor.close();
                return new DownloadItem(DefaultScrapers.getByID(formatter), nName, cName, DatabaseIdentification.getNovelURLFromChapterID(id), DatabaseIdentification.getChapterURLFromChapterID(id));
            }
        }

        /**
         * Removes download item
         *
         * @param downloadItem download item to remove
         * @return if removed
         */
        public static boolean removeDownload(DownloadItem downloadItem) {
            return sqLiteDatabase.delete(Tables.DOWNLOADS.toString(), Columns.PARENT_ID + "=" + DatabaseIdentification.getChapterIDFromChapterURL(downloadItem.chapterURL) + "", null) > 0;
        }

        /**
         * Adds to download list
         *
         * @param downloadItem Download item to add
         */
        public static void addToDownloads(DownloadItem downloadItem) {
            sqLiteDatabase.execSQL("insert into " + Tables.DOWNLOADS + " (" +
                    Columns.PARENT_ID + "," +
                    Columns.NOVEL_NAME + "," +
                    Columns.CHAPTER_NAME + "," +
                    Columns.PAUSED + ") " +
                    "values (" +
                    DatabaseIdentification.getChapterIDFromChapterURL(downloadItem.chapterURL) + ",'" +
                    DownloadItem.cleanse(downloadItem.novelName) + "','" +
                    DownloadItem.cleanse(downloadItem.chapterName) + "'," + 0 + ")");
        }

        /**
         * Checks if is in download list
         *
         * @param downloadItem download item to check
         * @return if is in list
         */
        public static boolean inDownloads(DownloadItem downloadItem) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.PARENT_ID + " from " + Tables.DOWNLOADS + " where " + Columns.PARENT_ID + " = " + DatabaseIdentification.getChapterIDFromChapterURL(downloadItem.chapterURL) + "", null);
            int a = cursor.getCount();
            cursor.close();
            return !(a <= 0);
        }

        /**
         * @return count of download items
         */
        public static int getDownloadCount() {
            Cursor cursor = sqLiteDatabase.rawQuery("select " + Columns.PARENT_ID + " from " + Tables.DOWNLOADS, null);
            int a = cursor.getCount();
            cursor.close();
            return a;
        }
    }

    public static class DatabaseChapter {

        // TODO This will remove all chapter data
        public static void purgeCache() {

        }

        //TODO purge not needed cache
        public static void purgeUneededCache() {
        }

        /**
         * @param novelURL URL of novel
         * @return Count of chapters left to read
         */
        public static int getCountOfChaptersUnread(String novelURL) {
            int novelID = getNovelIDFromNovelURL(novelURL);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.ID + " from " + Tables.CHAPTERS + " where " + Columns.PARENT_ID + "=" + novelID + "" + " and " + Columns.READ_CHAPTER + "!=" + Status.READ, null);
            int count = cursor.getCount();
            cursor.close();
            return count;
        }


        /**
         * Updates the Y coordinate
         * Precondition is the chapter is already in the database.
         *
         * @param chapterURL novelURL to update
         * @param y          integer value scroll
         */
        public static void updateY(String chapterURL, int y) {
            sqLiteDatabase.execSQL("update " + Tables.CHAPTERS + " set " + Columns.Y + "='" + y + "' where " + Columns.ID + "=" + DatabaseIdentification.getChapterIDFromChapterURL(chapterURL));
        }


        /**
         * @param chapterURL chapter to check
         * @return returns chapter status
         */
        public static Status getStatus(String chapterURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.READ_CHAPTER + " from " + Tables.CHAPTERS + " where " + Columns.ID + " =" + DatabaseIdentification.getChapterIDFromChapterURL(chapterURL), null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return Status.UNREAD;
            } else {
                cursor.moveToNext();
                int y = cursor.getInt(cursor.getColumnIndex(Columns.READ_CHAPTER.toString()));
                cursor.close();
                if (y == 0)
                    return Status.UNREAD;
                else if (y == 1)
                    return Status.READING;
                else
                    return Status.READ;
            }
        }

        /**
         * Sets chapter status
         *
         * @param chapterURL chapter to be set
         * @param status     status to be set
         */
        public static void setChapterStatus(String chapterURL, Status status) {
            sqLiteDatabase.execSQL("update " + Tables.CHAPTERS + " set " + Columns.READ_CHAPTER + "=" + status + " where " + Columns.ID + "=" + DatabaseIdentification.getChapterIDFromChapterURL(chapterURL));
        }

        /**
         * returns Y coordinate
         * Precondition is the chapter is already in the database
         *
         * @param chapterURL imageURL to the chapter
         * @return if bookmarked?
         */
        public static int getY(String chapterURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.Y + " from " + Tables.CHAPTERS + " where " + Columns.ID + " =" + DatabaseIdentification.getChapterIDFromChapterURL(chapterURL), null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return 0;
            } else {
                cursor.moveToNext();
                int y = cursor.getInt(cursor.getColumnIndex(Columns.Y.toString()));
                cursor.close();
                return y;
            }
        }

        /**
         * Sets bookmark true or false (1 for true, 0 is false)
         *
         * @param chapterURL chapter chapterURL
         * @param b          1 is true, 0 is false
         */
        public static void setBookMark(String chapterURL, int b) {
            sqLiteDatabase.execSQL("update " + Tables.CHAPTERS + " set " + Columns.BOOKMARKED + "=" + b + " where " + Columns.ID + "=" + DatabaseIdentification.getChapterIDFromChapterURL(chapterURL));

        }

        /**
         * is this chapter bookmarked?
         *
         * @param chapterURL imageURL to the chapter
         * @return if bookmarked?
         */
        public static boolean isBookMarked(String chapterURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.BOOKMARKED + " from " + Tables.CHAPTERS + " where " + Columns.ID + " =" + DatabaseIdentification.getChapterIDFromChapterURL(chapterURL), null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            } else {
                cursor.moveToNext();
                int y = cursor.getInt(cursor.getColumnIndex(Columns.BOOKMARKED.toString()));
                cursor.close();
                return y == 1;
            }

        }

        /**
         * Removes save path from chapter
         *
         * @param chapterURL chapter to remove save path of
         */
        public static void removePath(String chapterURL) {
            sqLiteDatabase.execSQL("update " + Tables.CHAPTERS + " set " + Columns.SAVE_PATH + "=null," + Columns.IS_SAVED + "=0 where " + Columns.ID + "=" + DatabaseIdentification.getChapterIDFromChapterURL(chapterURL));
        }

        /**
         * Adds save path
         *
         * @param chapterURL  chapter to update
         * @param chapterPath save path to set
         */
        public static void addSavedPath(String chapterURL, String chapterPath) {
            sqLiteDatabase.execSQL("update " + Tables.CHAPTERS + " set " + Columns.SAVE_PATH + "='" + chapterPath + "'," + Columns.IS_SAVED + "=1 where " + Columns.ID + "=" + DatabaseIdentification.getChapterIDFromChapterURL(chapterURL));
        }

        /**
         * Is the chapter saved
         *
         * @param chapterURL novelURL of the chapter
         * @return true if saved, false otherwise
         */
        public static boolean isSaved(String chapterURL) {
            //   Log.d("CheckSave", chapterURL);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.IS_SAVED + " from " + Tables.CHAPTERS + " where " + Columns.ID + "=" + DatabaseIdentification.getChapterIDFromChapterURL(chapterURL), null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                //   Log.d("CheckSave", chapterURL + " FALSE");
                return false;
            } else {
                cursor.moveToNext();
                int y = cursor.getInt(cursor.getColumnIndex(Columns.IS_SAVED.toString()));
                cursor.close();
                //         if (y == 1)
                //          Log.d("CheckSave", chapterURL + " TRUE");
                return y == 1;
            }
        }

        /**
         * Gets the novel from local storage
         *
         * @param chapterURL novelURL of the chapter
         * @return String of passage
         */
        public static String getSavedNovelPassage(String chapterURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.SAVE_PATH + " from " + Tables.CHAPTERS + " where " + Columns.ID + "=" + DatabaseIdentification.getChapterIDFromChapterURL(chapterURL), null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            } else {
                cursor.moveToNext();
                String savedData = cursor.getString(cursor.getColumnIndex(Columns.SAVE_PATH.toString()));
                cursor.close();
                return Download_Manager.getText(savedData);
            }
        }

        /**
         * If the chapter URL is present or not
         *
         * @param chapterURL chapter url
         * @return if present
         */
        public static boolean inChapters(String chapterURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.IS_SAVED + " from " + Tables.CHAPTERS + " where " + Columns.ID + " =" + DatabaseIdentification.getChapterIDFromChapterURL(chapterURL), null);
            int a = cursor.getCount();
            cursor.close();
            return !(a <= 0);
        }

        /**
         * Adds chapter to database
         *
         * @param novelID      ID of novel
         * @param novelChapter chapterURL
         */
        public static void addToChapters(int novelID, NovelChapter novelChapter) {
            if (!hasChapter(novelChapter.link))
                DatabaseIdentification.addChapter(novelID, novelChapter.link);

            String title = checkStringSerialize(novelChapter.title);
            String release = checkStringSerialize(novelChapter.release);

            try {
                sqLiteDatabase.execSQL("insert into " + Tables.CHAPTERS +
                        "(" +
                        Columns.ID + "," +
                        Columns.PARENT_ID + "," +
                        Columns.TITLE + "," +
                        Columns.RELEASE_DATE + "," +
                        Columns.Y + "," +
                        Columns.READ_CHAPTER + "," +
                        Columns.BOOKMARKED + "," +
                        Columns.IS_SAVED +
                        ") " +
                        "values" +
                        "(" +
                        DatabaseIdentification.getChapterIDFromChapterURL(novelChapter.link) + "," +
                        novelID + ",'" +
                        title + "','" +
                        release + "'," +
                        0 + "," + 0 + "," + 0 + "," + 0 +
                        ")");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static List<NovelChapter> getChapters(String novelURL) {
            int novelID = getNovelIDFromNovelURL(novelURL);
            return getChapters(novelID);
        }

        /**
         * Gets chapters of a novel
         *
         * @param novelID ID to retrieve from
         * @return List of chapters saved of novel
         */
        public static List<NovelChapter> getChapters(int novelID) {
            Cursor cursor = sqLiteDatabase.rawQuery("select " + Columns.ID + ", " + Columns.TITLE + ", " + Columns.RELEASE_DATE + " from " + Tables.CHAPTERS + " where " + Columns.PARENT_ID + " =" + novelID, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            } else {
                ArrayList<NovelChapter> novelChapters = new ArrayList<>();
                while (cursor.moveToNext()) {
                    try {
                        String url = DatabaseIdentification.getChapterURLFromChapterID(cursor.getInt(cursor.getColumnIndex(Columns.ID.toString())));

                        NovelChapter novelChapter = new NovelChapter();
                        novelChapter.title = checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString())));
                        novelChapter.link = url;
                        novelChapter.release = checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.RELEASE_DATE.toString())));
                        novelChapters.add(novelChapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
                return novelChapters;
            }
        }

        public static NovelChapter getChapter(String chapterURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("select " + Columns.ID + ", " + Columns.TITLE + ", " + Columns.RELEASE_DATE + " from " + Tables.CHAPTERS + " where " + Columns.ID + " =" + DatabaseIdentification.getChapterIDFromChapterURL(chapterURL), null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            } else {
                NovelChapter novelChapter = null;
                try {
                    novelChapter = new NovelChapter();
                    novelChapter.title = checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString())));
                    novelChapter.link = DatabaseIdentification.getChapterURLFromChapterID(cursor.getInt(cursor.getColumnIndex(Columns.ID.toString())));
                    novelChapter.release = checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.RELEASE_DATE.toString())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return novelChapter;
            }
        }
    }

    public static class DatabaseNovels {

        /**
         * Bookmarks the novel
         *
         * @param novelURL novelURL of the novel
         */
        public static void bookMark(@NotNull String novelURL) {
            sqLiteDatabase.execSQL("update " + Tables.NOVELS + " set " + Columns.BOOKMARKED + "=1 where " + Columns.PARENT_ID + "=" + getNovelIDFromNovelURL(novelURL));
        }

        /**
         * UnBookmarks the novel
         *
         * @param novelURL novelURL
         * @return if removed successfully
         */
        public static void unBookmark(@NotNull String novelURL) {
            sqLiteDatabase.execSQL("update " + Tables.NOVELS + " set " + Columns.BOOKMARKED + "=0 where " + Columns.ID + "=" + getNovelIDFromNovelURL(novelURL));
        }

        public static boolean isBookmarked(@NotNull String novelURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.BOOKMARKED + " from " + Tables.NOVELS + " where " + Columns.PARENT_ID + "=" + getNovelIDFromNovelURL(novelURL), null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
            cursor.moveToNext();
            System.out.println(Arrays.toString(cursor.getColumnNames()));
            int a = cursor.getInt(cursor.getColumnIndex("bookmarked"));
            cursor.close();
            return a > 0;
        }

        public static void setReaderType(@NotNull String novelURL, int reader) {
            sqLiteDatabase.execSQL("update " + Tables.NOVELS + " set " + Columns.READER_TYPE + "=" + reader + " where " + Columns.ID + "=" + getNovelIDFromNovelURL(novelURL));
        }

        /**
         * Gets reader type for novel
         *
         * @param novelURL Novel URL
         * @return -2 is no such novel, -1 is default, 0 is the same as -1, and 1+ is a specific reading type
         */
        public static int getReaderType(@NotNull String novelURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.READER_TYPE + " from " + Tables.NOVELS + " where " + Columns.PARENT_ID + "=" + getNovelIDFromNovelURL(novelURL), null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return -2;
            }
            cursor.moveToNext();
            System.out.println(Arrays.toString(cursor.getColumnNames()));
            int a = cursor.getInt(cursor.getColumnIndex(Columns.READER_TYPE.toString()));
            cursor.close();
            return a;
        }


        public static void addToLibrary(int formatter, @NotNull NovelPage novelPage, @NotNull String novelURL, int readingStatus) {
            addNovel(novelURL, formatter);
            int id = getNovelIDFromNovelURL(novelURL);
            String imageURL = novelPage.imageURL;
            if (imageURL == null)
                imageURL = "";

            try {
                sqLiteDatabase.execSQL("insert into " + Tables.NOVELS + "(" +
                        Columns.PARENT_ID + "," +
                        Columns.BOOKMARKED + "," +
                        Columns.READING_STATUS + "," +
                        Columns.READER_TYPE + "," +
                        Columns.TITLE + "," +
                        Columns.IMAGE_URL + "," +
                        Columns.DESCRIPTION + "," +
                        Columns.GENRES + "," +
                        Columns.AUTHORS + "," +
                        Columns.STATUS + "," +
                        Columns.TAGS + "," +
                        Columns.ARTISTS + "," +
                        Columns.LANGUAGE + "," +
                        Columns.MAX_CHAPTER_PAGE +
                        ")" + "values" + "(" +
                        id + "," +
                        0 + "," +
                        readingStatus + "," +
                        -1 + "," +
                        "'" + checkStringSerialize(novelPage.title) + "'," +
                        "'" + imageURL + "'," +
                        "'" + checkStringSerialize(novelPage.description) + "'," +
                        "'" + checkStringSerialize(convertArrayToString(novelPage.genres)) + "'," +
                        "'" + checkStringSerialize(convertArrayToString(novelPage.authors)) + "'," +
                        "'" + novelPage.status + "'," +
                        "'" + checkStringSerialize(convertArrayToString(novelPage.tags)) + "'," +
                        "'" + checkStringSerialize(convertArrayToString(novelPage.artists)) + "'," +
                        "'" + checkStringSerialize(novelPage.language) + "'," +
                        novelPage.maxChapterPage +
                        ")"
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * TODO Create a cache cleaner
         *
         * @param novelURL url of novel to remove
         * @return if successful
         */
        public static boolean removeFromLibrary(@NotNull String novelURL) {
            boolean a = sqLiteDatabase.delete(Tables.NOVELS.toString(), Columns.PARENT_ID + "=" + getNovelIDFromNovelURL(novelURL), null) > 0;
            boolean b = sqLiteDatabase.delete(Tables.NOVEL_IDENTIFICATION.toString(), Columns.ID + "=" + getNovelIDFromNovelURL(novelURL), null) > 0;
            return a && b;
        }

        /**
         * Is a novel in the library or not
         *
         * @param novelURL Novel novelURL
         * @return yes or no
         */
        public static boolean inLibrary(@NotNull String novelURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.ID + " from " + Tables.NOVEL_IDENTIFICATION + " where " + Columns.URL + " ='" + novelURL + "'", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
            cursor.close();
            return true;
        }

        /**
         * Get's the entire library to be listed
         *
         * @return the library
         */
        public static ArrayList<NovelCard> getLibrary() {
            Log.d("DL", "Getting");
            Cursor cursor = sqLiteDatabase.query(Tables.NOVELS.toString(),
                    new String[]{Columns.PARENT_ID.toString(), Columns.TITLE.toString(), Columns.IMAGE_URL.toString()},
                    Columns.BOOKMARKED + "=1", null, null, null, null);

            ArrayList<NovelCard> novelCards = new ArrayList<>();
            if (cursor.getCount() <= 0) {
                cursor.close();
                return new ArrayList<>();
            } else {
                while (cursor.moveToNext()) {
                    try {
                        int parent = cursor.getInt(cursor.getColumnIndex(Columns.PARENT_ID.toString()));
                        novelCards.add(new NovelCard(
                                checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString()))),
                                DatabaseIdentification.getNovelURLfromNovelID(parent),
                                cursor.getString(cursor.getColumnIndex(Columns.IMAGE_URL.toString())),
                                DatabaseIdentification.getFormatterIDFromNovelID(parent)
                        ));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
                return novelCards;
            }
        }

        public static NovelCard getNovel(String novelURL) {
            Log.d("DL", "Getting");
            int parent = getNovelIDFromNovelURL(novelURL);

            Cursor cursor = sqLiteDatabase.query(Tables.NOVELS.toString(),
                    new String[]{Columns.PARENT_ID.toString(), Columns.TITLE.toString(), Columns.IMAGE_URL.toString()},
                    Columns.BOOKMARKED + "=1 and " + Columns.PARENT_ID + "=" + parent, null, null, null, null);

            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            } else {
                cursor.moveToNext();
                try {
                    NovelCard novelCard = new NovelCard(
                            checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString()))),
                            novelURL,
                            checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.IMAGE_URL.toString()))),
                            DatabaseIdentification.getFormatterIDFromNovelID(parent)
                    );
                    cursor.close();
                    return novelCard;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        /**
         * Gets saved novelPage
         *
         * @param novelURL novelURL to retrieve
         * @return Saved novelPage
         */
        public static NovelPage getNovelPage(@NotNull String novelURL) {
            int ID = getNovelIDFromNovelURL(novelURL);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " +
                    Columns.TITLE + "," +
                    Columns.IMAGE_URL + "," +
                    Columns.DESCRIPTION + "," +
                    Columns.GENRES + "," +
                    Columns.AUTHORS + "," +
                    Columns.STATUS + "," +
                    Columns.TAGS + "," +
                    Columns.ARTISTS + "," +
                    Columns.LANGUAGE + "," +
                    Columns.MAX_CHAPTER_PAGE +
                    " from " + Tables.NOVELS + " where " + Columns.PARENT_ID + "=" + ID, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            } else {
                cursor.moveToNext();
                try {
                    NovelPage novelPage = new NovelPage();
                    novelPage.title = checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString())));
                    novelPage.imageURL = cursor.getString(cursor.getColumnIndex(Columns.IMAGE_URL.toString()));
                    novelPage.description = checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.DESCRIPTION.toString())));
                    novelPage.genres = convertStringToArray(checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.GENRES.toString()))));
                    novelPage.authors = convertStringToArray(checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.AUTHORS.toString()))));
                    novelPage.status = convertStringToStati(cursor.getString(cursor.getColumnIndex(Columns.STATUS.toString())));
                    novelPage.tags = convertStringToArray(checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.TAGS.toString()))));
                    novelPage.artists = convertStringToArray(checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.ARTISTS.toString()))));
                    novelPage.language = checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.LANGUAGE.toString())));
                    novelPage.maxChapterPage = cursor.getInt(cursor.getColumnIndex(Columns.MAX_CHAPTER_PAGE.toString()));
                    cursor.close();
                    return novelPage;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        public static void setStatus(@NotNull String novelURL, @NotNull Status status) {
            sqLiteDatabase.execSQL("update " + Tables.NOVELS + " set " + Columns.READING_STATUS + "=" + status + " where " + Columns.PARENT_ID + "='" + getNovelIDFromNovelURL(novelURL) + "'");
        }

        public static Status getStatus(@NotNull String novelURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.READING_STATUS + " from " + Tables.NOVELS + " where " + Columns.PARENT_ID + " =" + getNovelIDFromNovelURL(novelURL), null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return Status.UNREAD;
            } else {
                cursor.moveToNext();
                int y = cursor.getInt(cursor.getColumnIndex(Columns.READING_STATUS.toString()));
                cursor.close();
                if (y == 0)
                    return Status.UNREAD;
                else if (y == 1)
                    return Status.READING;
                else if (y == 2)
                    return Status.READ;
                else if (y == 3)
                    return Status.ONHOLD;
                else return Status.DROPPED;
            }
        }

        public static void updateData(@NotNull String novelURL, @NotNull NovelPage novelPage) {
            String imageURL = novelPage.imageURL;
            if (imageURL == null)
                imageURL = "";
            sqLiteDatabase.execSQL("update " + Tables.NOVELS + " set " +
                    Columns.TITLE + "='" + checkStringSerialize(novelPage.title) + "'," +
                    Columns.IMAGE_URL + "='" + imageURL + "'," +
                    Columns.DESCRIPTION + "='" + checkStringSerialize(novelPage.description) + "'," +
                    Columns.GENRES + "='" + checkStringSerialize(convertArrayToString(novelPage.genres)) + "'," +
                    Columns.AUTHORS + "='" + checkStringSerialize(convertArrayToString(novelPage.authors)) + "'," +
                    Columns.STATUS + "='" + novelPage.status + "'," +
                    Columns.TAGS + "='" + checkStringSerialize(convertArrayToString(novelPage.tags)) + "'," +
                    Columns.ARTISTS + "='" + checkStringSerialize(convertArrayToString(novelPage.artists)) + "'," +
                    Columns.LANGUAGE + "='" + checkStringSerialize(novelPage.language) + "'," +
                    Columns.MAX_CHAPTER_PAGE + "=" + novelPage.maxChapterPage +
                    " where " + Columns.PARENT_ID + "=" + getNovelIDFromNovelURL(novelURL));

        }

        public static void migrateNovel(@NotNull String oldURL, @NotNull String newURL, int formatterID, @NotNull NovelPage newNovel, int status) {
            unBookmark(oldURL);
            if (!DatabaseNovels.inLibrary(newURL))
                addToLibrary(formatterID, newNovel, newURL, status);
            bookMark(newURL);
        }

    }

    public static class DatabaseUpdates {

        public static DateTime trimDate(DateTime date) {
            Calendar cal = Calendar.getInstance();
            cal.clear(); // as per BalusC comment.
            cal.setTime(date.toDate());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return new DateTime(cal.getTimeInMillis());
        }

        public static int getTotalDays() {
            DateTime firstDay = new DateTime(getStartingDay());
            DateTime latest = new DateTime(getLatestDay());
            return Days.daysBetween(firstDay, latest).getDays();
        }

        public static long getStartingDay() {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.TIME + " FROM " + Tables.UPDATES + " ORDER BY ROWID ASC LIMIT 1", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return 0;
            } else {
                cursor.moveToNext();
                long day = cursor.getLong(cursor.getColumnIndex(Columns.TIME.toString()));
                cursor.close();
                return trimDate(new DateTime(day)).getMillis();
            }
        }

        public static long getLatestDay() {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.TIME + " FROM " + Tables.UPDATES + " ORDER BY ROWID DESC LIMIT 1", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return 0;
            } else {
                cursor.moveToNext();
                long day = cursor.getLong(cursor.getColumnIndex(Columns.TIME.toString()));
                cursor.close();
                return trimDate(new DateTime(day)).getMillis();
            }
        }

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

        /**
         * Gets count on day
         *
         * @param date1 first
         * @param date2 second
         */
        public static int getCountBetween(long date1, long date2) throws Exception {
            if (date2 <= date1)
                throw new Exception("Dates implemented wrongly");
            Cursor cursor = sqLiteDatabase.rawQuery(
                    "SELECT " + Columns.TIME + " from " + Tables.UPDATES +
                            " where " + Columns.TIME + "<" + date2 + " and " + Columns.TIME + ">=" + date1, null);
            int c = cursor.getCount();
            cursor.close();
            return c;
        }

        /**
         * Works as long as date2 is after date1
         *
         * @param date1 first
         * @param date2 second
         */
        public static ArrayList<Update> getTimeBetween(long date1, long date2) throws Exception {
            if (date2 <= date1)
                throw new Exception("Dates implemented wrongly");
            Log.i("UL", "Getting dates between [" + new DateTime(date1) + "] and [" + new DateTime(date2) + "]");
            Cursor cursor = sqLiteDatabase.rawQuery(
                    "SELECT " + Columns.PARENT_ID + "," + Columns.TIME + " from " + Tables.UPDATES +
                            " where " + Columns.TIME + "<" + date2 + " and " + Columns.TIME + ">=" + date1, null);


            ArrayList<Update> novelCards = new ArrayList<>();
            if (cursor.getCount() <= 0) {
                cursor.close();
                return new ArrayList<>();
            } else {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(Columns.PARENT_ID.toString()));
                    novelCards.add(new Update(
                            getNovelURLFromChapterID(id),
                            getChapterURLFromChapterID(id),
                            cursor.getLong(cursor.getColumnIndex(Columns.TIME.toString()))
                    ));
                }
                cursor.close();
                return novelCards;
            }
        }

        public static void addToUpdates(int novelID, @NotNull String chapterURL, long time) {
            sqLiteDatabase.execSQL("insert into " + Tables.UPDATES + "(" + Columns.ID + "," + Columns.PARENT_ID + "," + Columns.TIME + ") values(" +
                    getChapterIDFromChapterURL(chapterURL) + "," +
                    novelID + "," +
                    +time + ")");
        }

        public static boolean removeNovelFromUpdates(int novelID) {
            return sqLiteDatabase.delete(Tables.UPDATES.toString(), Columns.PARENT_ID + "=" + novelID, null) > 0;
        }

        public static boolean removeFromUpdates(@NotNull String chapterURL) {
            return sqLiteDatabase.delete(Tables.UPDATES.toString(), Columns.ID + "=" + getChapterIDFromChapterURL(chapterURL), null) > 0;
        }
    }
}
