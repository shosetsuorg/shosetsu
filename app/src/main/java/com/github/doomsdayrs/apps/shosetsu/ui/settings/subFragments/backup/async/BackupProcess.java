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
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.shoDir;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getChapterURLFromChapterID;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getFormatterIDFromNovelURL;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getNovelURLfromNovelID;
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


                        // Gets if it is in library, if not then it skips
                        boolean bookmarked = intToBoolean(cursor.getInt(cursor.getColumnIndex(Database.Columns.BOOKMARKED.toString())));
                        Log.i("NovelBack", "Valid?: " + bookmarked);
                        if (bookmarked) {
                            String nurl = getNovelURLfromNovelID(cursor.getInt(cursor.getColumnIndex(Database.Columns.PARENT_ID.toString())));

                            JSONObject novel = new JSONObject();
                            novel.put("novelURL", nurl);
                            novel.put("FORMATTER_ID", getFormatterIDFromNovelURL(nurl));

                            novel.put("title", cursor.getString(cursor.getColumnIndex(Database.Columns.TITLE.toString())));

                            novel.put("imageURL", cursor.getString(cursor.getColumnIndex(Database.Columns.IMAGE_URL.toString())));

                            novel.put("description", cursor.getString(cursor.getColumnIndex(Database.Columns.DESCRIPTION.toString())));

                            novel.put("genres", cursor.getString(cursor.getColumnIndex(Database.Columns.GENRES.toString())));

                            novel.put("authors", cursor.getString(cursor.getColumnIndex(Database.Columns.AUTHORS.toString())));

                            novel.put("status", cursor.getString(cursor.getColumnIndex(Database.Columns.STATUS.toString())));

                            novel.put("tags", cursor.getString(cursor.getColumnIndex(Database.Columns.TAGS.toString())));

                            novel.put("artists", cursor.getString(cursor.getColumnIndex(Database.Columns.ARTISTS.toString())));

                            novel.put("language", cursor.getString(cursor.getColumnIndex(Database.Columns.LANGUAGE.toString())));

                            novel.put("maxChapterPage", cursor.getInt(cursor.getColumnIndex(Database.Columns.MAX_CHAPTER_PAGE.toString())));

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
                        int novelID = cursor.getInt(cursor.getColumnIndex(Database.Columns.PARENT_ID.toString()));
                        boolean b = Database.DatabaseNovels.isBookmarked(novelID);

                        if (b) {
                            int id = cursor.getInt(cursor.getColumnIndex(Database.Columns.ID.toString()));
                            JSONObject chapter = new JSONObject();
                            chapter.put("novelURL", getNovelURLfromNovelID(novelID));
                            chapter.put("chapterURL", getChapterURLFromChapterID(id));

                            chapter.put("title", cursor.getString(cursor.getColumnIndex(Database.Columns.TITLE.toString())));
                            chapter.put("release_date", cursor.getString(cursor.getColumnIndex(Database.Columns.RELEASE_DATE.toString())));
                            chapter.put("order", cursor.getInt(cursor.getColumnIndex(Database.Columns.ORDER.toString())));

                            chapter.put("Y", cursor.getInt(cursor.getColumnIndex(Database.Columns.Y.toString())));
                            chapter.put("READ_CHAPTER", cursor.getInt(cursor.getColumnIndex(Database.Columns.READ_CHAPTER.toString())));
                            chapter.put("BOOKMARKED", cursor.getInt(cursor.getColumnIndex(Database.Columns.BOOKMARKED.toString())));
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
            fileOutputStream.write(("JSON+-=" + BACKUP.toString()).getBytes());
            fileOutputStream.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
