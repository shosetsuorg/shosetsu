package com.github.Doomsdayrs.apps.shosetsu.fragment.novel;

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
import android.widget.Toast;

import com.github.Doomsdayrs.api.novelreader_core.main.DefaultScrapers;
import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.database.Database;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Arrays;

/**
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
public class NovelFragmentMain extends Fragment {
    public Formatter formatter;
    public String url;
    NovelPage novelPage;
    ImageView imageView;
    TextView title;
    TextView author;
    TextView description;
    FloatingActionButton floatingActionButton;

    boolean inLibrary = false;

    public NovelFragmentMain() {
        setHasOptionsMenu(true);
    }

    public void inLibary() {
        this.inLibrary = true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("Saving Instance State", "NovelFragmentMain");
        outState.putString("imageURL", url);
        outState.putInt("formatter", formatter.getID());
        outState.putSerializable("page", novelPage);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreate", "NovelFragmentMain");
        View view = inflater.inflate(R.layout.fragment_novel_main, container, false);
        {
            imageView = view.findViewById(R.id.fragment_novel_image);
            title = view.findViewById(R.id.fragment_novel_title);
            author = view.findViewById(R.id.fragment_novel_author);
            description = view.findViewById(R.id.fragment_novel_description);
            floatingActionButton = view.findViewById(R.id.fragment_novel_add);
            floatingActionButton.setOnClickListener(new addToLibrary(this));
        }

        if (Database.inLibrary(url))
            inLibary();

        if (inLibrary)
            floatingActionButton.setImageResource(R.drawable.ic_add_circle_black_24dp);

        if (savedInstanceState != null) {
            url = savedInstanceState.getString("imageURL");
            formatter = DefaultScrapers.formatters.get(savedInstanceState.getInt("formatter") - 1);
            novelPage = (NovelPage) savedInstanceState.getSerializable("page");
        }

        {
            if (novelPage == null)
                System.exit(1);
            title.setText(novelPage.title);
            author.setText(Arrays.toString(novelPage.authors));
            description.setText(novelPage.description);
            NovelFragmentChapters.novelChapters = novelPage.novelChapters;
        }
        Picasso.get()
                .load(novelPage.imageURL)
                .into(imageView);
        Log.d("OnCreate", "NovelFragmentMain Complete");
        return view;
    }

    static class addToLibrary implements FloatingActionButton.OnClickListener {
        NovelFragmentMain novelFragmentMain;

        public addToLibrary(NovelFragmentMain novelFragmentMain) {
            this.novelFragmentMain = novelFragmentMain;
        }

        @Override
        public void onClick(View v) {
            if (!novelFragmentMain.inLibrary) {
                if (Database.addToLibrary(novelFragmentMain.formatter.getID(), novelFragmentMain.novelPage, novelFragmentMain.url, new JSONObject())) {
                    novelFragmentMain.inLibrary = true;
                    novelFragmentMain.floatingActionButton.setImageResource(R.drawable.ic_add_circle_black_24dp);
                } else
                    Toast.makeText(v.getContext(), "Error adding to library", Toast.LENGTH_LONG).show();

            } else {
                if (!Database.removeFromLibrary(novelFragmentMain.url)) {
                    novelFragmentMain.inLibrary = false;
                    novelFragmentMain.floatingActionButton.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
                } else
                    Toast.makeText(v.getContext(), "Error removing from library", Toast.LENGTH_LONG).show();
            }
        }
    }
}
