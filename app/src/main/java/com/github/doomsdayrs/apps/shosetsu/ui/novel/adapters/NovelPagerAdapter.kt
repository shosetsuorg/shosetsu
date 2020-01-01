package com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

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
class NovelPagerAdapter(fm: FragmentManager, behavior: Int, private val fragments: List<Fragment>) : FragmentPagerAdapter(fm, behavior) {
    private val titles = arrayOf("Info", "Chapters")
    //TODO with tracker use this instead the of the above
/*public NovelPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments, boolean ignored) {
        super(fm);
        this.fragments = fragments;
        titles = new String[]{titles[0], titles[1], "Tracker"};
    }*/
    override fun getItem(i: Int): Fragment {
        Log.d("SwapScreen", fragments[i].toString())
        return fragments[i]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

}