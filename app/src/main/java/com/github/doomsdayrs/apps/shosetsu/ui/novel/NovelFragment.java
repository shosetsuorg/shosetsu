package com.github.doomsdayrs.apps.shosetsu.ui.novel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.backend.async.NovelLoader;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.novel.NovelPagerAdapter;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

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
public class NovelFragment extends Fragment {

    public FragmentManager fragmentManager = null;

    public NovelFragmentMain novelFragmentMain;
    public NovelFragmentChapters novelFragmentChapters;
    public ProgressBar progressBar;

    private TabLayout tabLayout;
    public ViewPager viewPager;

    public ConstraintLayout errorView;
    public TextView errorMessage;
    public Button errorButton;


    public NovelFragment() {
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreateView", "NovelFragment");
        View view = inflater.inflate(R.layout.fragment_novel, container, false);
        {
            progressBar = view.findViewById(R.id.fragment_novel_progress);
            viewPager = view.findViewById(R.id.fragment_novel_viewpager);
            tabLayout = view.findViewById(R.id.fragment_novel_tabLayout);
            errorView = view.findViewById(R.id.network_error);
            errorMessage = view.findViewById(R.id.error_message);
            errorButton = view.findViewById(R.id.error_button);
        }
        novelFragmentMain = new NovelFragmentMain(this);
        novelFragmentChapters = new NovelFragmentChapters(this);
        //TODO FINISH TRACKING
        //boolean track = SettingsController.isTrackingEnabled();

        if (savedInstanceState == null) {
            if (SettingsController.isOnline() && !Database.DatabaseLibrary.inLibrary(StaticNovel.novelURL)) {
                setViewPager();
                new NovelLoader(this, true).execute(getActivity());
            } else {
                StaticNovel.novelPage = Database.DatabaseLibrary.getNovelPage(StaticNovel.novelURL);
                StaticNovel.status = Database.DatabaseLibrary.getStatus(StaticNovel.novelURL);
                if (StaticNovel.novelPage != null)
                    Statics.mainActionBar.setTitle(StaticNovel.novelPage.title);
                setViewPager();
            }
        } else {
            setViewPager();
        }
        return view;
    }


    private void setViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        {
            Log.d("FragmentLoading", "Main");
            fragments.add(novelFragmentMain);
            Log.d("FragmentLoading", "Chapters");
            fragments.add(novelFragmentChapters);
        }

        NovelPagerAdapter pagerAdapter = new NovelPagerAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.post(() -> tabLayout.setupWithViewPager(viewPager));
    }

}
