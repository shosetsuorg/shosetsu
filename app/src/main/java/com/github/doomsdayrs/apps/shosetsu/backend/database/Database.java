package com.github.doomsdayrs.apps.shosetsu.backend.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.Stati;
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
        MAX_PAGE("maxPage"),
        CHAPTER_URL("chapterURL"),
        NOVEL_URL("novelURL"),
        NOVEL_PAGE("novelPage"),
        SAVED_DATA("savedData"),
        FORMATTER_ID("formatterID"),
        READ_CHAPTER("read"),
        Y("y"),
        BOOKMARKED("bookmarked"),
        IS_SAVED("isSaved"),
        SAVE_PATH("savePath"),
        NOVEL_NAME("novelName"),
        CHAPTER_NAME("chapterName"),
        PAUSED("paused"),
        STATUS("status"),
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
    public static String serialize(Object object) throws IOException {
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
    public static Object deserialize(String string) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.decode(string, Base64.NO_WRAP);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object object = objectInputStream.readObject();
        Class c = object.getClass();
        if (c.equals(com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage.class)) {
            System.out.println("WARNING, OLD PAGE DETECTED.");
            NovelPage newPage = new NovelPage();
            com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage oldPage = (com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage) object;
            newPage.artists = oldPage.artists;
            newPage.authors = oldPage.artists;
            newPage.description = oldPage.description;
            newPage.genres = oldPage.genres;
            newPage.imageURL = oldPage.imageURL;
            newPage.language = oldPage.language;
            newPage.maxChapterPage = oldPage.maxChapterPage;
            newPage.novelChapters = new ArrayList<>();
            for (com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter C : oldPage.novelChapters) {
                NovelChapter newC = new NovelChapter();
                newC.chapterNum = C.chapterNum;
                newC.link = C.link;
                newC.release = C.release;
                newPage.novelChapters.add(newC);
            }
            switch (oldPage.status) {
                case PUBLISHING:
                    newPage.status = Stati.PUBLISHING;
                    break;
                case COMPLETED:
                    newPage.status = Stati.COMPLETED;
                    break;
                case PAUSED:
                    newPage.status = Stati.PAUSED;
                    break;
                case UNKNOWN:
                    newPage.status = Stati.UNKNOWN;
                    break;
            }
            newPage.tags = oldPage.tags;
            newPage.title = oldPage.title;
            return newPage;
        }
        return object;
    }

    public static class DatabaseDownloads {
        /**
         * Gets downloads that are stored
         *
         * @return DownloadItems to download
         */
        public static List<DownloadItem> getDownloadList() {
            ArrayList<DownloadItem> downloadItems = new ArrayList<>();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.FORMATTER_ID + "," + Columns.NOVEL_URL + "," + Columns.CHAPTER_URL + "," + Columns.NOVEL_NAME + "," + Columns.CHAPTER_NAME + " from " + Tables.DOWNLOADS + ";", null);
            while (cursor.moveToNext()) {
                String nURL = cursor.getString(cursor.getColumnIndex(Columns.NOVEL_URL.toString()));
                String cURL = cursor.getString(cursor.getColumnIndex(Columns.CHAPTER_URL.toString()));
                String nName = cursor.getString(cursor.getColumnIndex(Columns.NOVEL_NAME.toString()));
                String cName = cursor.getString(cursor.getColumnIndex(Columns.CHAPTER_NAME.toString()));
                int formatter = cursor.getInt(cursor.getColumnIndex(Columns.FORMATTER_ID.toString()));

                downloadItems.add(new DownloadItem(DefaultScrapers.getByID(formatter), nName, cName, nURL, cURL));
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
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.FORMATTER_ID + "," + Columns.NOVEL_URL + "," + Columns.CHAPTER_URL + "," + Columns.NOVEL_NAME + "," + Columns.CHAPTER_NAME + " from " + Tables.DOWNLOADS + " LIMIT 1;", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            } else {
                cursor.moveToNext();

                String nURL = cursor.getString(cursor.getColumnIndex(Columns.NOVEL_URL.toString()));
                String cURL = cursor.getString(cursor.getColumnIndex(Columns.CHAPTER_URL.toString()));
                String nName = cursor.getString(cursor.getColumnIndex(Columns.NOVEL_NAME.toString()));
                String cName = cursor.getString(cursor.getColumnIndex(Columns.CHAPTER_NAME.toString()));
                int formatter = cursor.getInt(cursor.getColumnIndex(Columns.FORMATTER_ID.toString()));
                cursor.close();
                return new DownloadItem(DefaultScrapers.getByID(formatter), nName, cName, nURL, cURL);
            }
        }

        /**
         * Removes download item
         *
         * @param downloadItem download item to remove
         * @return if removed
         */
        public static boolean removeDownload(DownloadItem downloadItem) {
            return sqLiteDatabase.delete(Tables.DOWNLOADS.toString(), Columns.CHAPTER_URL + "='" + downloadItem.chapterURL + "'", null) > 0;
        }

        /**
         * Adds to download list
         *
         * @param downloadItem Download item to add
         */
        public static void addToDownloads(DownloadItem downloadItem) {
            sqLiteDatabase.execSQL("insert into " + Tables.DOWNLOADS + " (" +
                    Columns.FORMATTER_ID + "," +
                    Columns.NOVEL_URL + "," +
                    Columns.CHAPTER_URL + "," +
                    Columns.NOVEL_NAME + "," +
                    Columns.CHAPTER_NAME + "," +
                    Columns.PAUSED + ") " +
                    "values (" +
                    downloadItem.formatter.getID() + ",'" +
                    downloadItem.novelURL + "','" +
                    downloadItem.chapterURL + "','" +
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
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.CHAPTER_URL + " from " + Tables.DOWNLOADS + " where " + Columns.CHAPTER_URL + " = '" + downloadItem.chapterURL + "'", null);
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
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.READ_CHAPTER + " from " + Tables.CHAPTERS + " where " + Columns.NOVEL_URL + "='" + novelURL + "'" + " and " + Columns.READ_CHAPTER + "!=" + Status.READ, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return 0;
            } else {
                cursor.moveToNext();
                int count = cursor.getCount();
                cursor.close();
                return count;
            }
        }


        /**
         * Updates the Y coordinate
         * Precondition is the chapter is already in the database.
         *
         * @param chapterURL novelURL to update
         * @param y          integer value scroll
         */
        public static void updateY(String chapterURL, int y) {
            Log.i("updateY", chapterURL + " := " + y);
            sqLiteDatabase.execSQL("update " + Tables.CHAPTERS + " set " + Columns.Y + "='" + y + "' where " + Columns.CHAPTER_URL + "='" + chapterURL + "'");
        }


        /**
         * @param chapterURL chapter to check
         * @return returns chapter status
         */
        public static Status getStatus(String chapterURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.READ_CHAPTER + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + " = '" + chapterURL + "'", null);
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
            sqLiteDatabase.execSQL("update " + Tables.CHAPTERS + " set " + Columns.READ_CHAPTER + "=" + status + " where " + Columns.CHAPTER_URL + "='" + chapterURL + "'");
        }

        /**
         * returns Y coordinate
         * Precondition is the chapter is already in the database
         *
         * @param chapterURL imageURL to the chapter
         * @return if bookmarked?
         */
        public static int getY(String chapterURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.Y + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + " = '" + chapterURL + "'", null);
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
            sqLiteDatabase.execSQL("update " + Tables.CHAPTERS + " set " + Columns.BOOKMARKED + "=" + b + " where " + Columns.CHAPTER_URL + "='" + chapterURL + "'");

        }

        /**
         * is this chapter bookmarked?
         *
         * @param url imageURL to the chapter
         * @return if bookmarked?
         */
        public static boolean isBookMarked(String url) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.BOOKMARKED + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + " = '" + url + "'", null);
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
            sqLiteDatabase.execSQL("update " + Tables.CHAPTERS + " set " + Columns.SAVE_PATH + "=null," + Columns.IS_SAVED + "=0 where " + Columns.CHAPTER_URL + "='" + chapterURL + "'");
        }

        /**
         * Adds save path
         *
         * @param chapterURL  chapter to update
         * @param chapterPath save path to set
         */
        public static void addSavedPath(String chapterURL, String chapterPath) {
            sqLiteDatabase.execSQL("update " + Tables.CHAPTERS + " set " + Columns.SAVE_PATH + "='" + chapterPath + "'," + Columns.IS_SAVED + "=1 where " + Columns.CHAPTER_URL + "='" + chapterURL + "'");
        }

        /**
         * Is the chapter saved
         *
         * @param chapterURL novelURL of the chapter
         * @return true if saved, false otherwise
         */
        public static boolean isSaved(String chapterURL) {
            //   Log.d("CheckSave", chapterURL);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.IS_SAVED + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + "='" + chapterURL + "'", null);
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
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.SAVE_PATH + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + "='" + chapterURL + "'", null);
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
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.IS_SAVED + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + " ='" + chapterURL + "'", null);
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
            try {
                sqLiteDatabase.execSQL("insert into " + Tables.CHAPTERS + "(" +
                        Columns.NOVEL_URL + "," +
                        Columns.CHAPTER_URL + "," +
                        Columns.SAVED_DATA + "," +
                        Columns.Y + "," +
                        Columns.READ_CHAPTER + "," +
                        Columns.BOOKMARKED + "," +
                        Columns.IS_SAVED + ") " +
                        "values ('" +
                        novelURL + "','" +
                        novelChapter.link + "','" +
                        serialize(novelChapter) + "'," +
                        0 + "," + 0 + "," + 0 + "," + 0 + ")");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Adds chapter to database
         *
         * @param novelURL novelURL
         */
        public static void addToChapters(String novelURL, String chapterURL, String chapter) {
            sqLiteDatabase.execSQL("insert into " + Tables.CHAPTERS + "(" +
                    Columns.NOVEL_URL + "," +
                    Columns.CHAPTER_URL + "," +
                    Columns.SAVED_DATA + "," +
                    Columns.Y + "," +
                    Columns.READ_CHAPTER + "," +
                    Columns.BOOKMARKED + "," +
                    Columns.IS_SAVED + ") " +
                    "values ('" +
                    novelURL + "','" +
                    chapterURL + "','" +
                    chapter + "'," +
                    0 + "," + 0 + "," + 0 + "," + 0 + ")");
        }


        /**
         * Gets chapters of a novel
         *
         * @param novelURL novel to retrieve from
         * @return List of chapters saved of novel
         */
        public static List<NovelChapter> getChapters(String novelURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("select " + Columns.SAVED_DATA + " from " + Tables.CHAPTERS + " where " + Columns.NOVEL_URL + " ='" + novelURL + "'", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            } else {
                ArrayList<NovelChapter> novelChapters = new ArrayList<>();
                while (cursor.moveToNext()) {
                    try {
                        String text = cursor.getString(cursor.getColumnIndex(Columns.SAVED_DATA.toString()));
                        if (text != null) {
                            novelChapters.add((NovelChapter) deserialize(text));
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
                return novelChapters;
            }
        }

        public static NovelChapter getChapter(String chapterURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("select " + Columns.SAVED_DATA + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + " ='" + chapterURL + "'", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            } else {
                NovelChapter novelChapters = null;
                try {
                    cursor.moveToNext();
                    String text = cursor.getString(cursor.getColumnIndex(Columns.SAVED_DATA.toString()));
                    cursor.close();
                    if (text != null) {
                        novelChapters = ((NovelChapter) deserialize(text));
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return novelChapters;
            }
        }

        public static String getChapterNovelURL(String chapterURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("select " + Columns.NOVEL_URL + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + " ='" + chapterURL + "'", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            } else {
                cursor.moveToNext();
                String text = cursor.getString(cursor.getColumnIndex(Columns.NOVEL_URL.toString()));
                cursor.close();
                return text;
            }
        }
    }


    public static class DatabaseLibrary {


        /**
         * Bookmarks the novel
         *
         * @param novelURL novelURL of the novel
         */
        public static void bookMark(@NotNull String novelURL) {
            sqLiteDatabase.execSQL("update " + Tables.NOVELS + " set " + Columns.BOOKMARKED + "=1 where " + Columns.NOVEL_URL + "='" + novelURL + "'");
        }

        /**
         * UnBookmarks the novel
         *
         * @param novelURL novelURL
         * @return if removed successfully
         */
        public static void unBookmark(@NotNull String novelURL) {
            sqLiteDatabase.execSQL("update " + Tables.NOVELS + " set " + Columns.BOOKMARKED + "=0 where " + Columns.NOVEL_URL + "='" + novelURL + "'");
        }

        public static boolean isBookmarked(@NotNull String novelURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.BOOKMARKED + " from " + Tables.NOVELS + " where " + Columns.NOVEL_URL + " ='" + novelURL + "'", null);
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
                        serialize(novelPage) + "'," +
                        status + ")"
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void addToLibrary(int formatter, @NotNull String novelPage, @NotNull String novelURL, int status) {
            sqLiteDatabase.execSQL("insert into " + Tables.NOVELS + "(" +
                    Columns.BOOKMARKED + ",'" + Columns.NOVEL_URL + "'," + Columns.FORMATTER_ID + "," + Columns.NOVEL_PAGE + "," + Columns.STATUS + ") values(" +
                    "0" + "," +
                    "'" + novelURL + "'," +
                    "'" + formatter + "','" +
                    novelPage + "'," +
                    status + ")"
            );
        }

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
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.FORMATTER_ID + " from " + Tables.NOVELS + " where " + Columns.NOVEL_URL + " ='" + novelURL + "'", null);
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
                    new String[]{Columns.NOVEL_URL.toString(), Columns.FORMATTER_ID.toString(), Columns.NOVEL_PAGE.toString()},
                    Columns.BOOKMARKED + "=1", null, null, null, null);

            ArrayList<NovelCard> novelCards = new ArrayList<>();
            if (cursor.getCount() <= 0) {
                cursor.close();
                return new ArrayList<>();
            } else {
                while (cursor.moveToNext()) {
                    try {
                        NovelPage novelPage = (NovelPage) deserialize(cursor.getString(cursor.getColumnIndex(Columns.NOVEL_PAGE.toString())));
                        novelCards.add(new NovelCard(
                                novelPage.title,
                                cursor.getString(cursor.getColumnIndex(Columns.NOVEL_URL.toString())),
                                novelPage.imageURL,
                                cursor.getInt(cursor.getColumnIndex(Columns.FORMATTER_ID.toString()))
                        ));
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
                return novelCards;
            }
        }

        public static NovelCard getNovel(String novelURL) {
            Log.d("DL", "Getting");
            Cursor cursor = sqLiteDatabase.query(Tables.NOVELS.toString(),
                    new String[]{Columns.NOVEL_URL.toString(), Columns.FORMATTER_ID.toString(), Columns.NOVEL_PAGE.toString()},
                    Columns.BOOKMARKED + "=1 and " + Columns.NOVEL_URL + "='" + novelURL + "'", null, null, null, null);

            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            } else {
                cursor.moveToNext();
                try {
                    NovelPage novelPage = (NovelPage) deserialize(cursor.getString(cursor.getColumnIndex(Columns.NOVEL_PAGE.toString())));
                    NovelCard novelCard = new NovelCard(
                            novelPage.title,
                            cursor.getString(cursor.getColumnIndex(Columns.NOVEL_URL.toString())),
                            novelPage.imageURL,
                            cursor.getInt(cursor.getColumnIndex(Columns.FORMATTER_ID.toString()))
                    );
                    cursor.close();
                    return novelCard;
                } catch (IOException | ClassNotFoundException e) {
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
                    NovelPage novelPage = (NovelPage) deserialize(cursor.getString(cursor.getColumnIndex(Columns.NOVEL_PAGE.toString())));
                    cursor.close();
                    return novelPage;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        public static void setStatus(@NotNull String novelURL, @NotNull Status status) {
            sqLiteDatabase.execSQL("update " + Tables.NOVELS + " set " + Columns.STATUS + "=" + status + " where " + Columns.NOVEL_URL + "='" + novelURL + "'");
        }

        public static Status getStatus(@NotNull String novelURL) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + Columns.STATUS + " from " + Tables.NOVELS + " where " + Columns.NOVEL_URL + " = '" + novelURL + "'", null);
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

        public static void updateData(@NotNull String novelURL, @NotNull NovelPage novelPage) throws IOException {
            sqLiteDatabase.execSQL("update " + Tables.NOVELS + " set " + Columns.NOVEL_PAGE + "='" + serialize(novelPage) + "' where " + Columns.NOVEL_URL + "='" + novelURL + "'");
        }

        public static void migrateNovel(@NotNull String oldURL, @NotNull String newURL, int formatterID, @NotNull NovelPage newNovel, int status) {
            unBookmark(oldURL);
            if (!Database.DatabaseLibrary.inLibrary(newURL))
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

        /**
         * Works as long as date2 is after date1
         *
         * @param date1 first
         * @param date2 second
         */
        public static ArrayList<Update> getTimeBetween(long date1, long date2) throws Exception {
            if (date2 <= date1)
                throw new Exception("Dates implemented wrongly");
            Log.d("ULDates", "" + date1 + "-" + date2);
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
