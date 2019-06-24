package com.github.doomsdayrs.apps.shosetsu.backend.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
 * 14 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "database.db";

    /**
     * Constructor
     *
     * @param context main context
     */
    public DBHelper(Context context) {
        super(context, DB_NAME, null, 4);
    }


    /**
     * Creates DB things
     *
     * @param db db to fill
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Database.libraryCreate);
        db.execSQL(Database.chaptersCreate);
        db.execSQL(Database.downloadsCreate);
    }

    /**
     * Upgrades database
     *
     * @param db         database to alter
     * @param oldVersion previous version ID
     * @param newVersion new version ID
     */
    //TODO Actually save data between db versions
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            db.execSQL(Database.libraryCreate);
            db.execSQL(Database.bookmarksCreate);
            db.execSQL("drop table if exists " + Database.Tables.LIBRARY);
            db.execSQL("drop table if exists " + Database.Tables.BOOKMARKS);
            db.execSQL(Database.libraryCreate);
        }

        if (oldVersion < 3) {
            db.execSQL(Database.libraryCreate);
            db.execSQL(Database.downloadsCreate);
            db.execSQL("drop table if exists " + Database.Tables.DOWNLOADS);
            db.execSQL("drop table if exists " + Database.Tables.BOOKMARKS);
            db.execSQL("drop table if exists " + Database.Tables.LIBRARY);
            db.execSQL(Database.libraryCreate);
            db.execSQL(Database.chaptersCreate);
        }

        if (oldVersion < 4) {
            db.execSQL(Database.downloadsCreate);
        }
    }
}