package com.github.doomsdayrs.apps.shosetsu.ui.reader.readers;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader;

import org.jetbrains.annotations.NotNull;

public abstract class Reader extends Fragment {
    final ChapterReader chapterReader;

    Reader(ChapterReader chapterReader) {
        this.chapterReader = chapterReader;
    }

    public void setText(@NotNull String text) {
        String t = text;
        if (t.length() > 100)
            t = t.substring(0, 100);
        else if (t.length() > 10)
            t = t.substring(0, 10);
        Log.d("SetText", t);
    }

}
