package com.github.doomsdayrs.apps.shosetsu.backend;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.variables.download.DeleteItem;
import com.github.doomsdayrs.apps.shosetsu.variables.download.DownloadItem;

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

    //TODO, make this a variable that can be changed in settings
    public static String shoDir = "/Shosetsu/";


    private static final ArrayList<DownloadItem> urlsToDownload = new ArrayList<>();


    public static void addToDownload(DownloadItem downloadItem) {
        urlsToDownload.add(0, downloadItem);
        if (urlsToDownload.size() == 1)
            new downloading().execute();
    }

    public static boolean delete(Context context, DeleteItem deleteItem) {
        File file = new File(shoDir + "/download/" + deleteItem.formatter.getID() + "/" + deleteItem.novelName + "/" + deleteItem.chapterName + ".txt");
        Database.removePath(deleteItem.chapterURL);
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


    static class downloading extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while (urlsToDownload.size() > 0) {
                DownloadItem downloadItem = urlsToDownload.get(urlsToDownload.size() - 1);
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
                    if (downloadItem.novelFragmentChapters != null) {
                        downloadItem.novelFragmentChapters.recyclerView.post(() -> downloadItem.novelFragmentChapters.adapter.notifyDataSetChanged());
                    }
                    Log.d("Downloaded", "Downloaded:" + downloadItem.novelName + " " + formattedName);
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
