package com.github.doomsdayrs.apps.shosetsu.backend.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.Columns;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.Tables;

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
 * 14 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "database.db";


    private static final String NOVEL_IDENTIFICATION_CREATE = "create table if not exists " + Tables.NOVEL_IDENTIFICATION + "(" +
            Columns.URL + " text not null primary key," +
            Columns.ID + " integer not null autoincrement," +
            Columns.FORMATTER_ID + " integer not null" +
            ")";

    private static final String CHAPTER_IDENTIFICATION_CREATE = "create table if not exists " + Tables.CHAPTER_IDENTIFICATION + "(" +
            Columns.URL + " text not null primary key," +
            Columns.ID + " integer not null autoincrement," +
            Columns.FORMATTER_ID + " integer not null" +
            ")";

    private static final String CHAPTERS_CREATE = "create table if not exists " + Tables.CHAPTERS + "(" +
            // Novel URL this chapter belongs to
            Columns.NOVEL_URL + " text not null," +
            // The chapter chapterURL
            Columns.CHAPTER_URL + " text not null unique," +

            Columns.NOVEL_CHAPTER + " text," +

            // Saved Data
            // > Scroll position, either 0 for top, or X for the position
            Columns.Y + " integer not null," +
            // > Either 0 for none, or an incremented count (Status)
            Columns.READ_CHAPTER + " integer not null," +
            // > Either 0 for false or 1 for true.
            Columns.BOOKMARKED + " integer not null," +

            // If 1 then true and SAVE_PATH has data, false otherwise
            Columns.IS_SAVED + " integer not null," +
            Columns.SAVE_PATH + " text)";

    //TODO Figure out a legitimate way to structure all this data

    // Library that the user has saved their novels to
    private static final String NOVELS = "create TABLE if not exists " + Tables.NOVELS + " (" +
            // If in the library
            Columns.BOOKMARKED + " integer not null," +
            // URL of this novel
            Columns.NOVEL_URL + " text not null unique, " +
            // Saved DATA of the novel
            Columns.NOVEL_PAGE + " text not null," +
            // Formatter this novel comes from
            Columns.FORMATTER_ID + " integer not null," +
            Columns.STATUS + " integer not null" + ")";


    // Watches download listing
    private static final String DOWNLOADS_CREATE = "create TABLE if not exists " + Tables.DOWNLOADS + "(" +
            Columns.FORMATTER_ID + " integer not null," +
            Columns.NOVEL_URL + " text not null," +
            Columns.CHAPTER_URL + " text not null," +

            Columns.NOVEL_NAME + " text not null," +
            Columns.CHAPTER_NAME + " text not null," +

            // If this novel should be skipped over
            // TODO Put this into use in Download_Manager
            // TODO put status as a column here
            Columns.PAUSED + " integer not null)";

    private static final String UPDATES_CREATE = "create table if not exists " + Tables.UPDATES + "(" +
            Columns.NOVEL_URL + " text not null," +
            Columns.CHAPTER_URL + " text not null unique," +
            Columns.TIME + " integer not null" + ")";

    /**
     * Constructor
     *
     * @param context main context
     */
    public DBHelper(Context context) {
        super(context, DB_NAME, null, 9);
    }

    /**
     * Creates DB things
     *
     * @param db db to fill
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NOVELS);
        db.execSQL(DOWNLOADS_CREATE);
        db.execSQL(CHAPTERS_CREATE);
        db.execSQL(UPDATES_CREATE);
    }


    /**
     * Upgrades database
     *
     * @param db         database to alter
     * @param oldVersion previous version ID
     * @param newVersion new version ID
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 9) {
            db.execSQL("DROP TABLE IF EXISTS " + Tables.CHAPTERS);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.NOVELS);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.DOWNLOADS);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.UPDATES);

            db.execSQL(NOVELS);
            db.execSQL(DOWNLOADS_CREATE);
            db.execSQL(CHAPTERS_CREATE);
            db.execSQL(UPDATES_CREATE);
        }
    }

}