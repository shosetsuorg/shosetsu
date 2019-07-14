package com.github.doomsdayrs.apps.shosetsu.backend;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.main.DownloadsFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

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
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class Download_Manager {

    public static String shoDir = "/Shosetsu/";
    private static Downloading download = new Downloading();

    /**
     * Initializes download manager
     */
    public static void init() {
        if (download.isCancelled())
            download = new Downloading();
        if (download.getStatus().equals(AsyncTask.Status.FINISHED) || download.getStatus().equals(AsyncTask.Status.PENDING))
            download.execute();
    }

    /**
     * Adds to download list
     *
     * @param downloadItem download item to add
     */
    public static void addToDownload(DownloadItem downloadItem) {
        if (!Database.DatabaseDownloads.inDownloads(downloadItem)) {
            Database.DatabaseDownloads.addToDownloads(downloadItem);
            if (download.isCancelled())
                if (Database.DatabaseDownloads.getDownloadCount() >= 1) {
                    download = new Downloading();
                    download.execute();
                }
        }
    }

    /**
     * delete downloaded chapter
     *
     * @param context      context to work with
     * @param downloadItem download item to remove
     * @return if downloaded
     */
    public static boolean delete(Context context, DownloadItem downloadItem) {
        Log.d("DeletingChapter", downloadItem.toString());
        File file = new File(shoDir + "/download/" + downloadItem.formatter.getID() + "/" + downloadItem.novelName + "/" + downloadItem.chapterName + ".txt");
        Database.DatabaseChapter.removePath(downloadItem.chapterURL);
        if (file.exists())
            if (!file.delete())
                if (context != null)
                    Toast.makeText(context, "Failed to delete, next download will correct", Toast.LENGTH_LONG).show();
        return true;
    }

    /**
     * Get saved text
     *
     * @param path path of saved chapter
     * @return Passage of saved chapter
     */
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


    /**
     * Download loop controller
     * TODO Notification of download progress (( What is being downloaded
     * TODO Skip over paused chapters or move them to the bottom of the list
     */
    static class Downloading extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while (Database.DatabaseDownloads.getDownloadCount() >= 1 && !Settings.downloadPaused) {

                DownloadItem downloadItem = Database.DatabaseDownloads.getFirstDownload();
                // Starts up
                DownloadsFragment.toggleProcess(downloadItem);
                if (downloadItem != null)
                    try {
                        {
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
                            Database.DatabaseChapter.addSavedPath(downloadItem.chapterURL, folder.getPath() + "/" + formattedName + ".txt");

                            if (NovelFragmentChapters.recyclerView != null && NovelFragmentChapters.adapter != null)
                                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());

                            Log.d("Downloaded", "Downloaded:" + downloadItem.novelName + " " + formattedName);
                        }

                        // Clean up
                        Database.DatabaseDownloads.removeDownload(downloadItem);
                        DownloadsFragment.toggleProcess(downloadItem);
                        DownloadsFragment.removeDownloads(downloadItem);

                        // Rate limiting
                        try {
                            TimeUnit.MILLISECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } catch (SocketTimeoutException e) {
                        // Mark download as faulted
                        DownloadsFragment.markError(downloadItem);
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
