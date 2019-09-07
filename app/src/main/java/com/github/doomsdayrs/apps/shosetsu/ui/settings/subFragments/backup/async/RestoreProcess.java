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
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.SUPERSERIALZIED;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.isTapToScroll;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.shoDir;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.toggleTapToScroll;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.deserialize;

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

                textView.post(() -> textView.setText(R.string.reading_file));
                SUPERSERIALZIED superserialzied = (SUPERSERIALZIED) deserialize(string.toString());
                progressBar.post(() -> progressBar.setMax(superserialzied.DBChapters.size() + superserialzied.libraries.size() + superserialzied.DBDownloadItems.size() + 1));

                Log.i("Progress", "Restoring downloads");
                for (com.github.doomsdayrs.apps.shosetsu.backend.database.objects.DBDownloadItem DBDownloadItem : superserialzied.DBDownloadItems) {
                    textView.post(() -> textView.setText("Restoring download: " + DBDownloadItem.CHAPTER_URL));
                    progressBar.post(() -> progressBar.incrementProgressBy(1));
                    DownloadItem downloadItem = new DownloadItem(DefaultScrapers.getByID(DBDownloadItem.FORMATTER_ID), DBDownloadItem.NOVEL_NAME, DBDownloadItem.CHAPTER_NAME, DBDownloadItem.NOVEL_URL, DBDownloadItem.CHAPTER_URL);
                    if (!Database.DatabaseDownloads.inDownloads(downloadItem))
                        Database.DatabaseDownloads.addToDownloads(downloadItem);
                }

                Log.i("Progress", "Restoring libraries");
                for (com.github.doomsdayrs.apps.shosetsu.backend.database.objects.DBNovel DBNovel : superserialzied.libraries) {
                    progressBar.post(() -> progressBar.incrementProgressBy(1));
                    textView.post(() -> textView.setText("Restoring: " + DBNovel.NOVEL_URL));
                    if (!Database.DatabaseLibrary.inLibrary(DBNovel.NOVEL_URL))
                        Database.DatabaseLibrary.addToLibrary(DBNovel.FORMATTER_ID, DBNovel.NOVEL_PAGE, DBNovel.NOVEL_URL, DBNovel.STATUS);

                    com.github.doomsdayrs.apps.shosetsu.variables.enums.Status status;
                    switch (DBNovel.STATUS) {
                        case 0:
                            status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD;
                            break;
                        case 1:
                            status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.READING;
                            break;
                        case 2:
                            status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.READ;
                            break;
                        case 3:
                            status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.ONHOLD;
                            break;
                        case 4:
                            status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.DROPPED;
                            break;
                        default:
                            status = null;
                            break;
                    }
                    if (status != null)
                        Database.DatabaseLibrary.setStatus(DBNovel.NOVEL_URL, status);

                    if (DBNovel.BOOKMARKED)
                        Database.DatabaseLibrary.bookMark(DBNovel.NOVEL_URL);
                }

                Log.i("Progress", "Restoring chapters");
                for (com.github.doomsdayrs.apps.shosetsu.backend.database.objects.DBChapter DBChapter : superserialzied.DBChapters) {
                    textView.post(() -> textView.setText("Restoring: " + DBChapter.CHAPTER_URL));
                    progressBar.post(() -> progressBar.incrementProgressBy(1));
                    if (!Database.DatabaseChapter.inChapters(DBChapter.CHAPTER_URL))
                        Database.DatabaseChapter.addToChapters(DBChapter.NOVEL_URL, DBChapter.CHAPTER_URL, DBChapter.SAVED_DATA);

                    Database.DatabaseChapter.updateY(DBChapter.CHAPTER_URL, DBChapter.Y);

                    com.github.doomsdayrs.apps.shosetsu.variables.enums.Status status;
                    switch (DBChapter.READ_CHAPTER) {
                        case 0:
                            status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD;
                            break;
                        case 1:
                            status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.READING;
                            break;
                        case 2:
                            status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.READ;
                            break;
                        case 3:
                            status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.ONHOLD;
                            break;
                        case 4:
                            status = com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.DROPPED;
                            break;
                        default:
                            status = null;
                            break;
                    }
                    if (status != null)
                        Database.DatabaseChapter.setChapterStatus(DBChapter.CHAPTER_URL, status);

                    if (DBChapter.BOOKMARKED)
                        Database.DatabaseChapter.setBookMark(DBChapter.CHAPTER_URL, 1);

                    //TODO settings backup
                }

                textView.post(() -> textView.setText("Restoring settings"));
                progressBar.post(() -> progressBar.incrementProgressBy(1));
                Settings.ReaderTextColor = superserialzied.settingsSerialized.reader_text_color;
                Settings.ReaderTextBackgroundColor = superserialzied.settingsSerialized.reader_text_background_color;
                shoDir = superserialzied.settingsSerialized.shoDir;
                Settings.downloadPaused = superserialzied.settingsSerialized.paused;
                Settings.ReaderTextSize = superserialzied.settingsSerialized.textSize;
                Settings.themeMode = superserialzied.settingsSerialized.themeMode;
                Settings.paragraphSpacing = superserialzied.settingsSerialized.paraSpace;
                Settings.indentSize = superserialzied.settingsSerialized.indent;

                if (isTapToScroll() != superserialzied.settingsSerialized.tap_to_scroll)
                    toggleTapToScroll();

                progressBar.post(() -> progressBar.incrementProgressBy(1));
                return true;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
