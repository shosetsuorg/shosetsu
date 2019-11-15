package com.github.doomsdayrs.apps.shosetsu.ui.reader.readers;

import androidx.fragment.app.Fragment;

import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader;

import org.jetbrains.annotations.NotNull;

public abstract class Reader extends Fragment {
    protected final ChapterReader chapterReader;

    protected Reader(ChapterReader chapterReader) {
        this.chapterReader = chapterReader;
    }

    public abstract void setText(@NotNull String text);
}
