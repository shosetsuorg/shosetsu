package com.github.doomsdayrs.apps.shosetsu.ui.reader.readers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.doomsdayrs.apps.shosetsu.R;

import org.jetbrains.annotations.NotNull;

public class TextViewReader extends Reader {
    private TextView textView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chapter_reader_text_view, container);
        textView = view.findViewById(R.id.textview);
        return view;
    }

    @Override
    public void setText(@NotNull String text) {
        textView.setText(text);
    }
}
