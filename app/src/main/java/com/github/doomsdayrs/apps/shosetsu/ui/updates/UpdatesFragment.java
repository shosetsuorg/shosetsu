package com.github.doomsdayrs.apps.shosetsu.ui.updates;
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
 * shosetsu
 * 15 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters.UpdatesPager;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;
import com.google.android.material.tabs.TabLayout;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseUpdates.getStartingDay;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseUpdates.getTotalDays;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseUpdates.trimDate;

public class UpdatesFragment extends Fragment {


    private TabLayout tabLayout;
    private ViewPager viewPager;

    public UpdatesFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.updater_now:
                Toast.makeText(getContext(), "In the future this will start a checking of each novel in this library", Toast.LENGTH_SHORT).show();
                return true;
        }

        return false;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_updater, menu);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Statics.mainActionBar.setTitle("Library");
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tabLayout);
        setViewPager();
        return view;
    }

    private void setViewPager() {
        ArrayList<UpdateFragment> updatesFragments = new ArrayList<>();
        int days = getTotalDays();

        long startTime = getStartingDay();
        for (int x = 0; x < days; x++) {
            UpdateFragment updateFragment = new UpdateFragment();
            updateFragment.setDate(startTime);
            startTime += 86400000;
            updatesFragments.add(updateFragment);
        }
        // TODAY
        UpdateFragment updateFragment = new UpdateFragment();
        updateFragment.setDate(trimDate(new DateTime(System.currentTimeMillis())).getMillis());
        updatesFragments.add(updateFragment);

        Collections.reverse(updatesFragments);

        UpdatesPager pagerAdapter = new UpdatesPager(getChildFragmentManager(), updatesFragments);
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
