package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async.NovelBackgroundAdd;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;

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
public class NovelCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    public final ConstraintLayout constraintLayout;
    public final ImageView library_card_image;
    public final TextView library_card_title;
    public CatalogueFragment catalogueFragment;
    public Formatter formatter;
    public String url;

    public NovelCardViewHolder(@NonNull View itemView) {
        super(itemView);
        library_card_image = itemView.findViewById(R.id.novel_item_image);
        library_card_title = itemView.findViewById(R.id.textView);
        constraintLayout = itemView.findViewById(R.id.constraint);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        NovelFragment novelFragment = new NovelFragment();
        StaticNovel.formatter = formatter;
        StaticNovel.novelURL = url;

        if (catalogueFragment.getFragmentManager() != null)
            catalogueFragment.getFragmentManager().beginTransaction()
                    .addToBackStack("tag")
                    .replace(R.id.fragment_container, novelFragment)
                    .commit();
    }

    @Override
    public boolean onLongClick(View view) {
        new NovelBackgroundAdd(this).execute(view);
        return true;
    }


}