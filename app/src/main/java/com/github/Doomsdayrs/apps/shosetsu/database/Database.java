package com.github.Doomsdayrs.apps.shosetsu.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.Doomsdayrs.apps.shosetsu.download.Downloadmanager;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.NovelCard;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

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
    static boolean ready = false;
    public static SQLiteDatabase library;

    public enum Tables {
        TABLE_LIBRARY("library"),
        TABLE_BOOKMARKS("bookmarks"),
        TABLE_DOWNLOADS("downloads");
        final String TABLE;

        Tables(String table) {
            this.TABLE = table;
        }

        @Override
        public String toString() {
            return TABLE;
        }
    }

    public enum Columns {
        COLUMN_CHAPTER_URL("chapterURL"),
        COLUMNS_NOVEL_URL("novelURL"),
        COLUMN_SAVED_DATA("savedData"),
        COLUMN_FORMATTER_ID("formatterID");
        final String COLUMN;

        Columns(String column) {
            this.COLUMN = column;
        }

        @Override
        public String toString() {
            return COLUMN;
        }
    }


    public static String create = "create TABLE if not exists " + Tables.TABLE_LIBRARY + " (" +
            Columns.COLUMNS_NOVEL_URL + " text not null unique, " +
            Columns.COLUMN_FORMATTER_ID + " integer not null, " +
            Columns.COLUMN_SAVED_DATA + " text not null)";

    public static String create2 = "create TABLE if not exists " + Tables.TABLE_BOOKMARKS + "(" +
            Columns.COLUMN_CHAPTER_URL + " text unique not null, " +
            Columns.COLUMN_SAVED_DATA + " text)";

    public static String create3 = "create TABLE if not exists " + Tables.TABLE_DOWNLOADS + "(" +
            Columns.COLUMNS_NOVEL_URL + " text not null," +
            Columns.COLUMN_CHAPTER_URL + " text not null, " +
            Columns.COLUMN_SAVED_DATA + " text)";

    // BOOKMARK CONTROLLERS

    /**
     * Adds a bookmark
     *
     * @param chapterURL imageURL of the novel
     * @param savedData  JSON object containing scroll position and others
     */
    public static boolean addBookMark(String chapterURL, JSONObject savedData) {
        library.execSQL("insert into " + Tables.TABLE_BOOKMARKS + " (" + Columns.COLUMN_CHAPTER_URL + "," + Columns.COLUMN_SAVED_DATA + ") values('" +
                chapterURL + "','" +
                savedData.toString() + "')"
        );
        return true;
    }

    public static void updateBookMark(String chapterURL, JSONObject savedData) {
        library.execSQL("update " + Tables.TABLE_BOOKMARKS + " set " + Columns.COLUMN_SAVED_DATA + "='" + savedData.toString() + "' where " + Columns.COLUMN_CHAPTER_URL + "='" + chapterURL + "'");
    }

    /**
     * Removes bookmark
     *
     * @param url imageURL to the chapter
     * @return if removed properly
     */
    public static boolean removeBookMarked(String url) {
        return library.delete(Tables.TABLE_BOOKMARKS.toString(), Columns.COLUMN_CHAPTER_URL + "='" + url + "'", null) > 0;
    }

    /**
     * is this chapter bookmarked?
     *
     * @param url imageURL to the chapter
     * @return if bookmarked?
     */
    public static boolean isBookMarked(String url) {
        Cursor cursor = library.rawQuery("SELECT * from " + Tables.TABLE_BOOKMARKS + " where " + Columns.COLUMN_CHAPTER_URL + " = '" + url + "'", null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    /**
     * Gets the bookmark object from the savedData column
     *
     * @param chapterURL Chapter to retrieve bookmark from
     * @return JSONObject of saved data
     */
    public static JSONObject getBookmarkObject(String chapterURL) {
        Cursor cursor = library.rawQuery("select " + Columns.COLUMN_SAVED_DATA + " from " + Tables.TABLE_BOOKMARKS + " where " + Columns.COLUMN_CHAPTER_URL + "='" + chapterURL + "'", null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return null;
        } else {
            try {
                cursor.moveToNext();
                JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex(Columns.COLUMN_SAVED_DATA.toString())));
                cursor.close();
                return jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    // LIBRARY CONTROLLERS

    public static boolean removePath(String novelURL, String chapterURL) {
        return library.delete(Tables.TABLE_DOWNLOADS.toString(), Columns.COLUMN_CHAPTER_URL + "='" + chapterURL + "' and " + Columns.COLUMNS_NOVEL_URL + "='" + novelURL + "'", null) > 0;
    }

    public static boolean addSavedPath(String novelURL, String chapterURL, String chapterPath) {
        library.execSQL("insert into " + Tables.TABLE_DOWNLOADS + " (" + Columns.COLUMNS_NOVEL_URL + "," + Columns.COLUMN_CHAPTER_URL + "," + Columns.COLUMN_SAVED_DATA + ") values('" +
                novelURL + "','" +
                chapterURL + "','" +
                chapterPath + "')"
        );
        return true;
    }


    /**
     * Is the chapter saved
     *
     * @param novelURL
     * @param chapterURL
     * @return
     */
    public static boolean isSaved(String novelURL, String chapterURL) {
        Cursor cursor = library.rawQuery("SELECT * from " + Tables.TABLE_DOWNLOADS + " where " + Columns.COLUMN_CHAPTER_URL + "='" + chapterURL + "' and " + Columns.COLUMNS_NOVEL_URL + "='" + novelURL + "'", null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    /**
     * Gets the novel from local storage
     *
     * @param novelURL   URL of the novel
     * @param chapterURL URL of the chapter
     * @return String of passage
     */
    public static String getSaved(String novelURL, String chapterURL) {
        Cursor cursor = library.rawQuery("SELECT * from " + Tables.TABLE_DOWNLOADS + " where " + Columns.COLUMN_CHAPTER_URL + "='" + chapterURL + "' and " + Columns.COLUMNS_NOVEL_URL + "='" + novelURL + "'", null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return null;
        } else {
            cursor.moveToNext();
            String savedData = cursor.getString(cursor.getColumnIndex(Columns.COLUMN_SAVED_DATA.toString()));
            cursor.close();
            return Downloadmanager.getText(savedData);
        }
    }

    /**
     * adds novel to the library TABLE
     *
     * @param novelPage novelPage
     * @param novelURL  novelURL of the novel
     * @param data      JSON of saved data
     * @return if successful
     */
    public static boolean addToLibrary(int formatter, NovelPage novelPage, String novelURL, JSONObject data) {
        try {
            data.put("title", novelPage.title.replaceAll("'", "\'"));
            data.put("authors", Arrays.toString(novelPage.authors).replaceAll("'", "\'"));
            data.put("imageURL", novelPage.imageURL);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
            return false;
        }
        library.execSQL("insert into " + Tables.TABLE_LIBRARY + "(" + Columns.COLUMNS_NOVEL_URL + "," + Columns.COLUMN_SAVED_DATA + "," + Columns.COLUMN_FORMATTER_ID + ") values('" +
                novelURL + "','" +
                data.toString().replaceAll("'","") + "'," +
                formatter + ")"
        );
        return true;
    }

    /**
     * Removes a novel from the library
     *
     * @param novelURL novelURL
     * @return if removed successfully
     */
    public static boolean removeFromLibrary(String novelURL) {
        return library.delete(Tables.TABLE_LIBRARY.toString(), Columns.COLUMNS_NOVEL_URL + "='" + novelURL + "'", null) > 0;
    }

    /**
     * Is a novel in the library or not
     *
     * @param novelURL Novel URL
     * @return yes or no
     */
    public static boolean inLibrary(String novelURL) {
        Cursor cursor = library.rawQuery("SELECT " + Columns.COLUMN_FORMATTER_ID + " from " + Tables.TABLE_LIBRARY + " where " + Columns.COLUMNS_NOVEL_URL + " ='" + novelURL + "'", null);
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
        Cursor cursor = library.query(Tables.TABLE_LIBRARY.toString(),
                new String[]{Columns.COLUMNS_NOVEL_URL.toString(), Columns.COLUMN_FORMATTER_ID.toString(), Columns.COLUMN_SAVED_DATA.toString()},
                null, null, null, null, null);
        ArrayList<NovelCard> novelCards = new ArrayList<>();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return new ArrayList<>();
        } else {
            while (cursor.moveToNext()) {
                try {
                    JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex(Columns.COLUMN_SAVED_DATA.toString())));
                    novelCards.add(
                            new NovelCard(
                                    jsonObject.getString("title"),
                                    cursor.getString(cursor.getColumnIndex(Columns.COLUMNS_NOVEL_URL.toString())),
                                    jsonObject.getString("imageURL"),
                                    cursor.getInt(cursor.getColumnIndex(Columns.COLUMN_FORMATTER_ID.toString()))
                            )
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
            return novelCards;
        }
    }
}
