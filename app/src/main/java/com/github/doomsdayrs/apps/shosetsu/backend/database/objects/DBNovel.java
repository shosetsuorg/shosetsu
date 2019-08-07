package com.github.doomsdayrs.apps.shosetsu.backend.database.objects;

import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.base.Base;

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
 */
public class DBNovel extends Base {
    public final boolean BOOKMARKED;

    /**
     * Serialized NovelPage object in string form, must be deserialized for use
     */
    public final String NOVEL_PAGE;

    public final int FORMATTER_ID;
    public final int STATUS;

    public DBNovel(String novel_url, boolean bookmarked, String novel_page, int formatter_id, int status) {
        super(novel_url);
        BOOKMARKED = bookmarked;
        NOVEL_PAGE = novel_page;
        FORMATTER_ID = formatter_id;
        STATUS = status;
    }

    @Override
    public String toString() {
        return "Library{" +
                "BOOKMARKED=" + BOOKMARKED +
                ", NOVEL_PAGE='" + NOVEL_PAGE + '\'' +
                ", FORMATTER_ID=" + FORMATTER_ID +
                ", STATUS=" + STATUS +
                ", NOVEL_URL='" + NOVEL_URL + '\'' +
                '}';
    }
}
