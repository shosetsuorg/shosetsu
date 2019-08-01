package com.github.doomsdayrs.apps.shosetsu.variables;

import com.github.Doomsdayrs.api.novelreader_core.extensions.lang.en.box_novel.BoxNovel;
import com.github.Doomsdayrs.api.novelreader_core.extensions.lang.en.novel_full.NovelFull;
import com.github.Doomsdayrs.api.novelreader_core.extensions.lang.en.syosetu.Syosetu;
import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelGenre;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

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
 * Shosetsu
 * 30 / May / 2019
 *
 * @author github.com/doomsdayrs
 */

/**
 * Moved from deprecated novelreader-core
 * Contains functional novels and their IDs
 */
// TODO Make this full dynamic, not needing to be predefined
// > Make IDs built into the formatter
public enum DefaultScrapers implements Formatter {
    NOVELFULL(new NovelFull(1)),
    BOXNOVEL(new BoxNovel(2)),
    SYOSETU(new Syosetu(3)),
    NOVELPLANENT(new NovelPlanet(4));

    public static final ArrayList<Formatter> formatters = new ArrayList<>();

    static {
        formatters.add(NOVELFULL);
        formatters.add(BOXNOVEL);
        formatters.add(SYOSETU);
        formatters.add(NOVELPLANENT);
    }

    private final Formatter formatter;

    DefaultScrapers(Formatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void setBuilder(Request.Builder builder) {
        formatter.setBuilder(builder);
    }

    @Override
    public void setClient(OkHttpClient okHttpClient) {
        formatter.setClient(okHttpClient);
    }

    @Override
    public boolean hasCloudFlare() {
        return formatter.hasCloudFlare();
    }

    @Override
    public String getName() {
        return formatter.getName();
    }

    @Override
    public String getImageURL() {
        return formatter.getImageURL();
    }

    @Override
    public int getID() {
        return formatter.getID();
    }

    @Override
    public boolean hasSearch() {
        return formatter.hasSearch();
    }

    @Override
    public boolean hasGenres() {
        return formatter.hasGenres();
    }

    public boolean isIncrementingChapterList() {
        return formatter.isIncrementingChapterList();
    }

    @Override
    public boolean isIncrementingPassagePage() {
        return formatter.isIncrementingPassagePage();
    }

    @Override
    public Ordering chapterOrder() {
        return formatter.chapterOrder();
    }

    @Override
    public Ordering latestOrder() {
        return formatter.latestOrder();
    }

    public String getNovelPassage(String URL) throws IOException {
        return formatter.getNovelPassage(URL);
    }

    public NovelPage parseNovel(String URL) throws IOException {
        return formatter.parseNovel(URL);
    }

    public NovelPage parseNovel(String URL, int increment) throws IOException {
        return formatter.parseNovel(URL, increment);
    }

    public String getLatestURL(int page) {
        return formatter.getLatestURL(page);
    }

    public List<Novel> parseLatest(String URL) throws IOException {
        return formatter.parseLatest(URL);
    }

    @Override
    public List<Novel> search(String query) throws IOException {
        return formatter.search(query);
    }

    @Override
    public NovelGenre[] getGenres() {
        return formatter.getGenres();
    }
}
