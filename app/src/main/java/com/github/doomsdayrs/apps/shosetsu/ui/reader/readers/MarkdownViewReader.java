package com.github.doomsdayrs.apps.shosetsu.ui.reader.readers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.doomsdayrs.apps.shosetsu.R;

import org.jetbrains.annotations.NotNull;

import us.feras.mdv.MarkdownView;

public class MarkdownViewReader extends Reader {
    private MarkdownView markdownView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chapter_reader_mark_down, container);
        markdownView = view.findViewById(R.id.markdown_view);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void setText(@NotNull String text) {
        markdownView.loadMarkdown(text);
    }
}
