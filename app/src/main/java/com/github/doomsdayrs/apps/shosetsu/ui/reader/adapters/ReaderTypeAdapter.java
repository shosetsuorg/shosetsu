package com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.github.doomsdayrs.apps.shosetsu.ui.reader.readers.Reader;

import java.util.ArrayList;
import java.util.List;

public class ReaderTypeAdapter extends FragmentPagerAdapter {
    private final List<Reader> fragments;

    public ReaderTypeAdapter(@NonNull FragmentManager fm, ArrayList<Reader> fragments) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragments = fragments;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
