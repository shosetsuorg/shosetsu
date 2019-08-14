package com.github.doomsdayrs.apps.shosetsu.ui.updates.async;

import android.os.AsyncTask;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

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
 * 13 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class ChapterUpdater extends AsyncTask<Void, Void, Void> {
    private NovelPage tempPage;
    private ArrayList<NovelCard> novelCards;
    private boolean continueProcesss = true;

    public ChapterUpdater(@NotNull ArrayList<NovelCard> novelCards) {
        this.novelCards = novelCards;
    }

    public void setContinueProcesss(boolean continueProcesss) {
        this.continueProcesss = continueProcesss;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            for (NovelCard novelCard : novelCards) {
                Formatter formatter = DefaultScrapers.getByID(novelCard.formatterID);
                if (formatter != null) {
                    int page = 1;
                    tempPage = formatter.parseNovel(novelCard.novelURL, page);
                    if (formatter.isIncrementingChapterList()) {
                        int mangaCount = 0;
                        while (page <= tempPage.maxChapterPage && continueProcesss) {
                            // TODO Notify a download process
                            tempPage = formatter.parseNovel(novelCard.novelURL, page);
                            for (NovelChapter novelChapter : tempPage.novelChapters)
                                add(mangaCount, novelChapter);
                            page++;
                            Utilities.wait(300);
                        }
                    } else {
                        tempPage = formatter.parseNovel(novelCard.novelURL, page);
                        int mangaCount = 0;
                        for (NovelChapter novelChapter : tempPage.novelChapters)
                            add(mangaCount, novelChapter);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void add(int mangaCount, NovelChapter novelChapter) {
        if (continueProcesss && !Database.DatabaseChapter.inChapters(novelChapter.link)) {
            mangaCount++;
            System.out.println("Adding #" + mangaCount + ": " + novelChapter.link);
            Database.DatabaseChapter.addToChapters(StaticNovel.novelURL, novelChapter);
        }
    }


}
