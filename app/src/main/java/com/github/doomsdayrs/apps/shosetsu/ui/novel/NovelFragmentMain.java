package com.github.doomsdayrs.apps.shosetsu.ui.novel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.NovelFragmentMainAddToLibrary;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see https://www.gnu.org/licenses/ .
 * ====================================================================
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */

/**
 * The page you see when you select a novel
 * TODO Swipe from top of screen downwards to update the data of this novel
 */
public class NovelFragmentMain extends Fragment {


    private ImageView imageView;
    private TextView title;
    private TextView author;
    private TextView description;
    private TextView formatterName;
    public FloatingActionButton floatingActionButton;
    public boolean inLibrary = false;

    /**
     * Constructor
     */
    public NovelFragmentMain() {
        setHasOptionsMenu(true);
    }

    /**
     * Tells this file that it is already in the library
     */
    private void inLibrary() {
        this.inLibrary = true;
    }

    /**
     * Save data of view before destroyed
     *
     * @param outState output save
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("Saving Instance State", "NovelFragmentMain");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreateView", "NovelFragmentMain");
        View view = inflater.inflate(R.layout.fragment_novel_main, container, false);
        {
            imageView = view.findViewById(R.id.fragment_novel_image);
            title = view.findViewById(R.id.fragment_novel_title);
            author = view.findViewById(R.id.fragment_novel_author);
            description = view.findViewById(R.id.fragment_novel_description);
            formatterName = view.findViewById(R.id.fragment_novel_formatter);
            floatingActionButton = view.findViewById(R.id.fragment_novel_add);
            floatingActionButton.setOnClickListener(new NovelFragmentMainAddToLibrary(this));
        }
        floatingActionButton.hide();


        if (Database.DatabaseLibrary.inLibrary(StaticNovel.novelURL))
            inLibrary();

        if (inLibrary)
            floatingActionButton.setImageResource(R.drawable.ic_add_circle_black_24dp);

        if (StaticNovel.novelPage != null && title != null)
            setData();

        return view;
    }

    /**
     * Sets the data of this page
     */
    public void setData() {
        title.setText(StaticNovel.novelPage.title);
        author.setText(Arrays.toString(StaticNovel.novelPage.authors));
        description.setText(StaticNovel.novelPage.description);
        Picasso.get()
                .load(StaticNovel.novelPage.imageURL)
                .into(imageView);
        floatingActionButton.show();
        formatterName.setText(StaticNovel.formatter.getName());
    }

}
