package com.github.doomsdayrs.apps.shosetsu.ui.reader.readers;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

public class TextViewReader extends Reader {
    TextView textView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setText(@NotNull String text) {
        textView.setText(text);
    }
}
