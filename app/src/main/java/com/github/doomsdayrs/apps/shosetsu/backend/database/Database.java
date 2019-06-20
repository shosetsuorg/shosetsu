package com.github.doomsdayrs.apps.shosetsu.backend.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;

import org.jetbrains.annotations.NotNull;

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
        READ_CHAPTERS("readChapters"),
        Y("y"),
        BOOKMARKED("bookmarked"),
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
            Columns.NOVEL_URL + " text not null," +
            Columns.CHAPTER_URL + " text not null, " +
            Columns.SAVED_DATA + " text)";

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
            Columns.READ_CHAPTERS + " integer not null," +
            // > Either 0 for false or 1 for true.
            Columns.BOOKMARKED + " integer not null," +

            // If 1 then true and SAVE_PATH has data, false otherwise
            Columns.IS_SAVED + " integer not null," +
            Columns.SAVE_PATH + " text)";


    // BOOKMARK CONTROLLERS

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

    /**
     * returns Y coordinate
     * Precondition is the chapter is already in the database
     *
     * @param url imageURL to the chapter
     * @return if bookmarked?
     */
    public static int getY(String url) {
        Cursor cursor = library.rawQuery("SELECT " + Columns.Y + " from " + Tables.CHAPTERS + " where " + Columns.CHAPTER_URL + " = '" + url + "'", null);
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


    public static boolean inChapters(String chapterURL){
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
                Columns.READ_CHAPTERS + "," +
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
}
