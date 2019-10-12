package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder.NovelCardViewHolder;

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
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
            if (!Database.DatabaseNovels.inLibrary(novelCardsViewHolder.url)) {
                Database.DatabaseNovels.addToLibrary(novelCardsViewHolder.formatter.getID(), novelCardsViewHolder.formatter.parseNovel(novelCardsViewHolder.url), novelCardsViewHolder.url, com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD.getA());
                if (views[0] != null)
                    views[0].post(() -> Toast.makeText(views[0].getContext(), "Added " + novelCardsViewHolder.library_card_title.getText().toString(), Toast.LENGTH_SHORT).show());
            }
            if (Database.DatabaseNovels.isBookmarked(novelCardsViewHolder.url)) {
                if (views[0] != null)
                    views[0].post(() -> Toast.makeText(views[0].getContext(), "Already in the library", Toast.LENGTH_SHORT).show());
            } else {
                Database.DatabaseNovels.bookMark(novelCardsViewHolder.url);
                if (views[0] != null)
                    views[0].post(() -> Toast.makeText(views[0].getContext(), "Added " + novelCardsViewHolder.library_card_title.getText().toString(), Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            if (views[0] != null)
                views[0].post(() -> Toast.makeText(views[0].getContext(), "Failed to add to library: " + novelCardsViewHolder.library_card_title.getText().toString(), Toast.LENGTH_LONG).show());
            String message = novelCardsViewHolder.library_card_title.getText().toString() + " : " + e.getMessage();
            Log.e("NovelBackgroundAdd", message);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        novelCardsViewHolder.catalogueFragment.library_view.post(() -> novelCardsViewHolder.catalogueFragment.catalogueAdapter.notifyDataSetChanged());
    }
}

