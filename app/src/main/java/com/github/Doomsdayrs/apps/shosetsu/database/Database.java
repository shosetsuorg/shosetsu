package com.github.Doomsdayrs.apps.shosetsu.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
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

    public static final String TABLE_LIBRARY = "library";
    public static final String TABLE_BOOKMARKS = "bookmarks";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_SAVED_DATA = "savedData";
    public static final String COLUMN_FORMATTER_ID = "formatterID";

    // > library
    // novelID is an self affiliated ID for the novel in storage
    // imageURL is the imageURL to the specific novel
    // savedData is a json containing the title(s), author(s), artist(s), paths to downloaded chapters, and more
    // userData includes read chapters, and others
    public static String create = "create table if not exists " + TABLE_LIBRARY + " (" +
            "novelID integer not null primary key autoincrement, " +
            COLUMN_URL + " text not null unique, " +
            COLUMN_FORMATTER_ID + " integer not null, " +
            "savedData text not null, " +
            "userData text)";

    // > bookmarks
    public static String create2 = "create table if not exists " + TABLE_BOOKMARKS + "(" +
            "novelID integer not null primary key, " +
            COLUMN_URL + " text unique not null, " +
            COLUMN_SAVED_DATA + " text)";

    /**
     * Adds a bookmark
     *
     * @param url       imageURL of the novel
     * @param savedData JSON object containing scroll position and others
     */
    public static boolean addBookMark(String url, JSONObject savedData) {
        if (library != null) {
            library.execSQL("insert into " + TABLE_BOOKMARKS + " (" + COLUMN_URL + "," + COLUMN_SAVED_DATA + ") values('" +
                    url + "','" +
                    savedData.toString() + "')"
            );
            return true;
        } else {
            Log.e("Database", "isNULL");
            return false;
        }
    }

    /**
     * Removes bookmark
     *
     * @param url imageURL to the chapter
     * @return if removed properly
     */
    public static boolean removeBookMarked(String url) {
        if (library != null)
            return library.delete(TABLE_BOOKMARKS, COLUMN_URL + "='" + url + "'", null) > 0;
        else {
            Log.e("Database", "isNULL");
            return false;
        }
    }

    /**
     * is this chapter bookmarked?
     *
     * @param url imageURL to the chapter
     * @return if bookmarked?
     */
    public static boolean isBookMarked(String url) {
        if (library != null) {
            Cursor cursor = library.rawQuery("SELECT * from " + TABLE_BOOKMARKS + " where " + COLUMN_URL + " = '" + url + "'", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
            cursor.close();
            return true;
        } else {
            Log.e("Database", "isNULL");
            return false;
        }
    }

    public static int getBookmarkObject(String chapterURL) {
        Cursor cursor = library.query(TABLE_BOOKMARKS, new String[]{COLUMN_SAVED_DATA}, null, null, null, null, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return 0;
        } else {
            try {
                cursor.moveToNext();
                JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex(COLUMN_SAVED_DATA)));
                cursor.close();
                return  jsonObject.getInt("y");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    /**
     * adds novel to the library table
     *
     * @param novelPage novelPage
     * @param novelURL  novelURL of the novel
     * @param data      JSON of saved data
     * @return if successful
     */
    public static boolean addToLibrary(int formatter, NovelPage novelPage, String novelURL, JSONObject data) {
        Log.d("addToLibrary imageURL", novelURL);
        try {
            data.put("title", novelPage.title.replaceAll("'", "\'"));
            data.put("authors", Arrays.toString(novelPage.authors).replaceAll("'", "\'"));
            Log.d("IMAGEURL", novelPage.imageURL);
            data.put("imageURL", novelPage.imageURL);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
            return false;
        }


        if (library != null) {
            library.execSQL("insert into " + TABLE_LIBRARY + "(" + COLUMN_URL + "," + COLUMN_SAVED_DATA + "," + COLUMN_FORMATTER_ID + ") values(" +
                    "'" + novelURL + "','" +
                    data.toString() + "'," +
                    formatter + ")"
            );
            return true;
        } else {
            Log.e("Database", "isNULL");
            return false;
        }
    }

    public static boolean removeFromLibrary(String novelURL) {
        Log.d("removeFromLibrary imageURL", novelURL);
        if (library != null)
            return library.delete(TABLE_LIBRARY, COLUMN_URL + "='" + novelURL + "'", null) > 0;
        else {
            Log.e("Database", "isNULL");
            return false;
        }
    }

    public static boolean inLibrary(String url) {
        Log.d("inLibrary imageURL", url);
        if (library != null) {
            Cursor cursor = library.rawQuery("SELECT * from " + TABLE_LIBRARY + " where " + COLUMN_URL + " ='" + url + "'", null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
            cursor.close();
            return true;
        } else {
            Log.e("Database", "isNULL");
            return false;
        }
    }

    public static ArrayList<NovelCard> getLibrary() {
        Cursor cursor = library.query(TABLE_LIBRARY, new String[]{COLUMN_URL, COLUMN_FORMATTER_ID, COLUMN_SAVED_DATA}, null, null, null, null, null);
        ArrayList<NovelCard> novelCards = new ArrayList<>();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return new ArrayList<>();
        } else {
            while (cursor.moveToNext()) {
                try {
                    JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex(COLUMN_SAVED_DATA)));
                    novelCards.add(new NovelCard(jsonObject.getString("title"), cursor.getString(cursor.getColumnIndex(COLUMN_URL)), jsonObject.getString("imageURL"), cursor.getInt(cursor.getColumnIndex(COLUMN_FORMATTER_ID))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
            return novelCards;
        }
    }
}
