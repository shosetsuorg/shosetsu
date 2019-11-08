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

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters.NovelPagerAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.async.NovelLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentInfo;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.isOnline;

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelFragment extends Fragment {


    public int novelID;
    public String novelURL;
    public NovelPage novelPage;
    public Formatter formatter;
    public Status status = Status.UNREAD;

    public List<NovelChapter> novelChapters = new ArrayList<>();


    /**
     * @param chapterURL Current chapter URL
     * @return chapter after the input, returns the current chapter if no more
     */
    public static NovelChapter getNextChapter(@NotNull String chapterURL, String[] novelChapters) {
        if (novelChapters != null && novelChapters.length != 0)
            for (int x = 0; x < novelChapters.length; x++) {
                if (novelChapters[x].equalsIgnoreCase(chapterURL)) {
                    if (NovelFragmentChapters.reversed) {
                        if (x - 1 != -1)
                            return Database.DatabaseChapter.getChapter(Database.DatabaseIdentification.getChapterIDFromChapterURL(novelChapters[x - 1]));
                        else
                            return Database.DatabaseChapter.getChapter(Database.DatabaseIdentification.getChapterIDFromChapterURL(novelChapters[x]));
                    } else {
                        if (x + 1 != novelChapters.length)
                            return Database.DatabaseChapter.getChapter(Database.DatabaseIdentification.getChapterIDFromChapterURL(novelChapters[x + 1]));
                        else
                            return Database.DatabaseChapter.getChapter(Database.DatabaseIdentification.getChapterIDFromChapterURL(novelChapters[x]));
                    }
                }
            }
        return null;
    }

    /**
     * @return position of last read chapter, reads array from reverse. If -1 then the array is null, if -2 the array is empty, else if not found plausible chapter returns the first.
     */
    public int lastRead() {
        if (novelChapters != null) {
            if (novelChapters.size() != 0) {
                if (!NovelFragmentChapters.reversed) {
                    for (int x = novelChapters.size() - 1; x >= 0; x--) {
                        Status status = Database.DatabaseChapter.getStatus(Database.DatabaseIdentification.getChapterIDFromChapterURL(novelChapters.get(x).link));
                        switch (status) {
                            default:
                                break;
                            case READ:
                                return x + 1;
                            case READING:
                                return x;
                        }
                    }
                } else {
                    for (int x = 0; x < novelChapters.size(); x++) {
                        Status status = Database.DatabaseChapter.getStatus(Database.DatabaseIdentification.getChapterIDFromChapterURL(novelChapters.get(x).link));
                        switch (status) {
                            default:
                                break;
                            case READ:
                                return x - 1;
                            case READING:
                                return x;
                        }
                    }
                }
                return 0;
            } else return -2;
        } else return -1;
    }


    public NovelFragmentInfo novelFragmentInfo;
    public NovelFragmentChapters novelFragmentChapters;
    public ProgressBar progressBar;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public ConstraintLayout errorView;
    public TextView errorMessage;
    public Button errorButton;


    public NovelFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("novelID", novelID);
        outState.putString("novelURL", novelURL);
        outState.putInt("formatter", formatter.getID());
        outState.putInt("status", status.getA());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreateView", "NovelFragment");
        View view = inflater.inflate(R.layout.fragment_novel, container, false);
        // Attach UI to program
        {
            progressBar = view.findViewById(R.id.fragment_novel_progress);
            viewPager = view.findViewById(R.id.fragment_novel_viewpager);
            tabLayout = view.findViewById(R.id.fragment_novel_tabLayout);
            errorView = view.findViewById(R.id.network_error);
            errorMessage = view.findViewById(R.id.error_message);
            errorButton = view.findViewById(R.id.error_button);
        }

        // Create sub-fragments
        {
            novelFragmentInfo = new NovelFragmentInfo();
            novelFragmentInfo.setNovelFragment(this);
            novelFragmentChapters = new NovelFragmentChapters();
            novelFragmentChapters.setNovelFragment(this);
        }
        //TODO FINISH TRACKING
        //boolean track = SettingsController.isTrackingEnabled();

        if (savedInstanceState == null) {
            if (isOnline() && !Database.DatabaseNovels.inDatabase(novelID)) {
                setViewPager();
                new NovelLoader(this, false).execute(getActivity());
            } else {
                novelPage = Database.DatabaseNovels.getNovelPage(novelID);
                status = Database.DatabaseNovels.getStatus(novelID);
                if (novelPage != null)
                    Statics.mainActionBar.setTitle(novelPage.title);
                setViewPager();
            }
        } else {
            novelID = savedInstanceState.getInt("novelID");
            novelURL = savedInstanceState.getString("novelURL");
            formatter = DefaultScrapers.getByID(savedInstanceState.getInt("formatter"));
            status = Status.getStatus(savedInstanceState.getInt("status"));
            setViewPager();
        }
        return view;
    }


    private void setViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        {
            Log.d("FragmentLoading", "Main");
            fragments.add(novelFragmentInfo);
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
