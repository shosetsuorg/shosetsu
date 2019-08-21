package com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters;
/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.github.doomsdayrs.apps.shosetsu.ui.updates.UpdateFragment;

import org.joda.time.DateTime;

import java.util.ArrayList;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseUpdates.trimDate;

/**
 * shosetsu
 * 20 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class UpdatesPager extends FragmentPagerAdapter {

    private final ArrayList<UpdateFragment> fragments;

    public UpdatesPager(@NonNull FragmentManager fm, ArrayList<UpdateFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        DateTime dateTime = new DateTime(fragments.get(position).date);
        if (dateTime.equals(trimDate(new DateTime(System.currentTimeMillis())))) {
            return "Today";
        } else if (dateTime.equals(trimDate(new DateTime(System.currentTimeMillis())).minusDays(1))) {
            return "Yesterday";
        }
        return dateTime.getDayOfMonth() + "/" + dateTime.getMonthOfYear() + "/" + dateTime.getYear();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addToFragments(UpdateFragment f) {
        fragments.add(0, f);
    }
}
