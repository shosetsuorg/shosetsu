package com.github.doomsdayrs.apps.shosetsu.backend.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.Chapter;
import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.Download;
import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.Library;
import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.SUPERSERIALZIED;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
    public static SQLiteDatabase library;


    /**
     * Tables to work with
     */
    public enum Tables {
        NOVELS("novels"),
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
        STATUS("status");
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
        return objectInputStream.readObject();
    }

    public static boolean intToBoolean(int a) {
        return a == 1;
    }

    public static int booleanToInt(boolean a) {
        if (a)
            return 1;
        else return 0;
    }

    /**
     * Download control
     */
    public static class DatabaseDownloads {
        /**
         * Gets downloads that are stored
         *
         * @return DownloadItems to download
         */
        public static List<DownloadItem> getDownloadList() {
            ArrayList<DownloadItem> downloadItems = new ArrayList<>();
            Cursor cursor = library.rawQuery("SELECT " + Columns.FORMATTER_ID + "," + Columns.NOVEL_URL + "," + Columns.CHAPTER_URL + "," + Columns.NOVEL_NAME + "," + Columns.CHAPTER_NAME + " from " + Tables.DOWNLOADS + ";", null);
            while (cursor.moveToNext()) {
                String nURL = cursor.getString(cursor.getColumnIndex(Columns.NOVEL_URL.toString()));
                String cURL = cursor.getString(cursor.getColumnIndex(Columns.CHAPTER_URL.toString()));
                String nName = cursor.getString(cursor.getColumnIndex(Columns.NOVEL_NAME.toString()));
                String cName = cursor.getString(cursor.getColumnIndex(Columns.CHAPTER_NAME.toString()));
                int formatter = cursor.getInt(cursor.getColumnIndex(Columns.FORMATTER_ID.toString()));

                downloadItems.add(new DownloadItem(DefaultScrapers.formatters.get(formatter - 1), nName, cName, nURL, cURL));
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
            Cursor cursor = library.rawQuery("SELECT " + Columns.FORMATTER_ID + "," + Columns.NOVEL_URL + "," + Columns.CHAPTER_URL + "," + Columns.NOVEL_NAME + "," + Columns.CHAPTER_NAME + " from " + Tables.DOWNLOADS + " LIMIT 1;", null);
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
                return new DownloadItem(DefaultScrapers.formatters.get(formatter - 1), nName, cName, nURL, cURL);
            }
        }

        /**
         * Removes download item
         *
         * @param downloadItem download item to remove
         * @return if removed
         */
        public static boolean removeDownload(DownloadItem downloadItem) {
            return library.delete(Tables.DOWNLOADS.toString(), Columns.CHAPTER_URL + "='" + downloadItem.chapterURL + "'", null) > 0;
        }

        /**
         * Adds to download list
         *
         * @param downloadItem Download item to add
         */
        public static void addToDownloads(DownloadItem downloadItem) {
            library.execSQL("insert into " + Tables.DOWNLOADS + " (" +
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
            Cursor cursor = library.rawQuery("SELECT " + Columns.CHAPTER_URL + " from " + Tables.DOWNLOADS + " where " + Columns.CHAPTER_URL + " = '" + downloadItem.chapterURL + "'", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            } else {
                cursor.close();
                return true;
            }
        }

        /**
         * @return count of download items
         */
        public static int getDownloadCount() {
            Cursor cursor = library.rawQuery("select " + Columns.FORMATTER_ID + " from " + Tables.DOWNLOADS, null);
            int a = cursor.getCount();
            cursor.close();
            return a;
        }
    }

    /**
     * Chapter control
     */
    public static class DatabaseChapter {

        // TODO This will remove all chapter data
        public static void purgeCache() {

        }

        //TODO purge not needed cache
        public static void purgeUneededCache() {
        }

        /**
         * @param novelURL
         * @return Count of chapters left to read
         */
        public static int getCountOfChaptersUnread(String novelURL) {
            Cursor cursor = library.rawQuery("SELECT " + Columns.READ_CHAPTER + " from " + Tables.CHAPTERS + " where " + Columns.NOVEL_URL + "='" + novelURL + "'" + " and " + Columns.READ_CHAPTER + "!=" + Status.READ, null);
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
            library.execSQL("update " + Tables.CHAPTERS + " set " + Columns.Y + "='" + y + "' where " + Columns.CHAPTER_URL + "='" + chapterURL + "'");
        }


        /**
         * @param chapterURL chapter to check
         * @return returns chapter status
         */
        public static Status getStatus(String chapterURL) {
            Cursor cursor = library.rawQuery("SELECT " + Columns.READ_CHAPTER + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + " = '" + chapterURL + "'", null);
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
            library.execSQL("update " + Tables.CHAPTERS + " set " + Columns.READ_CHAPTER + "=" + status + " where " + Columns.CHAPTER_URL + "='" + chapterURL + "'");
        }

        /**
         * returns Y coordinate
         * Precondition is the chapter is already in the database
         *
         * @param chapterURL imageURL to the chapter
         * @return if bookmarked?
         */
        public static int getY(String chapterURL) {
            Cursor cursor = library.rawQuery("SELECT " + Columns.Y + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + " = '" + chapterURL + "'", null);
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
            library.execSQL("update " + Tables.CHAPTERS + " set " + Columns.BOOKMARKED + "=" + b + " where " + Columns.CHAPTER_URL + "='" + chapterURL + "'");

        }

        /**
         * is this chapter bookmarked?
         *
         * @param url imageURL to the chapter
         * @return if bookmarked?
         */
        public static boolean isBookMarked(String url) {
            Cursor cursor = library.rawQuery("SELECT " + Columns.BOOKMARKED + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + " = '" + url + "'", null);
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
            library.execSQL("update " + Tables.CHAPTERS + " set " + Columns.SAVE_PATH + "=null," + Columns.IS_SAVED + "=0 where " + Columns.CHAPTER_URL + "='" + chapterURL + "'");
        }

        /**
         * Adds save path
         *
         * @param chapterURL  chapter to update
         * @param chapterPath save path to set
         */
        public static void addSavedPath(String chapterURL, String chapterPath) {
            library.execSQL("update " + Tables.CHAPTERS + " set " + Columns.SAVE_PATH + "='" + chapterPath + "'," + Columns.IS_SAVED + "=1 where " + Columns.CHAPTER_URL + "='" + chapterURL + "'");
        }

        /**
         * Is the chapter saved
         *
         * @param chapterURL novelURL of the chapter
         * @return true if saved, false otherwise
         */
        public static boolean isSaved(String chapterURL) {
            //   Log.d("CheckSave", chapterURL);
            Cursor cursor = library.rawQuery("SELECT " + Columns.IS_SAVED + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + "='" + chapterURL + "'", null);
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
            Cursor cursor = library.rawQuery("SELECT " + Columns.SAVE_PATH + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + "='" + chapterURL + "'", null);
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
            Cursor cursor = library.rawQuery("SELECT " + Columns.IS_SAVED + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + " ='" + chapterURL + "'", null);
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
                library.execSQL("insert into " + Tables.CHAPTERS + "(" +
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
         * Gets chapters of a novel
         *
         * @param novelURL novel to retrieve from
         * @return List of chapters saved of novel
         */
        public static List<NovelChapter> getChapters(String novelURL) {
            Cursor cursor = library.rawQuery("select " + Columns.SAVED_DATA + " from " + Tables.CHAPTERS + " where " + Columns.NOVEL_URL + " ='" + novelURL + "'", null);
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
                return novelChapters;
            }
        }
    }

    public static class DatabaseLibrary {


        /**
         * Bookmarks the novel
         *
         * @param novelURL novelURL of the novel
         */
        public static void bookMark(String novelURL) {
            library.execSQL("update " + Tables.NOVELS + " set " + Columns.BOOKMARKED + "=1 where " + Columns.NOVEL_URL + "='" + novelURL + "'");
        }

        /**
         * UnBookmarks the novel
         *
         * @param novelURL novelURL
         * @return if removed successfully
         */
        public static void unBookmark(String novelURL) {
            library.execSQL("update " + Tables.NOVELS + " set " + Columns.BOOKMARKED + "=0 where " + Columns.NOVEL_URL + "='" + novelURL + "'");
        }

        public static boolean isBookmarked(String novelURL) {
            Cursor cursor = library.rawQuery("SELECT " + Columns.BOOKMARKED + " from " + Tables.NOVELS + " where " + Columns.NOVEL_URL + " ='" + novelURL + "'", null);
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


        public static void addToLibrary(int formatter, NovelPage novelPage, String novelURL, int status) {
            try {
                library.execSQL("insert into " + Tables.NOVELS + "(" +
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

        public static boolean removeFromLibrary(String novelURL) {
            return library.delete(Tables.NOVELS.toString(), Columns.NOVEL_URL + "='" + novelURL + "'", null) > 0;
        }

        /**
         * Is a novel in the library or not
         *
         * @param novelURL Novel novelURL
         * @return yes or no
         */
        public static boolean inLibrary(String novelURL) {
            Cursor cursor = library.rawQuery("SELECT " + Columns.FORMATTER_ID + " from " + Tables.NOVELS + " where " + Columns.NOVEL_URL + " ='" + novelURL + "'", null);
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
            Cursor cursor = library.query(Tables.NOVELS.toString(),
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
                return novelCards;
            }
        }

        /**
         * Gets saved novelPage
         *
         * @param novelURL novelURL to retrieve
         * @return Saved novelPage
         */
        public static NovelPage getNovelPage(String novelURL) {
            Cursor cursor = library.rawQuery("SELECT " + Columns.NOVEL_PAGE + " from " + Tables.NOVELS + " where " + Columns.NOVEL_URL + "='" + novelURL + "'", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            } else {
                cursor.moveToNext();
                try {
                    NovelPage novelPage = (NovelPage) deserialize(cursor.getString(cursor.getColumnIndex(Columns.NOVEL_PAGE.toString())));
                    cursor.close();
                    return novelPage;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        public static void setStatus(String novelURL, Status status) {
            library.execSQL("update " + Tables.NOVELS + " set " + Columns.STATUS + "=" + status + " where " + Columns.NOVEL_URL + "='" + novelURL + "'");
        }

        public static Status getStatus(String novelURL) {
            Cursor cursor = library.rawQuery("SELECT " + Columns.STATUS + " from " + Tables.NOVELS + " where " + Columns.NOVEL_URL + " = '" + novelURL + "'", null);
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

        public static void updateData(String novelURL, NovelPage novelPage) throws IOException {
            library.execSQL("update " + Tables.NOVELS + " set " + Columns.NOVEL_PAGE + "='" + serialize(novelPage) + "' where " + Columns.NOVEL_URL + "='" + novelURL + "'");
        }

        public static void migrateNovel(String oldURL, String newURL, int formatterID, NovelPage newNovel, int status) {
            unBookmark(oldURL);
            if (!Database.DatabaseLibrary.inLibrary(newURL))
                addToLibrary(formatterID, newNovel, newURL, status);
            bookMark(newURL);
        }
    }

    //TODO Restore backup
    // > If entry exists, simply update the data
    // > Popup window of restoring progress and errors



 public    static class restore extends AsyncTask<Void, Void, Void> {
        String file_path;
        Context context;

        restore(String file_path, Context context) {
            this.file_path = file_path;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            Log.i("Progress", "Started restore");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i("Progress", "Completed restore");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            File file = new File("" + file_path);
            if (file.exists()) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

                    StringBuilder string = new StringBuilder();
                    {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            string.append(line);
                        }
                        bufferedReader.close();
                    }
                    SUPERSERIALZIED superserialzied = (SUPERSERIALZIED) deserialize(string.toString());

                    Log.i("Progress", "Restoring downloads");
                    for (Download download : superserialzied.downloads) {
                        DownloadItem downloadItem = new DownloadItem(DefaultScrapers.formatters.get(download.FORMATTER_ID - 1), download.NOVEL_NAME, download.CHAPTER_NAME, download.NOVEL_URL, download.CHAPTER_URL);
                        if (!DatabaseDownloads.inDownloads(downloadItem))
                            DatabaseDownloads.addToDownloads(downloadItem);
                    }

                    Log.i("Progress", "Restoring libraries");
                    for (Library library : superserialzied.libraries) {
                        if (!DatabaseLibrary.inLibrary(library.NOVEL_URL))
                            DatabaseLibrary.addToLibrary(library.FORMATTER_ID, (NovelPage) deserialize(library.NOVEL_PAGE), library.NOVEL_URL, library.STATUS);

                        com.github.doomsdayrs.apps.shosetsu.variables.enums.Status status;
                        switch (library.STATUS) {
                            case 0:
                                status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD;
                                break;
                            case 1:
                                status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.READING;
                                break;
                            case 2:
                                status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.READ;
                                break;
                            case 3:
                                status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.ONHOLD;
                                break;
                            case 4:
                                status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.DROPPED;
                                break;
                            default:
                                status = null;
                                break;
                        }
                        if (status != null)
                            DatabaseLibrary.setStatus(library.NOVEL_URL, status);

                        if (library.BOOKMARKED)
                            DatabaseLibrary.bookMark(library.NOVEL_URL);
                    }

                    Log.i("Progress", "Restoring chapters");
                    for (Chapter chapter : superserialzied.chapters) {
                        if (!DatabaseChapter.inChapters(chapter.CHAPTER_URL))
                            DatabaseChapter.addToChapters(chapter.NOVEL_URL, (NovelChapter) deserialize(chapter.SAVED_DATA));

                        DatabaseChapter.updateY(chapter.CHAPTER_URL, chapter.Y);

                        com.github.doomsdayrs.apps.shosetsu.variables.enums.Status status;
                        switch (chapter.READ_CHAPTER) {
                            case 0:
                                status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD;
                                break;
                            case 1:
                                status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.READING;
                                break;
                            case 2:
                                status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.READ;
                                break;
                            case 3:
                                status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.ONHOLD;
                                break;
                            case 4:
                                status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.DROPPED;
                                break;
                            default:
                                status = null;
                                break;
                        }
                        if (status != null)
                            DatabaseChapter.setChapterStatus(chapter.CHAPTER_URL, status);

                        if (chapter.BOOKMARKED)
                            DatabaseChapter.setBookMark(chapter.CHAPTER_URL, 1);

                        //TODO settings backup
                    }

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    /**
     * Async progress of backup
     */
    public  static class backUP extends AsyncTask<Void, Void, Void> {
        Context context;

        public backUP(Context context) {
            this.context = context;
        }


        @Override
        protected void onPreExecute() {
            Log.i("Progress", "Starting backup");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i("Progress", "Finished backup");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                final SUPERSERIALZIED SUPER_SERIALIZED = new SUPERSERIALZIED();

                Log.i("Progress", "Backing up novels");
                // Library backup
                {
                    Cursor cursor = library.rawQuery("select * from " + Tables.NOVELS + " where " + Columns.BOOKMARKED + "=1", null);
                    if (!(cursor.getCount() <= 0))
                        while (cursor.moveToNext()) {
                            String nurl = cursor.getString(cursor.getColumnIndex(Columns.NOVEL_URL.toString()));
                            Log.i("NovelBack", nurl);
                            boolean bookmarked = intToBoolean(cursor.getInt(cursor.getColumnIndex(Columns.BOOKMARKED.toString())));
                            String npage = cursor.getString(cursor.getColumnIndex(Columns.NOVEL_PAGE.toString()));
                            int formatter_id = cursor.getInt(cursor.getColumnIndex(Columns.FORMATTER_ID.toString()));
                            int status = cursor.getInt(cursor.getColumnIndex(Columns.STATUS.toString()));

                            SUPER_SERIALIZED.libraries.add(new Library(nurl, bookmarked, npage, formatter_id, status));
                        }
                    cursor.close();
                }

                // TODO figure out how to prevent non library chapters from being saved, lowering size of backup
                Log.i("Progress", "Backing up Chapters");
                // Chapter backup
                {
                    Cursor cursor = library.rawQuery("select * from " + Tables.CHAPTERS, null);
                    if (!(cursor.getCount() <= 0))
                        while (cursor.moveToNext()) {
                            String nurl = cursor.getString(cursor.getColumnIndex(Columns.NOVEL_URL.toString()));
                            String curl = cursor.getString(cursor.getColumnIndex(Columns.CHAPTER_URL.toString()));
                            // very dirty logger
                            //Log.i("ChapterBack", curl);
                            String saved_data = cursor.getString(cursor.getColumnIndex(Columns.SAVED_DATA.toString()));
                            int y = cursor.getInt(cursor.getColumnIndex(Columns.Y.toString()));
                            int read_chapter = cursor.getInt(cursor.getColumnIndex(Columns.READ_CHAPTER.toString()));
                            boolean bookmarked = intToBoolean(cursor.getInt(cursor.getColumnIndex(Columns.BOOKMARKED.toString())));
                            boolean is_saved = intToBoolean(cursor.getInt(cursor.getColumnIndex(Columns.IS_SAVED.toString())));
                            String path = cursor.getString(cursor.getColumnIndex(Columns.SAVED_DATA.toString()));

                            SUPER_SERIALIZED.chapters.add(new Chapter(nurl, curl, saved_data, y, read_chapter, bookmarked, is_saved, path));
                        }
                    cursor.close();
                }

                Log.i("Progress", "Backing up Downloads");
                // Downloads backup
                {
                    Cursor cursor = library.rawQuery("select * from " + Tables.DOWNLOADS, null);
                    if (!(cursor.getCount() <= 0))
                        while (cursor.moveToNext()) {
                            String nurl = cursor.getString(cursor.getColumnIndex(Columns.NOVEL_URL.toString()));
                            String curl = cursor.getString(cursor.getColumnIndex(Columns.CHAPTER_URL.toString()));
                            String nname = cursor.getString(cursor.getColumnIndex(Columns.NOVEL_NAME.toString()));
                            String cname = cursor.getString(cursor.getColumnIndex(Columns.CHAPTER_NAME.toString()));
                            int formatter_id = cursor.getInt(cursor.getColumnIndex(Columns.FORMATTER_ID.toString()));
                            boolean paused = intToBoolean(cursor.getInt(cursor.getColumnIndex(Columns.PAUSED.toString())));

                            SUPER_SERIALIZED.downloads.add(new Download(nurl, curl, formatter_id, nname, cname, paused));
                        }
                    cursor.close();
                }

                Log.i("Progress", "Writing");
                File folder = new File(Download_Manager.shoDir + "/backup/");
                if (!folder.exists())
                    if (!folder.mkdirs()) {
                        throw new IOException("Failed to mkdirs");
                    }
                FileOutputStream fileOutputStream = new FileOutputStream(
                        (folder.getPath() + "/backup-" + (new Date().toString()) + ".shoback")
                );
                fileOutputStream.write(SUPER_SERIALIZED.serialize().getBytes());
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
