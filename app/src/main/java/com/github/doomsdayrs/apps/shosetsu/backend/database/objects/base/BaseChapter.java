package com.github.doomsdayrs.apps.shosetsu.backend.database.objects.base;

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
 */public class BaseChapter extends Base {
    public final String CHAPTER_URL;

    public BaseChapter(String novel_url, String chapter_url) {
        super(novel_url);
        CHAPTER_URL = chapter_url;
    }

    @Override
    public String toString() {
        return "BaseChapter{" +
                "CHAPTER_URL='" + CHAPTER_URL + '\'' +
                ", NOVEL_URL='" + NOVEL_URL + '\'' +
                '}';
    }
}
