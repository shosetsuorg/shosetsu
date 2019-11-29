package com.github.doomsdayrs.apps.shosetsu.ui.reader.readers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.listeners.NovelFragmentChapterViewHideBar;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;

import org.jetbrains.annotations.NotNull;

public class TextViewReader extends Reader {
    private TextView textView;

    public TextViewReader(ChapterReader chapterReader) {
        super(chapterReader);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chapter_reader_text_view, container, false);
        textView = view.findViewById(R.id.textview);
        textView.setOnClickListener(new NovelFragmentChapterViewHideBar(chapterReader.toolbar));
        textView.setBackgroundColor(Settings.ReaderTextBackgroundColor);
        textView.setTextColor(Settings.ReaderTextColor);
        textView.setTextSize(Settings.ReaderTextSize);
        return view;
    }

    @Override
    public void setText(@NotNull String text) {
        textView.setText(text);
    }
}
