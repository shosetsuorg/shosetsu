package com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterView;

import java.util.ArrayList;


public class ChapterReaderAdapter extends FragmentPagerAdapter {
    private final ChapterReader chapterReader;

    public ChapterReaderAdapter(@NonNull FragmentManager fm, int behavior, ChapterReader chapterReader) {
        super(fm, behavior);
        this.chapterReader = chapterReader;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (!chapterReader.first) {
            chapterReader.currentView = chapterReader.cachedChapter(chapterReader.chapterIDs[position]);
            if (chapterReader.currentView == null) {
                chapterReader.currentView = new ChapterView(chapterReader, chapterReader.chapterIDs[position]);
                chapterReader.chapters.add(chapterReader.currentView);
            }
            chapterReader.currentChapterID = chapterReader.currentView.chapterID;
        }
        return chapterReader.currentView;
    }

    @Override
    public int getCount() {
        return chapterReader.chapterIDs.length;
    }
}
