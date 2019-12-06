package com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader;


public class ChapterReaderAdapter extends FragmentPagerAdapter {
    private final ChapterReader chapterReader;

    public ChapterReaderAdapter(@NonNull FragmentManager fm, int behavior, ChapterReader chapterReader) {
        super(fm, behavior);
        this.chapterReader = chapterReader;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        chapterReader.currentView = chapterReader.chapters.get(position);
        return chapterReader.currentView;
    }

    @Override
    public int getCount() {
        return chapterReader.chapters.size();
    }
}
