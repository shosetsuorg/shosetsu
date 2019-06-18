package com.github.doomsdayrs.apps.shosetsu.download;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.github.doomsdayrs.apps.shosetsu.database.Database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

    private static final String downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();


    private static final ArrayList<DownloadItem> urlsToDownload = new ArrayList<>();


    public static void addToDownload(DownloadItem downloadItem) {
        urlsToDownload.add(0, downloadItem);
        if (urlsToDownload.size() == 1)
            new downloading().execute();
    }

    public static boolean delete(DeleteItem deleteItem) {
        File file = new File(downloadDir + "/Shosetsu/" + deleteItem.formatter.getID() + "/" + deleteItem.novelName + "/" + deleteItem.chapterName + ".txt");
        if (file.exists()) {
            if (file.delete()) {
                return Database.removePath(deleteItem.novelURL, deleteItem.chapterURL);
            }
        }
        return false;
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


    static class downloading extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while (urlsToDownload.size() > 0) {
                DownloadItem downloadItem = urlsToDownload.get(urlsToDownload.size() - 1);
                try {
                    Log.d("Dir", downloadDir);
                    File folder = new File(downloadDir + "/Shosetsu/" + downloadItem.formatter.getID() + "/" + downloadItem.novelName);
                    Log.d("Des", folder.toString());
                    if (!folder.exists())
                        if (!folder.mkdirs()) {
                            throw new IOException("Failed to mkdirs");
                        }

                    String passage = downloadItem.formatter.getNovelPassage(downloadItem.chapterURL);
                    FileOutputStream fileOutputStream = new FileOutputStream(
                            (folder.getPath() + "/" + downloadItem.chapterName + ".txt")
                    );

                    fileOutputStream.write(passage.getBytes());
                    fileOutputStream.close();
                    Database.addSavedPath(downloadItem.novelURL, downloadItem.chapterURL, folder.getPath() + "/" + downloadItem.chapterName + ".txt");
                    if (downloadItem.novelFragmentChapters != null) {
                        downloadItem.novelFragmentChapters.recyclerView.post(() -> downloadItem.novelFragmentChapters.adapter.notifyDataSetChanged());
                    }
                    Log.d("Downloaded", "Downloaded:" + downloadItem.novelName + " " + downloadItem.chapterName);
                    urlsToDownload.remove(urlsToDownload.size() - 1);
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
            return null;
        }
    }
}
