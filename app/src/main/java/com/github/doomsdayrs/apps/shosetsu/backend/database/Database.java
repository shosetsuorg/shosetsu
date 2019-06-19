package com.github.doomsdayrs.apps.shosetsu.backend.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
        SAVED_DATA("savedData"),
        FORMATTER_ID("formatterID"),
        Y("y"),
        IS_SAVED("isSaved"),
        SAVE_PATH("savePath");
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
            Columns.FORMATTER_ID + " integer not null, " +
            Columns.SAVED_DATA + " text not null)";

    // If the user bookmarks a chapter, it is saved here
    // TODO merge with Tables.CHAPTERS, assign bool value
    static final String bookmarksCreate = "create TABLE if not exists " + Tables.BOOKMARKS + "(" +
            Columns.CHAPTER_URL + " text unique not null, " +
            Columns.SAVED_DATA + " text)";

    // If a user downloads a chapter, it is saved here
    // TODO, assign as columns for Tables.CHAPTERS
    static final String downloadsCreate = "create TABLE if not exists " + Tables.DOWNLOADS + "(" +
            Columns.NOVEL_URL + " text not null," +
            Columns.CHAPTER_URL + " text not null, " +
            Columns.SAVED_DATA + " text)";

    // Will be to new master table for chapters
    // TODO Convert this class to use this instead of the above
    static final String chaptersCreate = "create table if not exists " + Tables.CHAPTERS + "(" +
            // The chapter URL
            Columns.CHAPTER_URL + " text not null unique," +

            // Unsure if i should keep this or not
            Columns.SAVED_DATA + " text" +

            // Scroll position, either 0 for top, or X for the position
            Columns.Y + " integer not null," +

            // If is saved, save path will have the file path
            Columns.IS_SAVED + " integer not null" +
            Columns.SAVE_PATH + " text)";


    // BOOKMARK CONTROLLERS

    /**
     * Adds a bookmark
     *
     * @param chapterURL imageURL of the novel
     * @param savedData  JSON object containing scroll position and others
     */
    public static void addBookMark(String chapterURL, JSONObject savedData) {
        library.execSQL("insert into " + Tables.BOOKMARKS + " (" + Columns.CHAPTER_URL + "," + Columns.SAVED_DATA + ") values('" +
                chapterURL + "','" +
                savedData.toString() + "')"
        );
    }

    public static void updateBookMark(String chapterURL, JSONObject savedData) {
        library.execSQL("update " + Tables.BOOKMARKS + " set " + Columns.SAVED_DATA + "='" + savedData.toString() + "' where " + Columns.CHAPTER_URL + "='" + chapterURL + "'");
    }

    /**
     * Removes bookmark
     *
     * @param url imageURL to the chapter
     * @return if removed properly
     */
    public static boolean removeBookMarked(String url) {
        return library.delete(Tables.BOOKMARKS.toString(), Columns.CHAPTER_URL + "='" + url + "'", null) > 0;
    }

    /**
     * is this chapter bookmarked?
     *
     * @param url imageURL to the chapter
     * @return if bookmarked?
     */
    public static boolean isBookMarked(String url) {
        Cursor cursor = library.rawQuery("SELECT * from " + Tables.BOOKMARKS + " where " + Columns.CHAPTER_URL + " = '" + url + "'", null);
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
        Cursor cursor = library.rawQuery("select " + Columns.SAVED_DATA + " from " + Tables.BOOKMARKS + " where " + Columns.CHAPTER_URL + "='" + chapterURL + "'", null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return null;
        } else {
            try {
                cursor.moveToNext();
                JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex(Columns.SAVED_DATA.toString())));
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
        return library.delete(Tables.DOWNLOADS.toString(), Columns.CHAPTER_URL + "='" + chapterURL + "' and " + Columns.NOVEL_URL + "='" + novelURL + "'", null) > 0;
    }

    public static void addSavedPath(String novelURL, String chapterURL, String chapterPath) {
        library.execSQL("insert into " + Tables.DOWNLOADS + " (" + Columns.NOVEL_URL + "," + Columns.CHAPTER_URL + "," + Columns.SAVED_DATA + ") values('" +
                novelURL + "','" +
                chapterURL + "','" +
                chapterPath + "')"
        );
    }


    /**
     * Is the chapter saved
     *
     * @param novelURL   novelURL of the novel
     * @param chapterURL novelURL of the chapter
     * @return true if saved, false otherwise
     */
    public static boolean isSaved(String novelURL, String chapterURL) {
        Cursor cursor = library.rawQuery("SELECT * from " + Tables.DOWNLOADS + " where " + Columns.CHAPTER_URL + "='" + chapterURL + "' and " + Columns.NOVEL_URL + "='" + novelURL + "'", null);
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
     * @param novelURL   novelURL of the novel
     * @param chapterURL novelURL of the chapter
     * @return String of passage
     */
    public static String getSaved(String novelURL, String chapterURL) {
        Cursor cursor = library.rawQuery("SELECT * from " + Tables.DOWNLOADS + " where " + Columns.CHAPTER_URL + "='" + chapterURL + "' and " + Columns.NOVEL_URL + "='" + novelURL + "'", null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return null;
        } else {
            cursor.moveToNext();
            String savedData = cursor.getString(cursor.getColumnIndex(Columns.SAVED_DATA.toString()));
            cursor.close();
            return Download_Manager.getText(savedData);
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
            Log.e("JSONException", Objects.requireNonNull(e.getMessage()));
            return false;
        }
        library.execSQL("insert into " + Tables.LIBRARY + "(" + Columns.NOVEL_URL + "," + Columns.SAVED_DATA + "," + Columns.FORMATTER_ID + ") values('" +
                novelURL + "','" +
                data.toString().replaceAll("'", "") + "'," +
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
                new String[]{Columns.NOVEL_URL.toString(), Columns.FORMATTER_ID.toString(), Columns.SAVED_DATA.toString()},
                null, null, null, null, null);
        ArrayList<NovelCard> novelCards = new ArrayList<>();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return new ArrayList<>();
        } else {
            while (cursor.moveToNext()) {
                try {
                    JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex(Columns.SAVED_DATA.toString())));
                    novelCards.add(
                            new NovelCard(
                                    jsonObject.getString("title"),
                                    cursor.getString(cursor.getColumnIndex(Columns.NOVEL_URL.toString())),
                                    jsonObject.getString("imageURL"),
                                    cursor.getInt(cursor.getColumnIndex(Columns.FORMATTER_ID.toString()))
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
