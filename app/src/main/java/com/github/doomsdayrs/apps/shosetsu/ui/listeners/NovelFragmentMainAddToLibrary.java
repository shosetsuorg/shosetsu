package com.github.doomsdayrs.apps.shosetsu.ui.listeners;

import android.view.View;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentMain;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelFragmentMainAddToLibrary implements FloatingActionButton.OnClickListener {
    private final NovelFragmentMain novelFragmentMain;

    public NovelFragmentMainAddToLibrary(NovelFragmentMain novelFragmentMain) {
        this.novelFragmentMain = novelFragmentMain;
    }

    @Override
    public void onClick(View v) {
        if (!novelFragmentMain.inLibrary) {
            if (!Database.DatabaseLibrary.inLibrary(StaticNovel.novelURL))
                Database.DatabaseLibrary.addToLibrary(StaticNovel.formatter.getID(), StaticNovel.novelPage, StaticNovel.novelURL, StaticNovel.status.getA());
            Database.DatabaseLibrary.bookMark(StaticNovel.novelURL);
            novelFragmentMain.inLibrary = true;
            novelFragmentMain.floatingActionButton.setImageResource(R.drawable.ic_add_circle_black_24dp);
        } else {
            Database.DatabaseLibrary.unBookmark(StaticNovel.novelURL);
                novelFragmentMain.inLibrary = false;
                novelFragmentMain.floatingActionButton.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);

        }
    }
}