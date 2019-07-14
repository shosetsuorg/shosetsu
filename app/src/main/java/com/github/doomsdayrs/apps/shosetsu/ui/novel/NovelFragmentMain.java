package com.github.doomsdayrs.apps.shosetsu.ui.novel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.NovelFragmentMainAddToLibrary;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.NovelFragmentUpdate;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Shosetsu is distributed in the hope that it will be useful,
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
 */
public class NovelFragmentMain extends Fragment {

    private ImageView imageView;
    private TextView title;
    private TextView authors;
    private TextView artists;
    private TextView description;
    private TextView formatterName;
    private TextView status;
    private ChipGroup genres;
    public SwipeRefreshLayout swipeRefreshLayout;

    public FloatingActionButton floatingActionButton;
    public boolean inLibrary = false;

    public NovelFragment novelFragment;

    /**
     * Constructor
     */
    public NovelFragmentMain(NovelFragment novelFragment) {
        this.novelFragment = novelFragment;
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
            authors = view.findViewById(R.id.fragment_novel_author);
            artists = view.findViewById(R.id.fragment_novel_artists);
            genres = view.findViewById(R.id.fragment_novel_genres);
            description = view.findViewById(R.id.fragment_novel_description);
            formatterName = view.findViewById(R.id.fragment_novel_formatter);
            floatingActionButton = view.findViewById(R.id.fragment_novel_add);
            status = view.findViewById(R.id.fragment_novel_status);

            swipeRefreshLayout = view.findViewById(R.id.fragment_novel_main_refresh);
        }

        floatingActionButton.hide();


        if (Database.DatabaseLibrary.isBookmarked(StaticNovel.novelURL))
            inLibrary();

        if (inLibrary)
            floatingActionButton.setImageResource(R.drawable.ic_add_circle_black_24dp);

        if (StaticNovel.novelPage != null && title != null)
            setData();

        floatingActionButton.setOnClickListener(new NovelFragmentMainAddToLibrary(this));
        swipeRefreshLayout.setOnRefreshListener(new NovelFragmentUpdate(this));

        return view;
    }

    /**
     * Sets the data of this page
     */
    public void setData() {
        if (StaticNovel.novelPage == null) {
            Log.e("NULL", "Invalid novel page");
            return;
        }

        title.setText(StaticNovel.novelPage.title);

        if (StaticNovel.novelPage.authors != null && StaticNovel.novelPage.authors.length > 0)
            authors.setText(Arrays.toString(StaticNovel.novelPage.authors));

        description.setText(StaticNovel.novelPage.description);

        if (StaticNovel.novelPage.artists != null && StaticNovel.novelPage.artists.length > 0)
            artists.setText(Arrays.toString(StaticNovel.novelPage.artists));

        status.setText(StaticNovel.status.getStatus());

        if (StaticNovel.novelPage.genres != null && getContext() != null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            for (String string : StaticNovel.novelPage.genres) {
                Chip chip = (Chip) layoutInflater.inflate(R.layout.genre_chip, null, false);
                chip.setText(string);
                genres.addView(chip);
            }
        } else genres.setVisibility(View.GONE);

        Picasso.get()
                .load(StaticNovel.novelPage.imageURL)
                .into(imageView);

        floatingActionButton.show();
        formatterName.setText(StaticNovel.formatter.getName());
    }

}
