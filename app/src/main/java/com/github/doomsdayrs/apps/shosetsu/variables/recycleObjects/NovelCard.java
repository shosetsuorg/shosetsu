package com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects;

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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelCard extends RecycleCard {
    /**
     * NovelURL
     */
    public final String novelURL;
    /**
     * ImageURL
     */
    public final String imageURL;
    /**
     * ID of formatter
     */
    public final int formatterID;

    /**
     * Constructor
     *
     * @param title       title
     * @param novelURL    novelURL
     * @param imageURL    imageURL
     * @param formatterID id of formatter
     */
    public NovelCard(String title, String novelURL, String imageURL, int formatterID) {
        super(title);
        this.novelURL = novelURL;
        this.imageURL = imageURL;
        this.formatterID = formatterID;
    }


}
