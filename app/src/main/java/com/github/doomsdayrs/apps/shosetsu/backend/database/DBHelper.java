package com.github.doomsdayrs.apps.shosetsu.backend.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see https://www.gnu.org/licenses/ .
 * ====================================================================
 * Shosetsu
 * 14 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "database.db";


    private static final String chaptersCreate = "create table if not exists " + Database.Tables.CHAPTERS + "(" +
            // Novel URL this chapter belongs to
            Database.Columns.NOVEL_URL + " text not null," +
            // The chapter chapterURL
            Database.Columns.CHAPTER_URL + " text not null unique," +

            // Unsure if i should keep this or not
            Database.Columns.SAVED_DATA + " text," +

            // Saved Data
            // > Scroll position, either 0 for top, or X for the position
            Database.Columns.Y + " integer not null," +
            // > Either 0 for none, or an incremented count (Status)
            Database.Columns.READ_CHAPTER + " integer not null," +
            // > Either 0 for false or 1 for true.
            Database.Columns.BOOKMARKED + " integer not null," +

            // If 1 then true and SAVE_PATH has data, false otherwise
            Database.Columns.IS_SAVED + " integer not null," +
            Database.Columns.SAVE_PATH + " text)";

    //TODO Figure out a legitimate way to structure all this data

    // Library that the user has saved their novels to
    private static final String libraryCreate = "create TABLE if not exists " + Database.Tables.NOVELS + " (" +
            // If in the library
            Database.Columns.BOOKMARKED + " integer not null," +
            // URL of this novel
            Database.Columns.NOVEL_URL + " text not null unique, " +
            // Saved DATA of the novel
            Database.Columns.NOVEL_PAGE + " text not null," +
            // Formatter this novel comes from
            Database.Columns.FORMATTER_ID + " integer not null," +
            Database.Columns.STATUS + " integer not null" + ")";


    // Watches download listing
    private static final String downloadsCreate = "create TABLE if not exists " + Database.Tables.DOWNLOADS + "(" +
            Database.Columns.FORMATTER_ID + " integer not null," +
            Database.Columns.NOVEL_URL + " text not null," +
            Database.Columns.CHAPTER_URL + " text not null," +

            Database.Columns.NOVEL_NAME + " text not null," +
            Database.Columns.CHAPTER_NAME + " text not null," +

            // If this novel should be skipped over
            // TODO Put this into use in Download_Manager
            // TODO put status as a column here
            Database.Columns.PAUSED + " integer not null)";

    //TODO Update table for all the updates


    /**
     * Constructor
     *
     * @param context main context
     */
    public DBHelper(Context context) {
        super(context, DB_NAME, null, 6);
    }


    /**
     * Creates DB things
     *
     * @param db db to fill
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(libraryCreate);
        db.execSQL(downloadsCreate);
        db.execSQL(chaptersCreate);
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
        if (oldVersion < 4) {
            db.execSQL("drop table if exists library");
            db.execSQL("drop table if exists bookmarks");
            db.execSQL("drop table if exists downloads");
            db.execSQL("drop table if exists chapters");
            db.execSQL("create TABLE if not exists library (" +
                    // URL of this novel
                    Database.Columns.NOVEL_URL + " text not null unique, " +
                    // Saved DATA of the novel
                    Database.Columns.NOVEL_PAGE + " text not null," +
                    // Formatter this novel comes from
                    Database.Columns.FORMATTER_ID + " integer not null," +
                    Database.Columns.MAX_PAGE + " integer not null," +
                    Database.Columns.STATUS + " integer not null" + ")");
            db.execSQL(downloadsCreate);
            db.execSQL(chaptersCreate);
        }
        if (oldVersion < 5) {
            // in between
            db.execSQL("create TABLE if not exists libraryNext (" +
                    // URL of this novel
                    Database.Columns.NOVEL_URL + " text not null unique, " +
                    // Saved DATA of the novel
                    Database.Columns.NOVEL_PAGE + " text not null," +
                    // Formatter this novel comes from
                    Database.Columns.FORMATTER_ID + " integer not null," +
                    Database.Columns.MAX_PAGE + " integer not null," +
                    Database.Columns.STATUS + " integer not null" + ")");

            // Move data to middle
            Cursor cursor = db.rawQuery("select * from library", null);

            while (cursor.moveToNext()) {
                db.execSQL("insert into libraryNext (" +
                        Database.Columns.NOVEL_URL + "," +
                        Database.Columns.NOVEL_PAGE + "," +
                        Database.Columns.FORMATTER_ID + "," +
                        Database.Columns.MAX_PAGE + "," +
                        Database.Columns.STATUS + ") values ('" +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_URL.toString())) + "','" +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_PAGE.toString())) + "'," +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.FORMATTER_ID.toString())) + "," +
                        0 + "," +
                        0 + ")");
            }
            cursor.close();
            // Drop old table
            db.execSQL("drop table if exists library");
            db.execSQL("create TABLE if not exists library (" +
                    // URL of this novel
                    Database.Columns.NOVEL_URL + " text not null unique, " +
                    // Saved DATA of the novel
                    Database.Columns.NOVEL_PAGE + " text not null," +
                    // Formatter this novel comes from
                    Database.Columns.FORMATTER_ID + " integer not null," +
                    Database.Columns.MAX_PAGE + " integer not null," +
                    Database.Columns.STATUS + " integer not null" + ")");

            // Move middle to new
            cursor = db.rawQuery("select * from libraryNext", null);
            while (cursor.moveToNext()) {
                db.execSQL("insert into library (" +
                        Database.Columns.NOVEL_URL + "," +
                        Database.Columns.NOVEL_PAGE + "," +
                        Database.Columns.FORMATTER_ID + "," +
                        Database.Columns.MAX_PAGE + "," +
                        Database.Columns.STATUS + ") values ('" +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_URL.toString())) + "','" +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_PAGE.toString())) + "'," +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.FORMATTER_ID.toString())) + "," +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.MAX_PAGE.toString())) + "," +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.STATUS.toString())) + ")");
            }
            db.execSQL("drop table if exists libraryNext");
        }
        if (oldVersion < 6) {
            db.execSQL("drop table if exists libraryNext");
            // in between
            db.execSQL("create TABLE if not exists libraryNext (" +
                    //If in library
                    Database.Columns.BOOKMARKED + " text not null, " +
                    // URL of this novel
                    Database.Columns.NOVEL_URL + " text not null unique, " +
                    // Saved DATA of the novel
                    Database.Columns.NOVEL_PAGE + " text not null," +
                    // Formatter this novel comes from
                    Database.Columns.FORMATTER_ID + " integer not null," +
                    Database.Columns.STATUS + " integer not null" + ")");

            // Move data to middle
            Cursor cursor = db.rawQuery("select * from library", null);

            while (cursor.moveToNext()) {
                db.execSQL("insert into libraryNext (" +
                        Database.Columns.BOOKMARKED + "," +
                        Database.Columns.NOVEL_URL + "," +
                        Database.Columns.NOVEL_PAGE + "," +
                        Database.Columns.FORMATTER_ID + "," +
                        Database.Columns.STATUS + ") values (" +
                        1 + ",'" +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_URL.toString())) + "','" +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_PAGE.toString())) + "'," +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.FORMATTER_ID.toString())) + "," +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.STATUS.toString())) + ")");
            }
            cursor.close();

            // Drop old table
            db.execSQL("drop table if exists library");
            db.execSQL("create TABLE if not exists " + Database.Tables.NOVELS + " (" +
                    // If in library
                    Database.Columns.BOOKMARKED + " integer not null, " +
                    // URL of this novel
                    Database.Columns.NOVEL_URL + " text not null unique, " +
                    // Saved DATA of the novel
                    Database.Columns.NOVEL_PAGE + " text not null," +
                    // Formatter this novel comes from
                    Database.Columns.FORMATTER_ID + " integer not null," +
                    Database.Columns.STATUS + " integer not null" + ")");

            // Move middle to new
            cursor = db.rawQuery("select * from libraryNext", null);
            while (cursor.moveToNext()) {
                db.execSQL("insert into novels (" +
                        Database.Columns.BOOKMARKED + "," +
                        Database.Columns.NOVEL_URL + "," +
                        Database.Columns.NOVEL_PAGE + "," +
                        Database.Columns.FORMATTER_ID + "," +
                        Database.Columns.STATUS + ") values (" +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.BOOKMARKED.toString())) + ",'" +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_URL.toString())) + "','" +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_PAGE.toString())) + "'," +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.FORMATTER_ID.toString())) + "," +
                        cursor.getString(cursor.getColumnIndex(Database.Columns.STATUS.toString())) + ")");
            }
            cursor.close();
            db.execSQL("drop table if exists libraryNext");
        }
    }
}