package com.github.doomsdayrs.apps.shosetsu.ui.reader.readers;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import us.feras.mdv.MarkdownView;

public class MarkdownViewReader extends Reader {
    MarkdownView markdownView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setText(@NotNull String text) {
        markdownView.loadMarkdown(text);
    }
}
