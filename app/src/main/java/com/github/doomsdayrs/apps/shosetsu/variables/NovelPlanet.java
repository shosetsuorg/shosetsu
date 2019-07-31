package com.github.doomsdayrs.apps.shosetsu.variables;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.ScrapeFormat;
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
 * novelreader-extensions
 * 11 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
//TODO Cloudflare intercept
@Deprecated
public class NovelPlanet extends ScrapeFormat {
    public NovelPlanet(int id) {
        super(id);
        hasCloudFlare(true);
    }

    public NovelPlanet(int id, Request.Builder builder) {
        super(id, builder);
        hasCloudFlare(true);
    }

    public NovelPlanet(int id, OkHttpClient client) {
        super(id, client);
        hasCloudFlare(true);
    }

    public NovelPlanet(int id, Request.Builder builder, OkHttpClient client) {
        super(id, builder, client);
        hasCloudFlare(true);
    }

    @Override
    public String getName() {
        return "NovelPlanet";
    }

    @Override
    public String getImageURL() {
        return "https://novelplanet.com/Content/images/logoHeader.png";
    }


    @Override
    public String getNovelPassage(String s) throws IOException {
        return null;
    }

    @Override
    public NovelPage parseNovel(String s) throws IOException {
        return null;
    }

    @Override
    public NovelPage parseNovel(String s, int i) throws IOException {
        return null;
    }

    @Override
    public String getLatestURL(int i) {
        if (i == 0)
            i = 1;
        return "https://novelplanet.com/NovelList?order=latestupdate&page=" + i;
    }

    @Override
    public List<Novel> parseLatest(String s) throws IOException {
        return new ArrayList<>();
    }

    @Override
    public List<Novel> search(String s) throws IOException {
        return new ArrayList<>();
    }

    @Override
    public NovelGenre[] getGenres() {
        return new NovelGenre[0];
    }
}
