package com.github.doomsdayrs.apps.shosetsu.backend;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.DownloadAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.DownloadItemViewHolder;
import com.github.doomsdayrs.apps.shosetsu.ui.main.DownloadsFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.download.DownloadItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class Download_Manager {

    //TODO, make this a variable that can be changed in settings
    public static String shoDir = "/Shosetsu/";
    private static Downloading download = new Downloading();


    public static void init() {
        if (download.isCancelled())
            download = new Downloading();
        download.execute();
    }

    public static void addToDownload(DownloadItem downloadItem) {
        Database.addToDownloads(downloadItem);
        if (download.isCancelled())
            if (Database.getDownloadCount() >= 1) {
                download = new Downloading();
                download.execute();
            }
    }


    public static boolean delete(Context context, DownloadItem downloadItem) {
        File file = new File(shoDir + "/download/" + downloadItem.formatter.getID() + "/" + downloadItem.novelName + "/" + downloadItem.chapterName + ".txt");
        Database.removePath(downloadItem.chapterURL);
        if (file.exists())
            if (!file.delete())
                Toast.makeText(context, "Failed to delete, next download will correct", Toast.LENGTH_LONG).show();


        return true;
    }

    public static String getText(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    static class Downloading extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while (Database.getDownloadCount() >= 1 && !Settings.downloadPaused) {
                DownloadItem downloadItem = Database.getFirstDownload();

                if (DownloadAdapter.contains(downloadItem)) {
                    DownloadItemViewHolder viewHolder = DownloadAdapter.getHolder(downloadItem);
                    if (viewHolder != null)
                        DownloadAdapter.progressToggle(viewHolder);
                }
                if (downloadItem != null)
                    try {
                        Log.d("Dir", shoDir + "download/");
                        File folder = new File(shoDir + "/download/" + downloadItem.formatter.getID() + "/" + downloadItem.novelName);
                        Log.d("Des", folder.toString());
                        if (!folder.exists())
                            if (!folder.mkdirs()) {
                                throw new IOException("Failed to mkdirs");
                            }
                        String formattedName = downloadItem.chapterName.replaceAll("/", "");

                        String passage = downloadItem.formatter.getNovelPassage(downloadItem.chapterURL);
                        FileOutputStream fileOutputStream = new FileOutputStream(
                                (folder.getPath() + "/" + (formattedName) + ".txt")
                        );

                        fileOutputStream.write(passage.getBytes());
                        fileOutputStream.close();
                        Database.addSavedPath(downloadItem.chapterURL, folder.getPath() + "/" + formattedName + ".txt");

                        if (NovelFragmentChapters.recyclerView != null && NovelFragmentChapters.adapter != null)
                            NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());

                        Log.d("Downloaded", "Downloaded:" + downloadItem.novelName + " " + formattedName);
                        Database.removeDownload(downloadItem);


                        if (DownloadAdapter.contains(downloadItem)) {
                            DownloadItemViewHolder viewHolder = DownloadAdapter.getHolder(downloadItem);
                            if (viewHolder != null)
                                DownloadAdapter.progressToggle(viewHolder);
                        }
                        DownloadsFragment.removeDownloads(downloadItem);

                        try {
                            TimeUnit.MILLISECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
            }
            download.cancel(true);
            return null;
        }
    }
}
