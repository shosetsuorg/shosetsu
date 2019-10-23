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
            Columns.ID + " integer primary key autoincrement," +
            Columns.URL + " text unique not null," +
            Columns.FORMATTER_ID + " integer not null" +
            ")";

    private static final String CHAPTER_IDENTIFICATION_CREATE = "create table if not exists " + Tables.CHAPTER_IDENTIFICATION + "(" +
            Columns.ID + " integer primary key autoincrement," +

            // Refers to NOVEL
            Columns.PARENT_ID + " integer not null," +
            Columns.URL + " text unique not null" +
            ")";


    private static final String CHAPTERS_CREATE = "create table if not exists " + Tables.CHAPTERS + "(" +
            // Refers to CHAPERID
            Columns.ID + " integer primary key," +

            // Refers to NOVELID
            Columns.PARENT_ID + " integer not null," +

            Columns.TITLE + " text," +
            Columns.RELEASE_DATE + " text," +
            Columns.ORDER + " integer not null," +

            // > Scroll position, either 0 for top, or X for the position
            Columns.Y + " integer not null," +
            // > Either 0 for none, or an incremented count (Status)
            Columns.READ_CHAPTER + " integer not null," +
            // > Either 0 for false or 1 for true.
            Columns.BOOKMARKED + " integer  not null," +

            // If 1 then true and SAVE_PATH has data, false otherwise
            Columns.IS_SAVED + " integer not null," +
            Columns.SAVE_PATH + " text" +
            ")";

    //TODO Figure out a legitimate way to structure all this data

    // Library that the user has saved their novels to
    private static final String NOVELS = "create TABLE if not exists " + Tables.NOVELS + " (" +
            // Refers to NOVELID
            Columns.PARENT_ID + " integer not null," +
            // If in the library
            Columns.BOOKMARKED + " integer not null," +

            Columns.READING_STATUS + " text," +
            Columns.READER_TYPE + " integer," +

            // This bulk is the data values
            Columns.TITLE + " text," +
            Columns.IMAGE_URL + " text," +
            Columns.DESCRIPTION + " text," +
            Columns.GENRES + " text," +
            Columns.AUTHORS + " text," +
            Columns.STATUS + " text not null," +
            Columns.TAGS + " text," +
            Columns.ARTISTS + " text," +
            Columns.LANGUAGE + " text," +
            Columns.MAX_CHAPTER_PAGE + " integer" +
            ")";


    // Watches download listing
    private static final String DOWNLOADS_CREATE = "create TABLE if not exists " + Tables.DOWNLOADS + "(" +
            // Refers to CHAPERID
            Columns.PARENT_ID + " integer not null," +

            Columns.NOVEL_NAME + " text not null," +
            Columns.CHAPTER_NAME + " text not null," +

            // If this novel should be skipped over
            // TODO Put this into use in Download_Manager
            // TODO put status as a column here
            Columns.PAUSED + " integer not null)";

    private static final String UPDATES_CREATE = "create table if not exists " + Tables.UPDATES + "(" +
            // Refers to CHAPERID
            Columns.ID + " integer not null," +
            // Refers to NovelID
            Columns.PARENT_ID + " integer not null," +
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
        db.execSQL(NOVEL_IDENTIFICATION_CREATE);
        db.execSQL(CHAPTER_IDENTIFICATION_CREATE);
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
            //TODO Convert tables
            db.execSQL("DROP TABLE IF EXISTS " + Tables.CHAPTERS);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.NOVELS);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.DOWNLOADS);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.UPDATES);

            db.execSQL(NOVEL_IDENTIFICATION_CREATE);
            db.execSQL(CHAPTER_IDENTIFICATION_CREATE);

            db.execSQL(NOVELS);
            db.execSQL(DOWNLOADS_CREATE);
            db.execSQL(CHAPTERS_CREATE);
            db.execSQL(UPDATES_CREATE);
        }
    }

}