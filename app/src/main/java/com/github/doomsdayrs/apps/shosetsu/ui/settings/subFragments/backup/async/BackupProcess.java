package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments.backup.async;
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

import com.github.doomsdayrs.apps.shosetsu.backend.Serialize;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.intToBoolean;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.serializeToString;
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
            final JSONObject BACKUP = new JSONObject();

            Log.i("Progress", "Backing up novels");
            {
                final JSONArray NOVELS = new JSONArray();
                Cursor cursor = sqLiteDatabase.rawQuery("select * from " + Database.Tables.NOVELS + " where " + Database.Columns.BOOKMARKED + "=1", null);
                if (!(cursor.getCount() <= 0))
                    while (cursor.moveToNext()) {

                        // Gets the novelURL
                        String nurl = cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_URL.toString()));
                        Log.i("NovelBack", nurl);

                        // Gets if it is in library, if not then it skips
                        boolean bookmarked = intToBoolean(cursor.getInt(cursor.getColumnIndex(Database.Columns.BOOKMARKED.toString())));
                        Log.i("NovelBack", "Valid?: " + bookmarked);
                        if (bookmarked) {
                            //SHOULD BE IN JSON ALREADY
                            JSONObject npage = new JSONObject(cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_PAGE.toString())));
                            npage.put("novelChapters", new JSONArray());

                            // ID of formatter
                            int formatter_id = cursor.getInt(cursor.getColumnIndex(Database.Columns.FORMATTER_ID.toString()));

                            //IGNORED: int status = cursor.getInt(cursor.getColumnIndex(Database.Columns.STATUS.toString()));

                            JSONObject novel = new JSONObject();
                            novel.put("novelURL", serializeToString(nurl));
                            novel.put("bookmarked", true);
                            novel.put("FORMATTER_ID", formatter_id);
                            novel.put("novelPage", npage);
                            NOVELS.put(novel);
                        }
                    }
                BACKUP.put("novels", NOVELS);
                cursor.close();
            }

            Log.i("Progress", "Backing up Chapters");
            {
                final JSONArray CHAPTERS = new JSONArray();
                Cursor cursor = sqLiteDatabase.rawQuery("select * from " + Database.Tables.CHAPTERS, null);
                if (!(cursor.getCount() <= 0))
                    while (cursor.moveToNext()) {
                        String nurl = cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_URL.toString()));

                        boolean inLibrary = Database.DatabaseNovels.isBookmarked(nurl);
                        if (inLibrary) {
                            String curl = cursor.getString(cursor.getColumnIndex(Database.Columns.CHAPTER_URL.toString()));
                            // very dirty logger
                            //Log.i("ChapterBack", curl);

                            String saved_data = cursor.getString(cursor.getColumnIndex(Database.Columns.NOVEL_CHAPTER.toString()));
                            int y = cursor.getInt(cursor.getColumnIndex(Database.Columns.Y.toString()));
                            int read_chapter = cursor.getInt(cursor.getColumnIndex(Database.Columns.READ_CHAPTER.toString()));
                            boolean bookmarked = intToBoolean(cursor.getInt(cursor.getColumnIndex(Database.Columns.BOOKMARKED.toString())));
                            boolean is_saved = intToBoolean(cursor.getInt(cursor.getColumnIndex(Database.Columns.IS_SAVED.toString())));
                            String path = cursor.getString(cursor.getColumnIndex(Database.Columns.SAVE_PATH.toString()));

                            JSONObject chapter = new JSONObject();
                            chapter.put("novelURL", serializeToString(nurl));
                            chapter.put("chapterURL", serializeToString(curl));

                            //TODO Figure out where i use this
                            //chapter.put("SAVED_DATA",);

                            chapter.put("Y", y);
                            chapter.put("READ_CHAPTER", read_chapter);
                            chapter.put("BOOKMARKED", bookmarked);
                            chapter.put("IS_SAVED", is_saved);
                            chapter.put("SAVE_PATH", serializeToString(path));
                            CHAPTERS.put(chapter);
                        }
                    }
                BACKUP.put("chapters", CHAPTERS);
                cursor.close();
            }

            BACKUP.put("settings", Serialize.getSettingsInJSON());

            Log.i("Progress", "Writing");
            File folder = new File(shoDir + "/backup/");
            if (!folder.exists())
                if (!folder.mkdirs()) {
                    throw new IOException("Failed to mkdirs");
                }
            FileOutputStream fileOutputStream = new FileOutputStream(
                    (folder.getPath() + "/backup-" + (new Date().toString()) + ".shoback")
            );
            fileOutputStream.write(BACKUP.toString().getBytes());
            fileOutputStream.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
