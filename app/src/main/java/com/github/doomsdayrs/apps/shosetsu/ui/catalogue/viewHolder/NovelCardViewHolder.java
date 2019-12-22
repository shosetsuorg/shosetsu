package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async.NovelBackgroundAdd;
import com.github.doomsdayrs.apps.shosetsu.ui.main.MainActivity;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;

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
public class NovelCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    public final ConstraintLayout constraintLayout;
    public final ImageView library_card_image;
    public final TextView library_card_title;
    public CatalogueFragment catalogueFragment;
    public Formatter formatter;
    public String url;
    public int novelID;

    public NovelCardViewHolder(@NonNull View itemView) {
        super(itemView);
        library_card_image = itemView.findViewById(R.id.image);
        library_card_title = itemView.findViewById(R.id.title);
        constraintLayout = itemView.findViewById(R.id.constraint);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        NovelFragment novelFragment = new NovelFragment();
        novelFragment.novelURL = url;
        novelFragment.formatter = formatter;
        novelFragment.novelID = Database.DatabaseIdentification.getNovelIDFromNovelURL(url);
        if (catalogueFragment.getActivity() != null)
            ((MainActivity) catalogueFragment.getActivity()).transitionView(novelFragment);
    }

    @Override
    public boolean onLongClick(View view) {
        new NovelBackgroundAdd(this).execute(view);
        return true;
    }


}