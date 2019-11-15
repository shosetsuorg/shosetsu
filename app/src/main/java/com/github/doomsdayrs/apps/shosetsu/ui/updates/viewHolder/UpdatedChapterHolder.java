package com.github.doomsdayrs.apps.shosetsu.ui.updates.viewHolder;
/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;
import com.squareup.picasso.Picasso;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.openChapter;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getChapterIDFromChapterURL;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getFormatterIDFromNovelURL;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getNovelIDFromNovelURL;

/**
 * shosetsu
 * 17 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class UpdatedChapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final ImageView moreOptions;
    public final TextView downloadTag;
    public NovelChapter novelChapter;
    public final TextView title;
    private final ImageView image;
    public PopupMenu popupMenu;

    public UpdatedChapterHolder(@NonNull View itemView) {
        super(itemView);
        moreOptions = itemView.findViewById(R.id.more_options);
        downloadTag = itemView.findViewById(R.id.recycler_novel_chapter_download);
        title = itemView.findViewById(R.id.title);
        image = itemView.findViewById(R.id.image);

        if (popupMenu == null) {
            popupMenu = new PopupMenu(moreOptions.getContext(), moreOptions);
            popupMenu.inflate(R.menu.popup_chapter_menu);
        }


    }

    public void setNovelChapter(@NonNull NovelChapter novelChapter) {
        this.novelChapter = novelChapter;
        title.setText(novelChapter.title);
        //TODO fix this disgust
        NovelCard novelCard = Database.DatabaseNovels.getNovel(Database.DatabaseIdentification.getNovelIDFromChapterID(getChapterIDFromChapterURL(novelChapter.link)));
        Picasso.get()
                .load(
                        novelCard.imageURL)
                .into(image);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String nurl = Database.DatabaseIdentification.getNovelURLFromChapterURL(novelChapter.link);
        Formatter formatter = DefaultScrapers.getByID(getFormatterIDFromNovelURL(nurl));
        if (formatter != null)
            openChapter((Activity) itemView.getContext(), novelChapter, getNovelIDFromNovelURL(nurl), formatter.getID());
    }
}
