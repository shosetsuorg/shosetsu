package com.github.doomsdayrs.apps.shosetsu.backend.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.deserializeNovelPageJSON;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.serializeOBJECT;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getNovelIDFromNovelURL;

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

    /**
     * Serialize object to string
     *
     * @param object object serialize
     * @return Serialised string
     * @throws IOException exception
     */
    public static String serializeToString(@NotNull Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    /**
     * Deserialize a string to the object
     *
     * @param string serialized string
     * @return Object from string
     * @throws IOException            exception
     * @throws ClassNotFoundException exception
     */
    public static Object deserializeString(@NotNull String string) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.decode(string, Base64.NO_WRAP);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object object = objectInputStream.readObject();
        return object;
    }

    /**
     * Checks string before deserialization
     * If null or empty, returns "". Else deserializes the string and returns
     *
     * @param string String to be checked
     * @return Completed String
     */
    public static String checkStringDeserialize(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        } else {
            try {
                return (String) deserializeString(string);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * Checks string before serialization
     * If null or empty, returns "". Else serializes the string and returns
     *
     * @param string String to be checked
     * @return Completed String
     */
    public static String checkStringSerialize(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        } else {
            try {
                return serializeToString(string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static class DatabaseIdentification {

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
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.ID + " from " + Tables.NOVEL_IDENTIFICATION + " where " + Columns.URL + " = " + url + "", null);
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
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.ID + " from " + Tables.CHAPTER_IDENTIFICATION + " where " + Columns.URL + " = " + url + "", null);
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
                String serial = cursor.getString(cursor.getColumnIndex(Columns.URL.toString()));
                cursor.close();
                try {
                    return (String) Database.deserializeString(serial);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
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
         * @param id NovelID
         * @return NovelURL
         */
        public static String getNovelURLfromNovelID(int id) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.URL + " from " + Tables.NOVEL_IDENTIFICATION + " where " + Columns.ID + " = " + id + "", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
            } else {
                cursor.moveToNext();
                String serial = cursor.getString(cursor.getColumnIndex(Columns.URL.toString()));
                cursor.close();
                try {
                    return (String) Database.deserializeString(serial);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
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
                int formatter = cursor.getInt(cursor.getColumnIndex(Columns.FORMATTER_ID.toString()));
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
                    Columns.FORMATTER_ID + "," +
                    Columns.PARENT_ID + "," +
                    Columns.NOVEL_NAME + "," +
                    Columns.CHAPTER_NAME + "," +
                    Columns.PAUSED + ") " +
                    "values (" +
                    downloadItem.formatter.getID() + "," +
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
            Cursor cursor = sqLiteDatabase.rawQuery("select " + Columns.FORMATTER_ID + " from " + Tables.DOWNLOADS, null);
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
         * @param novelURL     novelURL
         * @param novelChapter chapterURL
         */
        public static void addToChapters(String novelURL, NovelChapter novelChapter) {
            DatabaseIdentification.addChapter(getNovelIDFromNovelURL(novelURL), novelChapter.link);

            String title = checkStringSerialize(novelChapter.chapterNum);
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
                        getNovelIDFromNovelURL(novelURL) + "," +
                        title + "," +
                        release + "," +
                        0 + "," + 0 + "," + 0 + "," + 0 +
                        ")");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Gets chapters of a novel
         *
         * @param novelURL novel to retrieve from
         * @return List of chapters saved of novel
         */
        public static List<NovelChapter> getChapters(String novelURL) {
            int novelID = getNovelIDFromNovelURL(novelURL);
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
                        novelChapter.chapterNum = checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString())));
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
                    novelChapter.chapterNum = checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString())));
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
            sqLiteDatabase.execSQL("update " + Tables.NOVELS + " set " + Columns.BOOKMARKED + "=1 where " + Columns.ID + "=" + getNovelIDFromNovelURL(novelURL));
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
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.BOOKMARKED + " from " + Tables.NOVELS + " where " + Columns.ID + "=" + getNovelIDFromNovelURL(novelURL), null);
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

        public static void addToLibrary(int formatter, @NotNull NovelPage novelPage, @NotNull String novelURL, int status) {
            try {
                sqLiteDatabase.execSQL("insert into " + Tables.NOVELS + "(" +
                        Columns.BOOKMARKED + ",'" + Columns.NOVEL_URL + "'," + Columns.FORMATTER_ID + "," + Columns.NOVEL_PAGE + "," + Columns.STATUS + ") values(" +
                        "0" + "," +
                        "'" + novelURL + "'," +
                        "'" + formatter + "','" +
                        serializeOBJECT(novelPage) + "'," +
                        status + ")"
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
            return sqLiteDatabase.delete(Tables.NOVELS.toString(), Columns.NOVEL_URL + "='" + novelURL + "'", null) > 0;
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
                                checkStringDeserialize(cursor.getString(cursor.getColumnIndex(Columns.IMAGE_URL.toString()))),
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
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.NOVEL_PAGE + " from " + Tables.NOVELS + " where " + Columns.NOVEL_URL + "='" + novelURL + "'", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            } else {
                cursor.moveToNext();
                try {
                    NovelPage novelPage = deserializeNovelPageJSON(cursor.getString(cursor.getColumnIndex(Columns.NOVEL_PAGE.toString())));
                    cursor.close();
                    return novelPage;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        public static void setStatus(@NotNull String novelURL, @NotNull Status status) {
            sqLiteDatabase.execSQL("update " + Tables.NOVELS + " set " + Columns.STATUS + "=" + status + " where " + Columns.NOVEL_URL + "='" + novelURL + "'");
        }

        public static Status getStatus(@NotNull String novelURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.READING_STATUS + " from " + Tables.NOVELS + " where " + Columns.PARENT_ID + " =" + getNovelIDFromNovelURL(novelURL), null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return Status.UNREAD;
            } else {
                cursor.moveToNext();
                int y = cursor.getInt(cursor.getColumnIndex(Columns.STATUS.toString()));
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

        public static void updateData(@NotNull String novelURL, @NotNull NovelPage novelPage) throws Exception {
            sqLiteDatabase.execSQL("update " + Tables.NOVELS + " set " + Columns.NOVEL_PAGE + "='" + serializeOBJECT(novelPage) + "' where " + Columns.NOVEL_URL + "='" + novelURL + "'");
        }

        public static void migrateNovel(@NotNull String oldURL, @NotNull String newURL, int formatterID, @NotNull NovelPage newNovel, int status) {
            unBookmark(oldURL);
            if (!DatabaseNovels.inLibrary(newURL))
                addToLibrary(formatterID, newNovel, newURL, status);
            bookMark(newURL);
        }

        public static Formatter getFormat(String novelURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.FORMATTER_ID + " from " + Tables.NOVELS + " where " + Columns.NOVEL_URL + "='" + novelURL + "'", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            } else {
                cursor.moveToNext();
                Formatter formatter = DefaultScrapers.getByID(cursor.getInt(cursor.getColumnIndex(Columns.FORMATTER_ID.toString())));
                cursor.close();
                return formatter;
            }
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
                    "SELECT " + Columns.NOVEL_URL + "," + Columns.CHAPTER_URL + "," + Columns.TIME + " from " + Tables.UPDATES +
                            " where " + Columns.TIME + "<" + date2 + " and " + Columns.TIME + ">=" + date1, null);


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

        public static void addToUpdates(@NotNull String novelURL, @NotNull String chapterURL, long time) {
            sqLiteDatabase.execSQL("insert into " + Tables.UPDATES + "('" + Columns.NOVEL_URL + "','" + Columns.CHAPTER_URL + "'," + Columns.TIME + ") values(" +
                    "'" + novelURL + "'," +
                    "'" + chapterURL + "'," +
                    "" + time + ")");
        }

        public static boolean removeNovelFromUpdates(@NotNull String novelURL) {
            return sqLiteDatabase.delete(Tables.UPDATES.toString(), Columns.NOVEL_URL + "='" + novelURL + "'", null) > 0;
        }

        public static boolean removeFromUpdates(@NotNull String chapterURL) {
            return sqLiteDatabase.delete(Tables.UPDATES.toString(), Columns.CHAPTER_URL + "='" + chapterURL + "'", null) > 0;
        }
    }
}
