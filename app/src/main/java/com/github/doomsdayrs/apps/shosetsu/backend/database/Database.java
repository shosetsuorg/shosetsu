package com.github.doomsdayrs.apps.shosetsu.backend.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;

import com.github.Doomsdayrs.api.novelreader_core.main.DefaultScrapers;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.settings.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.variables.download.DownloadItem;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see https://www.gnu.org/licenses/ .
 * ====================================================================
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class Database {
    public static SQLiteDatabase library;


    public enum Tables {
        LIBRARY("library"),
        BOOKMARKS("bookmarks"),
        DOWNLOADS("downloads"),
        CHAPTERS("chapters");
        final String TABLE;

        Tables(String table) {
            this.TABLE = table;
        }

        @NotNull
        @Override
        public String toString() {
            return TABLE;
        }
    }

    public enum Columns {
        CHAPTER_URL("chapterURL"),
        NOVEL_URL("novelURL"),
        TITLE("title"),
        IMAGE_URL("imageURL"),
        AUTHORS("authors"),
        SAVED_DATA("savedData"),
        FORMATTER_ID("formatterID"),
        READ_CHAPTER("read"),
        Y("y"),
        BOOKMARKED("bookmarked"),
        IS_SAVED("isSaved"),
        SAVE_PATH("savePath"),
        NOVEL_NAME("novelName"),
        CHAPTER_NAME("chapterName");
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

    //TODO Figure out a legitimate way to structure all this data

    // Library that the user has saved their novels to
    static final String libraryCreate = "create TABLE if not exists " + Tables.LIBRARY + " (" +
            Columns.NOVEL_URL + " text not null unique, " +
            Columns.TITLE + " text not null," +
            Columns.IMAGE_URL + " text not null," +
            Columns.AUTHORS + " text not null," +
            Columns.FORMATTER_ID + " integer not null)";

    // If the user bookmarks a chapter, it is saved here
    // TODO merge with Tables.CHAPTERS, assign bool value
    static final String bookmarksCreate = "create TABLE if not exists " + Tables.BOOKMARKS + "(" +
            Columns.CHAPTER_URL + " text unique not null, " +
            Columns.SAVED_DATA + " text)";

    // If a user downloads a chapter, it is saved here
    // TODO, assign as columns for Tables.CHAPTERS
    static final String downloadsCreate = "create TABLE if not exists " + Tables.DOWNLOADS + "(" +
            Columns.FORMATTER_ID + " integer not null," +
            Columns.NOVEL_URL + " text not null," +
            Columns.CHAPTER_URL + " text not null," +

            Columns.NOVEL_NAME + " text not null," +
            Columns.CHAPTER_NAME + " text not null)";

    // Will be to new master table for chapters
    // TODO Convert this class to use this instead of the above
    static final String chaptersCreate = "create table if not exists " + Tables.CHAPTERS + "(" +
            Columns.NOVEL_URL + " text not null," +
            // The chapter chapterURL
            Columns.CHAPTER_URL + " text not null unique," +

            // Unsure if i should keep this or not
            Columns.SAVED_DATA + " text," +

            // Saved Data
            // > Scroll position, either 0 for top, or X for the position
            Columns.Y + " integer not null," +
            // > Either 0 for none, or an incremented count
            Columns.READ_CHAPTER + " integer not null," +
            // > Either 0 for false or 1 for true.
            Columns.BOOKMARKED + " integer not null," +

            // If 1 then true and SAVE_PATH has data, false otherwise
            Columns.IS_SAVED + " integer not null," +
            Columns.SAVE_PATH + " text)";


    // BOOKMARK CONTROLLERS

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

    public static boolean removeDownload(DownloadItem downloadItem) {
        return library.delete(Tables.DOWNLOADS.toString(), Columns.CHAPTER_URL + "='" + downloadItem.chapterURL + "'", null) > 0;
    }

    public static void addToDownloads(DownloadItem downloadItem) {
        library.execSQL("insert into " + Tables.DOWNLOADS + " (" + Columns.FORMATTER_ID + "," + Columns.NOVEL_URL + "," + Columns.CHAPTER_URL + "," + Columns.NOVEL_NAME + "," + Columns.CHAPTER_NAME + ") " +
                "values (" + downloadItem.formatter.getID() + ",'" + downloadItem.novelURL + "','" + downloadItem.chapterURL + "','" + DownloadItem.cleanse(downloadItem.novelName) + "','" + DownloadItem.cleanse(downloadItem.chapterName) + "')");
    }

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

    public static int getDownloadCount() {
        Cursor cursor = library.rawQuery("select " + Columns.FORMATTER_ID + " from " + Tables.DOWNLOADS, null);
        return cursor.getCount();
    }

    /**
     * Updates the Y coordinate
     * Precondition is the chapter is already in the database.
     *
     * @param chapterURL url to update
     * @param y          integer value scroll
     */
    public static void updateY(String chapterURL, int y) {
        library.execSQL("update " + Tables.CHAPTERS + " set " + Columns.Y + "='" + y + "' where " + Columns.CHAPTER_URL + "='" + chapterURL + "'");
    }

    public static Status isRead(String chapterURL) {
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

    public static void removePath(String chapterURL) {
        library.execSQL("update " + Tables.CHAPTERS + " set " + Columns.SAVE_PATH + "=null," + Columns.IS_SAVED + "=0 where " + Columns.CHAPTER_URL + "='" + chapterURL + "'");
    }

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
        Cursor cursor = library.rawQuery("SELECT " + Columns.IS_SAVED + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + "='" + chapterURL + "'", null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        } else {
            cursor.moveToNext();
            int y = cursor.getInt(cursor.getColumnIndex(Columns.IS_SAVED.toString()));
            cursor.close();
            return y == 1;
        }
    }

    /**
     * Gets the novel from local storage
     *
     * @param chapterURL novelURL of the chapter
     * @return String of passage
     */
    public static String getSaved(String chapterURL) {
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


    public static boolean inChapters(String chapterURL) {
        Cursor cursor = library.rawQuery("SELECT " + Columns.IS_SAVED + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + " ='" + chapterURL + "'", null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public static void addToChapters(String novelURL, String chapterURL) {
        library.execSQL("insert into " + Tables.CHAPTERS + "(" +
                Columns.NOVEL_URL + "," +
                Columns.CHAPTER_URL + "," +
                Columns.Y + "," +
                Columns.READ_CHAPTER + "," +
                Columns.BOOKMARKED + "," +
                Columns.IS_SAVED + ") values ('" +
                novelURL + "','" +
                chapterURL + "'," +
                0 + "," + 0 + "," + 0 + "," + 0 + ")");
    }

    /**
     * adds novel to the library TABLE
     *
     * @param novelPage novelPage
     * @param novelURL  novelURL of the novel
     * @return if successful
     */
    public static void addToLibrary(int formatter, NovelPage novelPage, String novelURL) {
        library.execSQL("insert into " + Tables.LIBRARY + "('" + Columns.NOVEL_URL + "'," + Columns.FORMATTER_ID + "," + Columns.TITLE + "," + Columns.IMAGE_URL + "," + Columns.AUTHORS + ") values(" +
                "'" + novelURL + "'," +
                "'" + formatter + "'," +
                "'" + novelPage.title.replaceAll("'", "") + "'," +
                "'" + novelPage.imageURL + "'," +
                "'" + Arrays.toString(novelPage.authors).replaceAll("'", "") + " ')"
        );
    }

    /**
     * Removes a novel from the library
     *
     * @param novelURL novelURL
     * @return if removed successfully
     */
    public static boolean removeFromLibrary(String novelURL) {
        return library.delete(Tables.LIBRARY.toString(), Columns.NOVEL_URL + "='" + novelURL + "'", null) > 0;
    }

    /**
     * Is a novel in the library or not
     *
     * @param novelURL Novel novelURL
     * @return yes or no
     */
    public static boolean inLibrary(String novelURL) {
        Cursor cursor = library.rawQuery("SELECT " + Columns.FORMATTER_ID + " from " + Tables.LIBRARY + " where " + Columns.NOVEL_URL + " ='" + novelURL + "'", null);
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
        Cursor cursor = library.query(Tables.LIBRARY.toString(),
                new String[]{Columns.TITLE.toString(), Columns.NOVEL_URL.toString(), Columns.IMAGE_URL.toString(), Columns.FORMATTER_ID.toString()},
                null, null, null, null, null);
        ArrayList<NovelCard> novelCards = new ArrayList<>();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return new ArrayList<>();
        } else {
            while (cursor.moveToNext()) {
                novelCards.add(new NovelCard(
                        cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString())),
                        cursor.getString(cursor.getColumnIndex(Columns.NOVEL_URL.toString())),
                        cursor.getString(cursor.getColumnIndex(Columns.IMAGE_URL.toString())),
                        cursor.getInt(cursor.getColumnIndex(Columns.FORMATTER_ID.toString()))
                ));
            }
            cursor.close();
            return novelCards;
        }
    }


    //TODO Restore backup
    // > If entry exists, simply update the data

    public static void backupDatabase() {
        new backUP().execute();
    }

    static class backUP extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Master object for database
                JSONObject master = new JSONObject();

                // Library backup
                {
                    JSONArray libraryArray = new JSONArray();
                    Cursor cursor = library.rawQuery("select * from " + Tables.LIBRARY, null);
                    if (!(cursor.getCount() <= 0))
                        while (cursor.moveToNext()) {
                            JSONObject a = new JSONObject();
                            a.put("novelURL", cursor.getString(cursor.getColumnIndex(Columns.NOVEL_URL.toString())));
                            a.put("title", cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString().replace("\"", "\\\""))));
                            a.put("imageURL", cursor.getString(cursor.getColumnIndex(Columns.IMAGE_URL.toString())));
                            a.put("authors", cursor.getString(cursor.getColumnIndex(Columns.AUTHORS.toString())));
                            a.put("formatterID", cursor.getString(cursor.getColumnIndex(Columns.FORMATTER_ID.toString())));
                            libraryArray.put(a);
                        }
                    master.put("library", libraryArray);
                    cursor.close();
                }

                // Chapter backup
                {
                    JSONArray chapterArray = new JSONArray();
                    Cursor cursor = library.rawQuery("select * from " + Tables.CHAPTERS, null);
                    if (!(cursor.getCount() <= 0))
                        while (cursor.moveToNext()) {
                            JSONObject a = new JSONObject();
                            a.put("novelURL", cursor.getString(cursor.getColumnIndex(Columns.NOVEL_URL.toString())));
                            a.put("chapterURL", cursor.getString(cursor.getColumnIndex(Columns.CHAPTER_URL.toString().replace("\"", "\\\""))));
                            a.put("savedData", cursor.getString(cursor.getColumnIndex(Columns.SAVED_DATA.toString())));
                            a.put("Y", cursor.getInt(cursor.getColumnIndex(Columns.Y.toString())));
                            a.put("readChapter", cursor.getInt(cursor.getColumnIndex(Columns.READ_CHAPTER.toString())));
                            a.put("bookmarked", cursor.getInt(cursor.getColumnIndex(Columns.BOOKMARKED.toString())));
                            a.put("isSaved", cursor.getString(cursor.getColumnIndex(Columns.IS_SAVED.toString())));
                            a.put("savePath", cursor.getString(cursor.getColumnIndex(Columns.SAVED_DATA.toString())));
                            chapterArray.put(a);
                        }
                    master.put("chapters", chapterArray);
                    cursor.close();
                }

                // Settings Backup
                {
                    JSONObject settingObject = new JSONObject();
                    // View
                    {
                        JSONObject viewSettings = new JSONObject();
                        viewSettings.put("textColor", SettingsController.view.getInt("ReaderTextColor", Color.BLACK));
                        viewSettings.put("backgroundColor", SettingsController.view.getInt("ReaderBackgroundColor", Color.WHITE));
                        settingObject.put("view", viewSettings);
                    }
                    // Download
                    {
                        JSONObject downloadSettings = new JSONObject();
                        downloadSettings.put("path", SettingsController.download.getString("dir", "/storage/emulated/0/Shosetsu/"));
                    }
                    master.put("settings", settingObject);
                }
                File folder = new File(Download_Manager.shoDir + "/backup/");
                if (!folder.exists())
                    if (!folder.mkdirs()) {
                        throw new IOException("Failed to mkdirs");
                    }
                FileOutputStream fileOutputStream = new FileOutputStream(
                        (folder.getPath() + "/backup-" + (new Date().toString()) + ".txt")
                );
                fileOutputStream.write(master.toString().getBytes());
                fileOutputStream.close();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
