package com.github.doomsdayrs.apps.shosetsu.ui.novel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.Doomsdayrs.api.novelreader_core.main.DefaultScrapers;
import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.async.NovelLoader;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.backend.settings.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.novel.SlidingNovelPageAdapter;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;

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
    private View view;
    public FragmentManager fragmentManager = null;
    public Formatter formatter;
    public String novelURL;
    public NovelFragmentMain novelFragmentMain;
    public NovelFragmentChapters novelFragmentChapters;
    public ProgressBar progressBar;


    public NovelFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("Saving Instance State", "NovelFragment");
        outState.putString("novelURL", novelURL);
        outState.putInt("formatter", formatter.getID());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreateView", "NovelFragment");
        view = inflater.inflate(R.layout.fragment_novel, container, false);
        progressBar = view.findViewById(R.id.fragment_novel_progress);
        novelFragmentMain = new NovelFragmentMain();
        novelFragmentChapters = new NovelFragmentChapters();
        //TODO FINISH TRACKING
        //boolean track = SettingsController.isTrackingEnabled();

        if (savedInstanceState == null) {
            if (SettingsController.isOnline() && !Database.DatabaseLibrary.inLibrary(novelURL)) {
                setViewPager();
                new NovelLoader(this).execute(getActivity());
            } else {
                StaticNovel.novelPage = Database.DatabaseLibrary.getNovelPage(novelURL);
                Statics.mainActionBar.setTitle(StaticNovel.novelPage.title);
                setViewPager();
            }
        } else {
            novelURL = savedInstanceState.getString("novelURL");
            formatter = DefaultScrapers.formatters.get(savedInstanceState.getInt("formatter") - 1);
            setViewPager();
        }
        return view;
    }


    private void setViewPager() {
        Log.d("ViewPager", "Loading");
        ViewPager viewPager = view.findViewById(R.id.fragment_novel_viewpager);
        Log.d("ViewPager", "Loaded:" + viewPager);

        List<Fragment> fragments = new ArrayList<>();
        // Sets the data
        {
            novelFragmentChapters.formatter = formatter;
            novelFragmentChapters.novelURL = novelURL;
            novelFragmentChapters.setFragmentManager(fragmentManager);

            novelFragmentMain.url = novelURL;
            novelFragmentMain.formatter = formatter;

        }
        // Add the fragments
        {
            Log.d("FragmentLoading", "Main");
            fragments.add(novelFragmentMain);
            Log.d("FragmentLoading", "Chapters");
            fragments.add(novelFragmentChapters);
        }

        SlidingNovelPageAdapter pagerAdapter = new SlidingNovelPageAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);

    }

}
