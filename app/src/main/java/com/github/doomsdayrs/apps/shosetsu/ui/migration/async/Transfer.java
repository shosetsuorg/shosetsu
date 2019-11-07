package com.github.doomsdayrs.apps.shosetsu.ui.migration.async;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationView;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getNovelIDFromNovelURL;

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 * shosetsu
 * 05 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class Transfer extends AsyncTask<Void, Void, Void> {
    private final ArrayList<String[]> strings;
    private final Formatter formatter;
    @SuppressLint("StaticFieldLeak")
    private
    MigrationView migrationView;
    private boolean C = true;

    public Transfer(ArrayList<String[]> strings, int target, MigrationView migrationView) {
        this.strings = strings;
        this.migrationView = migrationView;
        formatter = DefaultScrapers.getByID(target);
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
                            int novelID = getNovelIDFromNovelURL(strings[1]);
                            for (NovelChapter novelChapter : novelPage.novelChapters)
                                if (C && !Database.DatabaseChapter.inChapters(novelChapter.link)) {
                                    mangaCount++;
                                    System.out.println("Adding #" + mangaCount + ": " + novelChapter.link);

                                    Database.DatabaseChapter.addToChapters(novelID, novelChapter);
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
                        int novelID = getNovelIDFromNovelURL(strings[1]);
                        for (NovelChapter novelChapter : novelPage.novelChapters)
                            if (C && !Database.DatabaseChapter.inChapters(novelChapter.link)) {
                                mangaCount++;
                                System.out.println("Adding #" + mangaCount + ": " + novelChapter.link);
                                Database.DatabaseChapter.addToChapters(novelID, novelChapter);
                            }
                    }
                    if (C) {
                        migrationView.pageCount.post(() -> migrationView.pageCount.setText(""));
                        int oldID = getNovelIDFromNovelURL(strings[0]);
                        Database.DatabaseNovels.migrateNovel(oldID, strings[1], formatter.getID(), novelPage, Database.DatabaseNovels.getStatus(oldID).getA());
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
