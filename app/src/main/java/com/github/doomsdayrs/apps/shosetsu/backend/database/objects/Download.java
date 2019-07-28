package com.github.doomsdayrs.apps.shosetsu.backend.database.objects;

import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.base.BaseChapter;

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
 * 27 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */public class Download extends BaseChapter {
    public final int FORMATTER_ID;
    public final String NOVEL_NAME;
    public final String CHAPTER_NAME;
    public final boolean PAUSED;

    public Download(String novel_url, String chapter_url, int formatter_id, String novel_name, String chapter_name, boolean paused) {
        super(novel_url, chapter_url);
        FORMATTER_ID = formatter_id;
        NOVEL_NAME = novel_name;
        CHAPTER_NAME = chapter_name;
        PAUSED = paused;
    }

    @Override
    public String toString() {
        return "Download{" +
                "FORMATTER_ID=" + FORMATTER_ID +
                ", NOVEL_NAME='" + NOVEL_NAME + '\'' +
                ", CHAPTER_NAME='" + CHAPTER_NAME + '\'' +
                ", PAUSED=" + PAUSED +
                ", CHAPTER_URL='" + CHAPTER_URL + '\'' +
                ", NOVEL_URL='" + NOVEL_URL + '\'' +
                '}';
    }
}
