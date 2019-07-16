package com.github.doomsdayrs.apps.shosetsu.variables;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;

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
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */

/**
 * These items symbolize download items and their data
 */
public class DownloadItem {

    public final Formatter formatter;
    public final String novelName;
    public final String chapterName;
    public final String novelURL;
    public final String chapterURL;

    //Variables only for download manager

    private String status = "Pending";

    /**
     * Constructor
     *
     * @param formatter   formatter to work with
     * @param novelName   name of the novel
     * @param chapterName name of the chapter
     * @param novelURL    NovelURL
     * @param chapterURL  ChapterURL
     */
    public DownloadItem(Formatter formatter, String novelName, String chapterName, String novelURL, String chapterURL) {
        this.formatter = formatter;
        this.novelName = cleanse(novelName);
        this.chapterName = cleanse(chapterName);
        this.novelURL = novelURL;
        this.chapterURL = chapterURL;
    }

    /**
     * Returns the status
     *
     * @return Status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the download item
     *
     * @param status status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }


    /**
     * Cleans up the names to be functional in file system and DB
     *
     * @param s string to clean
     * @return cleaned string
     */
    public static String cleanse(String s) {
        //Log.d("Cleaning", s);
        s = s.replaceAll("'", "_").replaceAll("\"", "_");
        // Log.d("Cleaned", s);
        return s;
    }
}
