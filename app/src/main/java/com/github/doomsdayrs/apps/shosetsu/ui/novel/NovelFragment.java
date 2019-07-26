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
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 * @author github.com/hXtreme
 */
public class NovelFragment extends Fragment {



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


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (StaticNovel.chapterLoader != null)
            StaticNovel.chapterLoader.cancel(true);
        if (StaticNovel.novelLoader != null)
            StaticNovel.novelLoader.cancel(true);

        StaticNovel.chapterLoader = null;
        StaticNovel.novelLoader = null;
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
        novelFragmentMain = new NovelFragmentMain();
        novelFragmentMain.setNovelFragment(this);
        novelFragmentChapters = new NovelFragmentChapters();
        novelFragmentChapters.setNovelFragment(this);
        //TODO FINISH TRACKING
        //boolean track = SettingsController.isTrackingEnabled();

        if (savedInstanceState == null) {
            if (SettingsController.INSTANCE.isOnline() && !Database.DatabaseLibrary.inLibrary(StaticNovel.novelURL)) {
                setViewPager();

                if (StaticNovel.novelLoader != null && StaticNovel.novelLoader.isCancelled())
                    StaticNovel.novelLoader.cancel(true);

                if (StaticNovel.novelLoader == null || StaticNovel.novelLoader.isCancelled())
                    StaticNovel.novelLoader = new NovelLoader(this, true);

                StaticNovel.novelLoader.execute(getActivity());
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
