package com.github.doomsdayrs.apps.shosetsu.fragment.novel;

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
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.database.Database;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
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
    private ImageView imageView;
    private TextView title;
    private TextView author;
    private TextView description;
    private TextView formatterName;
    private FloatingActionButton floatingActionButton;

    private boolean inLibrary = false;

    public NovelFragmentMain() {
        setHasOptionsMenu(true);
    }

    private void inLibrary() {
        this.inLibrary = true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("Saving Instance State", "NovelFragmentMain");
        outState.putString("imageURL", url);
        outState.putInt("formatter", formatter.getID());
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
            floatingActionButton.setOnClickListener(new addToLibrary(this));
        }
        floatingActionButton.hide();

        if (savedInstanceState != null) {
            url = savedInstanceState.getString("imageURL");
            formatter = DefaultScrapers.formatters.get(savedInstanceState.getInt("formatter") - 1);
        }


        if (Database.inLibrary(url))
            inLibrary();

        if (inLibrary)
            floatingActionButton.setImageResource(R.drawable.ic_add_circle_black_24dp);

        if (StaticNovel.novelPage != null)
            setData();
        return view;
    }

    public void setData() {
        title.setText(StaticNovel.novelPage.title);
        author.setText(Arrays.toString(StaticNovel.novelPage.authors));
        description.setText(StaticNovel.novelPage.description);
        NovelFragmentChapters.novelChapters =StaticNovel. novelPage.novelChapters;
        Picasso.get()
                .load(StaticNovel.novelPage.imageURL)
                .into(imageView);
        floatingActionButton.show();
        formatterName.setText(formatter.getName());
    }

    static class addToLibrary implements FloatingActionButton.OnClickListener {
        final NovelFragmentMain novelFragmentMain;

        addToLibrary(NovelFragmentMain novelFragmentMain) {
            this.novelFragmentMain = novelFragmentMain;
        }

        @Override
        public void onClick(View v) {
            if (!novelFragmentMain.inLibrary) {

                JSONObject savedData = new JSONObject();
                try {
                    JSONArray chapters = new JSONArray();
                    for (NovelChapter novelChapter : NovelFragmentChapters.novelChapters) {
                        JSONObject chapter = new JSONObject();
                        {
                            chapter.put("link", novelChapter.link);
                            chapter.put("chapterNum", novelChapter.chapterNum);
                        }
                        chapters.put(chapter);
                    }
                    savedData.put("chapters", chapters);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (Database.addToLibrary(novelFragmentMain.formatter.getID(), StaticNovel.novelPage, novelFragmentMain.url, savedData)) {
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
