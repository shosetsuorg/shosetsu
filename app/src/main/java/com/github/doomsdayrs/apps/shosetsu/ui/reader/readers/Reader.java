package com.github.doomsdayrs.apps.shosetsu.ui.reader.readers;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

public abstract class Reader extends Fragment {
    public abstract void setText(@NotNull String text);
}
