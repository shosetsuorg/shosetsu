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
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.Columns;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.Tables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.inChapters;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.addNovel;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getNovelIDFromNovelURL;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.inLibrary;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.sqLiteDatabase;

/**
 * shosetsu
 * 16 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class RestoreProcess extends AsyncTask<Void, Void, Boolean> {
    String file_path;

    @SuppressLint("StaticFieldLeak")
    Context context;
    @SuppressLint("StaticFieldLeak")
    Button close;
    @SuppressLint("StaticFieldLeak")
    ProgressBar progressBar;
    @SuppressLint("StaticFieldLeak")
    ProgressBar progressBar2;
    @SuppressLint("StaticFieldLeak")
    TextView textView;

    Dialog dialog;

    public RestoreProcess(String file_path, Context context) {
        this.file_path = file_path;
        this.context = context;

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.backup_restore_view);
        close = dialog.findViewById(R.id.button);
        progressBar = dialog.findViewById(R.id.progress);
        progressBar2 = dialog.findViewById(R.id.progressBar3);
        textView = dialog.findViewById(R.id.text);
    }

    @Override
    protected void onPreExecute() {
        Log.i("Progress", "Started restore");
        dialog.show();
    }

    @Override
    protected void onPostExecute(Boolean b) {
        if (b) {
            Log.i("Progress", "Completed restore");
            textView.post(() -> textView.setText(R.string.completed));
            progressBar2.post(() -> progressBar2.setVisibility(View.GONE));
            close.post(() -> close.setOnClickListener(view -> dialog.cancel()));
        } else {
            dialog.cancel();
            Toast.makeText(context, "Failed to process", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected Boolean doInBackground(Void... voids) {
        File file = new File("" + file_path);

        if (file.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

                textView.post(() -> textView.setText(R.string.reading_file));

                StringBuilder string = new StringBuilder();
                {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        string.append(line);
                    }
                    bufferedReader.close();
                }
                final JSONObject BACKUP = new JSONObject(string.substring(6));
                JSONArray novels = BACKUP.getJSONArray("novels");
                JSONArray chapters = BACKUP.getJSONArray("chapters");


                progressBar.post(() -> progressBar.setMax(novels.length() + chapters.length() + 1));


                Log.i("Progress", "Restoring novels");
                for (int x = 0; x < novels.length(); x++) {
                    JSONObject novel = novels.getJSONObject(x);
                    String novelURL = novel.getString(Columns.URL.toString());
                    textView.post(() -> textView.setText("Restoring: " + novelURL));

                    if (!inLibrary(novelURL)) {
                        addNovel(novelURL, novel.getInt(Columns.FORMATTER_ID.toString()));
                        int id = getNovelIDFromNovelURL(novelURL);
                        try {
                            sqLiteDatabase.execSQL("insert into " + Tables.NOVELS + "(" +
                                    Columns.PARENT_ID + "," +
                                    Columns.BOOKMARKED + "," +
                                    Columns.READING_STATUS + "," +
                                    Columns.READER_TYPE + "," +
                                    Columns.TITLE + "," +
                                    Columns.IMAGE_URL + "," +
                                    Columns.DESCRIPTION + "," +
                                    Columns.GENRES + "," +
                                    Columns.AUTHORS + "," +
                                    Columns.STATUS + "," +
                                    Columns.TAGS + "," +
                                    Columns.ARTISTS + "," +
                                    Columns.LANGUAGE + "," +
                                    Columns.MAX_CHAPTER_PAGE +
                                    ")" + "values" + "(" +
                                    id + "," +
                                    1 + "," +
                                    novel.getInt(Columns.READING_STATUS.toString()) + "," +
                                    novel.getInt(Columns.READER_TYPE.toString()) + "," +
                                    "'" + novel.getString(Columns.TITLE.toString()) + "'," +
                                    "'" + novel.getString(Columns.IMAGE_URL.toString()) + "'," +
                                    "'" + novel.getString(Columns.DESCRIPTION.toString()) + "'," +
                                    "'" + novel.getString(Columns.GENRES.toString()) + "'," +
                                    "'" + novel.getString(Columns.AUTHORS.toString()) + "'," +
                                    "'" + novel.getInt(Columns.STATUS.toString()) + "'," +
                                    "'" + novel.getString(Columns.TAGS.toString()) + "'," +
                                    "'" + novel.getString(Columns.ARTISTS.toString()) + "'," +
                                    "'" + novel.getString(Columns.LANGUAGE.toString()) + "'," +
                                    novel.getInt(Columns.MAX_CHAPTER_PAGE.toString()) +
                                    ")");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        //TODO Novel if exists
                    }


                    progressBar.post(() -> progressBar.incrementProgressBy(1));
                }

                Log.i("Progress", "Restoring chapters");
                for (int x = 0; x < chapters.length(); x++) {
                    JSONObject chapter = chapters.getJSONObject(x);
                    String chapterURL = chapter.getString("chapterURL");
                    String novelURL = chapter.getString("novelURL");

                    textView.post(() -> textView.setText("Restoring: " + novelURL + "|" + chapterURL));
                    progressBar.post(() -> progressBar.incrementProgressBy(1));
                    if (!inChapters(chapterURL)) {
                        //TODO Chapter=
                    }

                }

                textView.post(() -> textView.setText("Restoring settings"));
                progressBar.post(() -> progressBar.incrementProgressBy(1));

                //TODO Settings
                progressBar.post(() -> progressBar.incrementProgressBy(1));
                return true;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
