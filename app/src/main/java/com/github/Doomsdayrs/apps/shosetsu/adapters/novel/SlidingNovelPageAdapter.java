package com.github.Doomsdayrs.apps.shosetsu.adapters.novel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

public class SlidingNovelPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public SlidingNovelPageAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int i) {
        Log.d("SwapScreen", fragments.get(i).toString());
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
