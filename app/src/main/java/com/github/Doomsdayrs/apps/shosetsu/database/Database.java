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
        TABLE_BOOKMARKS("bookmarks");
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
        COLUMN_URL("url"),
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

    // > library
    // novelID is an self affiliated ID for the novel in storage
    // imageURL is the imageURL to the specific novel
    // savedData is a json containing the title(s), author(s), artist(s), paths to downloaded chapters, and more
    // userData includes read chapters, and others
    public static String create = "create TABLE if not exists " + Tables.TABLE_LIBRARY + " (" +
            "novelID integer not null primary key autoincrement, " +
            Columns.COLUMN_URL + " text not null unique, " +
            Columns.COLUMN_FORMATTER_ID + " integer not null, " +
            "savedData text not null, " +
            "userData text)";

    // > bookmarks
    public static String create2 = "create TABLE if not exists " + Tables.TABLE_BOOKMARKS + "(" +
            "novelID integer not null primary key, " +
            Columns.COLUMN_URL + " text unique not null, " +
            Columns.COLUMN_SAVED_DATA + " text)";


    // BOOKMARK CONTROLLERS

    /**
     * Adds a bookmark
     *
     * @param url       imageURL of the novel
     * @param savedData JSON object containing scroll position and others
     */
    public static boolean addBookMark(String url, JSONObject savedData) {
        if (library != null) {
            library.execSQL("insert into " + Tables.TABLE_BOOKMARKS + " (" + Columns.COLUMN_URL + "," + Columns.COLUMN_SAVED_DATA + ") values('" +
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
            return library.delete(Tables.TABLE_BOOKMARKS.toString(), Columns.COLUMN_URL + "='" + url + "'", null) > 0;
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
            Cursor cursor = library.rawQuery("SELECT * from " + Tables.TABLE_BOOKMARKS + " where " + Columns.COLUMN_URL + " = '" + url + "'", null);
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
        Cursor cursor = library.query(Tables.TABLE_BOOKMARKS.toString(), new String[]{Columns.COLUMN_SAVED_DATA.toString()}, null, null, null, null, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return 0;
        } else {
            try {
                cursor.moveToNext();
                JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex(Columns.COLUMN_SAVED_DATA.toString())));
                cursor.close();
                return jsonObject.getInt("y");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    // LIBRARY CONTROLLERS

    public static boolean removePath(String novelURL, String chapterURL) {
        Cursor cursor = library.rawQuery("SELECT " + Columns.COLUMN_SAVED_DATA + " from " + Tables.TABLE_LIBRARY + " where " + Columns.COLUMN_URL + " = '" + novelURL + "'", null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        } else {
            cursor.moveToNext();
            String string = cursor.getString(cursor.getColumnIndex(Columns.COLUMN_SAVED_DATA.toString()));
            cursor.close();
            if (string != null) {
                try {
                    JSONObject savedData = new JSONObject(string);
                    if (!savedData.has("chaptersSaved")) {
                        savedData.put("chaptersSaved", new JSONObject());
                    }
                    JSONObject chaptersSaved = savedData.getJSONObject("chaptersSaved");
                    chaptersSaved.remove(chapterURL);
                    savedData.put("chaptersSaved", chaptersSaved);
                    library.execSQL("update " + Tables.TABLE_LIBRARY + " set " + Columns.COLUMN_SAVED_DATA + "='" + savedData.toString() + "' where " + Columns.COLUMN_URL + "='" + novelURL + "'");
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean addSavedPath(String novelURL, String chapterURL, String chapterPath) {

        Cursor cursor = library.rawQuery("SELECT " + Columns.COLUMN_SAVED_DATA + " from " + Tables.TABLE_LIBRARY + " where " + Columns.COLUMN_URL + " = '" + novelURL + "'", null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        } else {
            cursor.moveToNext();
            String string = cursor.getString(cursor.getColumnIndex(Columns.COLUMN_SAVED_DATA.toString()));
            cursor.close();
            if (string != null) {
                try {
                    JSONObject savedData = new JSONObject(string);
                    if (!savedData.has("chaptersSaved")) {
                        savedData.put("chaptersSaved", new JSONObject());
                    }
                    JSONObject chaptersSaved = savedData.getJSONObject("chaptersSaved");
                    chaptersSaved.put(chapterURL, chapterPath);
                    savedData.put("chaptersSaved", chaptersSaved);
                    library.execSQL("update " + Tables.TABLE_LIBRARY + " set " + Columns.COLUMN_SAVED_DATA + "='" + savedData.toString() + "' where " + Columns.COLUMN_URL + "='" + novelURL + "'");
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public static boolean isSaved(String novelURL, String chapterURL) {
        Cursor cursor = library.rawQuery("SELECT * from " + Tables.TABLE_LIBRARY + " where " + Columns.COLUMN_URL + " = '" + novelURL + "'", null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        } else {
            try {
                cursor.moveToNext();
                int index = cursor.getColumnIndex(Columns.COLUMN_SAVED_DATA.toString());
                if (index < 0)
                    return false;
                JSONObject savedData = new JSONObject(cursor.getString(index));
                cursor.close();
                if (savedData.has("chaptersSaved"))
                    if (savedData.getJSONObject("chaptersSaved").has(chapterURL))
                        return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String getSaved(String novelURL, String chapterURL) {
        Cursor cursor = library.rawQuery("SELECT * from " + Tables.TABLE_LIBRARY + " where " + Columns.COLUMN_URL + " = '" + novelURL + "'", null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return null;
        } else {
            try {
                cursor.moveToNext();
                int index = cursor.getColumnIndex(Columns.COLUMN_SAVED_DATA.toString());
                if (index < 0)
                    return null;
                JSONObject savedData = new JSONObject(cursor.getString(index));
                cursor.close();
                if (savedData.has("chaptersSaved"))
                    if (savedData.getJSONObject("chaptersSaved").has(chapterURL)) {
                        String path = savedData.getJSONObject("chaptersSaved").getString(chapterURL);
                        return Downloadmanager.getText(path);
                    } else return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
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
            library.execSQL("insert into " + Tables.TABLE_LIBRARY + "(" + Columns.COLUMN_URL + "," + Columns.COLUMN_SAVED_DATA + "," + Columns.COLUMN_FORMATTER_ID + ") values(" +
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
        Log.d("removeLibtem imageURL", novelURL);
        if (library != null)
            return library.delete(Tables.TABLE_LIBRARY.toString(), Columns.COLUMN_URL + "='" + novelURL + "'", null) > 0;
        else {
            Log.e("Database", "isNULL");
            return false;
        }
    }

    public static boolean inLibrary(String url) {
        Log.d("inLibrary imageURL", url);
        if (library != null) {
            Cursor cursor = library.rawQuery("SELECT * from " + Tables.TABLE_LIBRARY + " where " + Columns.COLUMN_URL + " ='" + url + "'", null);
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
        Cursor cursor = library.query(Tables.TABLE_LIBRARY.toString(),
                new String[]{Columns.COLUMN_URL.toString(), Columns.COLUMN_FORMATTER_ID.toString(), Columns.COLUMN_SAVED_DATA.toString()},
                null, null, null, null, null);
        ArrayList<NovelCard> novelCards = new ArrayList<>();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return new ArrayList<>();
        } else {
            while (cursor.moveToNext()) {
                try {
                    JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex(Columns.COLUMN_SAVED_DATA.toString())));
                    novelCards.add(new NovelCard(jsonObject.getString("title"), cursor.getString(cursor.getColumnIndex(Columns.COLUMN_URL.toString())), jsonObject.getString("imageURL"), cursor.getInt(cursor.getColumnIndex(Columns.COLUMN_FORMATTER_ID.toString()))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
            return novelCards;
        }
    }
}
