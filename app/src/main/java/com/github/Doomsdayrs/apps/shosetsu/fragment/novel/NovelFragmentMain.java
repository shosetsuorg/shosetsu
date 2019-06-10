package com.github.Doomsdayrs.apps.shosetsu.fragment.novel;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    static NovelPage novelPage;
    static Formatter formatter;
    static String URL;
    boolean incrementChapters;
    NovelFragmentChapters novelFragmentChapters;

    ImageView imageView;
    TextView title;
    TextView author;
    TextView description;

    public NovelFragmentMain() {
        setHasOptionsMenu(true);
    }

    public void setFormatter(Formatter formatter) {
        NovelFragmentMain.formatter = formatter;
        incrementChapters = formatter.isIncrementingChapterList();
    }

    public void setURL(String URL) {
        NovelFragmentMain.URL = URL;
    }

    public void setNovelFragmentChapters(NovelFragmentChapters novelFragmentChapters) {
        this.novelFragmentChapters = novelFragmentChapters;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreate", "NovelFragmentMain");
        View view = inflater.inflate(R.layout.fragment_novel_main, container, false);
        System.out.println("Loading view...");
        imageView = view.findViewById(R.id.fragment_novel_image);
        title = view.findViewById(R.id.fragment_novel_title);
        author = view.findViewById(R.id.fragment_novel_author);
        description = view.findViewById(R.id.fragment_novel_description);
        System.out.println("Completed.");
        try {
            Log.d("Novel info load", "Loading");
            String u = new fillData().execute(this).get(40, TimeUnit.SECONDS);
            Picasso.get()
                    .load("http://novelfull.com" + u)
                    .into(imageView);
            Log.d("Novel info load", "Loading complete");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    static class fillData extends AsyncTask<NovelFragmentMain, Void, String> {
        @Override
        protected String doInBackground(NovelFragmentMain... novelFragmentMains) {
            if (novelPage == null) return null;
            novelFragmentMains[0].title.setText(novelPage.title);
            novelFragmentMains[0].author.setText(Arrays.toString(novelPage.authors));
            novelFragmentMains[0].description.setText(novelPage.description);
            novelFragmentMains[0].novelFragmentChapters.novelChapters = novelPage.novelChapters;
            return novelPage.imageURL;
        }
    }
}
