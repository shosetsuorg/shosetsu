package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder.NovelCardViewHolder;

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
 * 06 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelBackgroundAdd extends AsyncTask<View, Void, Void> {
    private NovelCardViewHolder novelCardsViewHolder;

    public NovelBackgroundAdd(NovelCardViewHolder novelCardsViewHolder) {
        this.novelCardsViewHolder = novelCardsViewHolder;
    }

    @Override
    protected Void doInBackground(View... views) {
        try {
            if (!Database.DatabaseLibrary.inLibrary(novelCardsViewHolder.url)) {
                Database.DatabaseLibrary.addToLibrary(novelCardsViewHolder.formatter.getID(), novelCardsViewHolder.formatter.parseNovel(novelCardsViewHolder.url), novelCardsViewHolder.url, com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD.getA());
                if (views[0] != null)
                    views[0].post(() -> Toast.makeText(views[0].getContext(), "Added " + novelCardsViewHolder.library_card_title.getText().toString(), Toast.LENGTH_SHORT).show());
            }
            if (Database.DatabaseLibrary.isBookmarked(novelCardsViewHolder.url)) {
                if (views[0] != null)
                    views[0].post(() -> Toast.makeText(views[0].getContext(), "Already in the library", Toast.LENGTH_SHORT).show());
            } else {
                Database.DatabaseLibrary.bookMark(novelCardsViewHolder.url);
                if (views[0] != null)
                    views[0].post(() -> Toast.makeText(views[0].getContext(), "Added " + novelCardsViewHolder.library_card_title.getText().toString(), Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            if (views[0] != null)
                views[0].post(() -> Toast.makeText(views[0].getContext(), "Failed to add to library: " + novelCardsViewHolder.library_card_title.getText().toString(), Toast.LENGTH_LONG).show());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        novelCardsViewHolder.catalogueFragment.library_view.post(() -> novelCardsViewHolder.catalogueFragment.catalogueAdapter.notifyDataSetChanged());
    }
}

