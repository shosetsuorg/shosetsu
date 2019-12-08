package com.github.doomsdayrs.apps.shosetsu.ui.reader.readers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.listeners.NovelFragmentChapterViewHideBar;

import org.jetbrains.annotations.NotNull;

import us.feras.mdv.MarkdownView;

public class MarkdownViewReader extends Reader {
    private MarkdownView markdownView;

    public MarkdownViewReader(ChapterReader chapterReader) {
        super(chapterReader);
    }


    @Nullable
    @Override
    public View getView() {
        return markdownView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chapter_reader_mark_down, container, false);
        markdownView = view.findViewById(R.id.markdown_view);
        markdownView.setOnClickListener(new NovelFragmentChapterViewHideBar(chapterReader.toolbar));
        return view;
    }

    @Override
    public void setText(@NotNull String text) {
        markdownView.post(() -> markdownView.loadMarkdown(text));
    }
}
