package com.github.Doomsdayrs.apps.shosetsu.fragment.novel;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.adapters.novel.SlidingNovelPageAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class NovelFragment extends Fragment {
    static View view;
    static FragmentManager fragmentManager = null;
    static Formatter formatter;
    static String URL;
    static NovelPage novelPage;
    static SlidingNovelPageAdapter pagerAdapter;
    boolean incrementChapters;
    NovelFragmentMain novelFragmentMain;
    NovelFragmentChapters novelFragmentChapters;
    ViewPager viewPager;


    public NovelFragment() {
        setHasOptionsMenu(true);
    }

    public void setFormatter(Formatter formatter) {
        NovelFragment.formatter = formatter;
        incrementChapters = formatter.isIncrementingChapterList();
    }

    public void setURL(String URL) {
        NovelFragment.URL = URL;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        NovelFragment.fragmentManager = fragmentManager;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreate", "NovelFragment");
        view = inflater.inflate(R.layout.fragment_novel, container, false);
        if (savedInstanceState == null) {
            new fillData().execute();
            setViewPager();
        } else setViewPager();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    void setViewPager() {
        viewPager = view.findViewById(R.id.fragment_novel_viewpager);

        List<Fragment> fragments = new ArrayList<>();

        novelFragmentChapters = new NovelFragmentChapters();
        novelFragmentChapters.setFormatter(formatter);
        novelFragmentChapters.setNovelURL(URL);
        novelFragmentChapters.setFragmentManager(fragmentManager);

        novelFragmentMain = new NovelFragmentMain();
        novelFragmentMain.setURL(URL);
        novelFragmentMain.setFormatter(formatter);
        novelFragmentMain.setNovelFragmentChapters(novelFragmentChapters);


        fragments.add(novelFragmentMain);
        fragments.add(novelFragmentChapters);

        Log.d("Fragments", fragments.toString());

        pagerAdapter = new SlidingNovelPageAdapter(fragmentManager, fragments);
        viewPager.setAdapter(pagerAdapter);
    }

    static class fillData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                novelPage = formatter.parseNovel("http://novelfull.com" + URL);
                NovelFragmentMain.novelPage = novelPage;
                Log.d("Loaded Novel:", novelPage.title);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
