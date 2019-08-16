package com.github.doomsdayrs.apps.shosetsu.backend.async;
/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.DBChapter;
import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.DBDownloadItem;
import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.DBNovel;
import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.SUPERSERIALZIED;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.intToBoolean;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.shoDir;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.sqLiteDatabase;

/**
 * shosetsu
 * 16 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class BackupProcess extends AsyncTask<Void, Void, Void> {
    @SuppressLint("StaticFieldLeak")
    Context context;

    public BackupProcess(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        Log.i("Progress", "Starting backup");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.i("Progress", "Finished backup");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            final SUPERSERIALZIED SUPER_SERIALIZED = new SUPERSERIALZIED();

            Log.i("Progress", "Backing up novels");
            // Library backup
            {
                Cursor cursor = sqLiteDatabase.rawQuery("select * from " + Database.Tables.NOVELS + " where " + Database.Columns.BOOKMARKED + "=1", null);
                if (!(cursor.getCount() <= 0))
                    while (cursor.moveToNext()) {
                        String nurl = cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_URL.toString()));
                        Log.i("NovelBack", nurl);
                        boolean bookmarked = intToBoolean(cursor.getInt(cursor.getColumnIndex(Database.Columns.BOOKMARKED.toString())));
                        String npage = cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_PAGE.toString()));
                        int formatter_id = cursor.getInt(cursor.getColumnIndex(Database.Columns.FORMATTER_ID.toString()));
                        int status = cursor.getInt(cursor.getColumnIndex(Database.Columns.STATUS.toString()));

                        SUPER_SERIALIZED.libraries.add(new DBNovel(nurl, bookmarked, npage, formatter_id, status));
                    }
                cursor.close();
            }

            // TODO figure out how to prevent non library chapters from being saved, lowering size of backup
            Log.i("Progress", "Backing up Chapters");
            // Chapter backup
            {
                Cursor cursor = sqLiteDatabase.rawQuery("select * from " + Database.Tables.CHAPTERS, null);
                if (!(cursor.getCount() <= 0))
                    while (cursor.moveToNext()) {
                        String nurl = cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_URL.toString()));
                        String curl = cursor.getString(cursor.getColumnIndex(Database.Columns.CHAPTER_URL.toString()));
                        // very dirty logger
                        //Log.i("ChapterBack", curl);
                        String saved_data = cursor.getString(cursor.getColumnIndex(Database.Columns.SAVED_DATA.toString()));
                        int y = cursor.getInt(cursor.getColumnIndex(Database.Columns.Y.toString()));
                        int read_chapter = cursor.getInt(cursor.getColumnIndex(Database.Columns.READ_CHAPTER.toString()));
                        boolean bookmarked = intToBoolean(cursor.getInt(cursor.getColumnIndex(Database.Columns.BOOKMARKED.toString())));
                        boolean is_saved = intToBoolean(cursor.getInt(cursor.getColumnIndex(Database.Columns.IS_SAVED.toString())));
                        String path = cursor.getString(cursor.getColumnIndex(Database.Columns.SAVED_DATA.toString()));

                        SUPER_SERIALIZED.DBChapters.add(new DBChapter(nurl, curl, saved_data, y, read_chapter, bookmarked, is_saved, path));
                    }
                cursor.close();
            }

            Log.i("Progress", "Backing up Downloads");
            // Downloads backup
            {
                Cursor cursor = sqLiteDatabase.rawQuery("select * from " + Database.Tables.DOWNLOADS, null);
                if (!(cursor.getCount() <= 0))
                    while (cursor.moveToNext()) {
                        String nurl = cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_URL.toString()));
                        String curl = cursor.getString(cursor.getColumnIndex(Database.Columns.CHAPTER_URL.toString()));
                        String nname = cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_NAME.toString()));
                        String cname = cursor.getString(cursor.getColumnIndex(Database.Columns.CHAPTER_NAME.toString()));
                        int formatter_id = cursor.getInt(cursor.getColumnIndex(Database.Columns.FORMATTER_ID.toString()));
                        boolean paused = intToBoolean(cursor.getInt(cursor.getColumnIndex(Database.Columns.PAUSED.toString())));

                        SUPER_SERIALIZED.DBDownloadItems.add(new DBDownloadItem(nurl, curl, formatter_id, nname, cname, paused));
                    }
                cursor.close();
            }

            Log.i("Progress", "Writing");
            File folder = new File(shoDir + "/backup/");
            if (!folder.exists())
                if (!folder.mkdirs()) {
                    throw new IOException("Failed to mkdirs");
                }
            FileOutputStream fileOutputStream = new FileOutputStream(
                    (folder.getPath() + "/backup-" + (new Date().toString()) + ".shoback")
            );
            fileOutputStream.write(SUPER_SERIALIZED.serialize().getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
