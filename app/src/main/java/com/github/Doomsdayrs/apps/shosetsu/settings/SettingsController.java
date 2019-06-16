package com.github.Doomsdayrs.apps.shosetsu.settings;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.github.Doomsdayrs.apps.shosetsu.database.Database;

import org.json.JSONObject;

/**
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see https://www.gnu.org/licenses/ .
 * ====================================================================
 * Shosetsu
 * 14 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class SettingsController {
    public static SharedPreferences download;
    public static SharedPreferences view;
    public static SharedPreferences advanced;
    public static SharedPreferences tracking;

    public static void init() {
        Settings.ReaderTextColor = view.getInt("ReaderTextColor", Color.BLACK);
        Settings.ReaderTextBackgroundColor = view.getInt("ReaderBackgroundColor", Color.WHITE);
    }

    public static boolean isOnline() {
        NetworkInfo activeNetwork = Settings.connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null)
            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        return false;
    }

    public static boolean isReaderLightMode() {
        return Settings.ReaderTextColor == Color.BLACK;
    }

    private static void setReaderColor(int text, int background) {
        Settings.ReaderTextColor = text;
        Settings.ReaderTextBackgroundColor = background;
        view.edit()
                .putInt("ReaderTextColor", text)
                .putInt("ReaderBackgroundColor", background)
                .apply();
    }

    public static void swapReaderColor() {
        if (isReaderLightMode())
            setReaderColor(Color.WHITE, Color.BLACK);
        else
            setReaderColor(Color.BLACK, Color.WHITE);
    }

    public static boolean isBookMarked(String chapterURL) {
        return Database.isBookMarked(chapterURL);
    }

    public static int getYBookmark(String chapterURL) {
        return Database.getBookmarkObject(chapterURL);
    }

    /**
     * Toggles bookmark
     *
     * @param chapterURL imageURL of chapter
     * @param saveData   JSON object containing scroll position and others
     * @return true means added, false means removed
     */
    public static boolean toggleBookmarkChapter(String chapterURL, JSONObject saveData) {
        if (isBookMarked(chapterURL)) {
            return !Database.removeBookMarked(chapterURL);
        } else {
            return Database.addBookMark(chapterURL, saveData);
        }
    }

    public static boolean isTrackingEnabled() {
        return tracking.getBoolean("enabled", false);
    }

    public static void addTracker() {
    }
}
