package com.github.Doomsdayrs.apps.shosetsu.settings;

import android.graphics.Color;

public class SettingsController {

    public static void swapReaderColor() {
        if (Settings.ReaderTextColor == Color.BLACK) {
            Settings.ReaderTextColor = Color.WHITE;
            Settings.ReaderTextBackgroundColor = Color.BLACK;
        } else {
            Settings.ReaderTextColor = Color.BLACK;
            Settings.ReaderTextBackgroundColor = Color.WHITE;
        }
    }
}
