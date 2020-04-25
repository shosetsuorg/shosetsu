package com.github.doomsdayrs.apps.shosetsu.backend.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

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

    /**
     * Constructor
     *
     * @param context main context
     */
    public DBHelper(Context context) {
        super(context, DB_NAME, null, 10);
    }

    /**
     * Creates DB things
     *
     * @param db db to fill
     */
    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
    }

    /**
     * Upgrades database
     *
     * @param db         database to alter
     * @param oldVersion previous version ID
     * @param newVersion new version ID
     */
    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 9) {
            //TODO Convert tables
            db.execSQL("DROP TABLE IF EXISTS " + Tables.CHAPTERS);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.NOVELS);
            db.execSQL("DROP TABLE IF EXISTS downloads");
            db.execSQL("DROP TABLE IF EXISTS updates");
        }
        if (oldVersion < 10) {
            // Convert to room~
        }
    }
}