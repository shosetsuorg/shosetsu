package com.github.doomsdayrs.apps.shosetsu.ui.migration.async;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationView;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 * shosetsu
 * 05 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class Transfer extends AsyncTask<Void, Void, Void> {
    final ArrayList<String[]> strings;
    final Formatter formatter;
    @SuppressLint("StaticFieldLeak")
    MigrationView migrationView;
    boolean C = true;

    public Transfer(ArrayList<String[]> strings, int target, MigrationView migrationView) {
        this.strings = strings;
        this.migrationView = migrationView;
        formatter = DefaultScrapers.formatters.get(target);
    }

    public void setC(boolean c) {
        C = c;
    }

    @Override
    protected void onCancelled() {
        C = false;
        super.onCancelled();
    }

    @Override
    protected void onPreExecute() {
        migrationView.migration.setVisibility(View.GONE);
        migrationView.progressBar.setVisibility(View.VISIBLE);
        migrationView.output.post(() -> migrationView.output.setText(migrationView.getResources().getText(R.string.starting)));
        if (formatter.isIncrementingChapterList())
            migrationView.pageCount.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (String[] strings : strings)
            if (C) {
                String s = strings[0] + "--->" + strings[1];
                System.out.println(s);
                migrationView.output.post(() -> migrationView.output.setText(s));
                try {
                    NovelPage novelPage = formatter.parseNovel(strings[1]);
                    if (formatter.isIncrementingChapterList()) {
                        int mangaCount = 0;
                        int page = 1;
                        while (page <= novelPage.maxChapterPage && C) {
                            String p = "Page: " + page + "/" + novelPage.maxChapterPage;
                            migrationView.pageCount.post(() -> migrationView.pageCount.setText(p));

                            novelPage = formatter.parseNovel(strings[1], page);
                            for (NovelChapter novelChapter : novelPage.novelChapters)
                                if (C && !Database.DatabaseChapter.inChapters(novelChapter.link)) {
                                    mangaCount++;
                                    System.out.println("Adding #" + mangaCount + ": " + novelChapter.link);

                                    Database.DatabaseChapter.addToChapters(strings[1], novelChapter);
                                }
                            page++;

                            try {
                                TimeUnit.MILLISECONDS.sleep(300);
                            } catch (InterruptedException e) {
                                if (e.getMessage() != null)
                                    Log.e("Interrupt", e.getMessage());
                            }
                        }
                    } else {
                        int mangaCount = 0;
                        for (NovelChapter novelChapter : novelPage.novelChapters)
                            if (C && !Database.DatabaseChapter.inChapters(novelChapter.link)) {
                                mangaCount++;
                                System.out.println("Adding #" + mangaCount + ": " + novelChapter.link);
                                Database.DatabaseChapter.addToChapters(strings[1], novelChapter);
                            }
                    }
                    if (C) {
                        migrationView.pageCount.post(() -> migrationView.pageCount.setText(""));
                        Database.DatabaseLibrary.migrateNovel(strings[0], strings[1], formatter.getID(), novelPage, Database.DatabaseLibrary.getStatus(strings[0]).getA());
                    }
                } catch (IOException e) {
                    if (e.getMessage() != null)
                        Log.e("Interrupt", e.getMessage());
                }
            }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        LibraryFragment.changedData = true;
        if (migrationView != null) {
            migrationView.finish();
        }
    }
}
