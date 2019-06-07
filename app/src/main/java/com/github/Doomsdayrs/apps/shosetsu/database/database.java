package com.github.Doomsdayrs.apps.shosetsu.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class database {
    static boolean ready = false;
    static SQLiteDatabase library;

    class Helper extends SQLiteOpenHelper {
        public Helper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            library = this.getWritableDatabase();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String create = "create table if not exists library (novelID integer not null primary key autoincrement, formatterID integer not null, offlineData text not null, userData text)";
            library.execSQL(create);
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
