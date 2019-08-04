package com.github.doomsdayrs.apps.shosetsu.backend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.ChapterReader;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 * shosetsu
 * 26 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */public class Utilities {

    public static final int SELECTED_STROKE_WIDTH = 8;
    public static String shoDir = "/Shosetsu/";

    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidthDp / columnWidthDp + 0.5);
    }

    // Preference objects
    public static SharedPreferences download;
    public static SharedPreferences view;
    public static SharedPreferences advanced;
    public static SharedPreferences tracking;
    public static SharedPreferences backup;

    /**
     * Initializes the settings
     */
    public static void initPreferences() {
        Settings.ReaderTextColor = view.getInt("ReaderTextColor", Color.BLACK);
        Settings.ReaderTextBackgroundColor = view.getInt("ReaderBackgroundColor", Color.WHITE);
        shoDir = download.getString("dir", "/storage/emulated/0/Shosetsu/");
        Settings.downloadPaused = download.getBoolean("paused", false);
        Settings.ReaderTextSize = view.getInt("ReaderTextSize", 14);
        Settings.themeMode = advanced.getInt("themeMode", 0);
        Settings.paragraphSpacing = view.getInt("paragraphSpacing", 1);
        Settings.indentSize = view.getInt("indentSize", 1);
    }

    public static boolean toggleTapToScroll() {
        if (isTapToScroll())
            view.edit().putBoolean("tapToScroll", false).apply();
        else view.edit().putBoolean("tapToScroll", true).apply();
        return isTapToScroll();
    }

    public static boolean isTapToScroll() {
        return view.getBoolean("tapToScroll", false);
    }

    public static boolean intToBoolean(int a) {
        return a == 1;
    }

    public static int booleanToInt(boolean a) {
        if (a)
            return 1;
        else return 0;
    }

    public static void changeIndentSize(int newIndent) {
        Settings.indentSize = newIndent;
        view.edit().putInt("indentSize", newIndent).apply();
    }

    public static void changeParagraphSpacing(int newSpacing) {
        Settings.paragraphSpacing = newSpacing;
        view.edit().putInt("paragraphSpacing", newSpacing).apply();
    }


    public static void changeMode(Activity activity, int newMode) {
        if (!(newMode >= 0 && newMode <= 2))
            throw new IndexOutOfBoundsException("Non valid int passed");
        Settings.themeMode = newMode;
        advanced.edit()
                .putInt("themeMode", newMode)
                .apply();

        switch (Settings.themeMode) {
            case 0:
                activity.setTheme(R.style.Theme_MaterialComponents_Light_NoActionBar);
                break;
            case 1:
                activity.setTheme(R.style.Theme_MaterialComponents_NoActionBar);
                break;
            case 2:
                activity.setTheme(R.style.ThemeOverlay_MaterialComponents_Dark);
        }
    }


    /**
     * Toggles paused downloads
     *
     * @return if paused or not
     */
    public static boolean togglePause() {
        Settings.downloadPaused = !Settings.downloadPaused;
        download.edit()
                .putBoolean("paused", Settings.downloadPaused)
                .apply();
        return Settings.downloadPaused;
    }

    /**
     * Checks if online
     *
     * @return true if so, otherwise false
     */
    public static boolean isOnline() {
        NetworkInfo activeNetwork = Settings.connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null)
            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        return false;
    }

    /**
     * Is reader in night mode
     *
     * @return true if so, otherwise false
     */
    public static boolean isReaderNightMode() {
        //TODO: Check this also, this doesn't seem to be a nice way to do things.
        return Settings.ReaderTextColor == Color.WHITE;
    }

    public static void setNightNode() {
        setReaderColor(Color.WHITE, Color.BLACK);
    }

    public static void unsetNightMode() {
        setReaderColor(Color.BLACK, Color.WHITE);
    }

    /**
     * Sets the reader color
     *
     * @param text       Color of text
     * @param background Color of background
     */
    private static void setReaderColor(int text, int background) {
        Settings.ReaderTextColor = text;
        Settings.ReaderTextBackgroundColor = background;
        view.edit()
                .putInt("ReaderTextColor", text)
                .putInt("ReaderBackgroundColor", background)
                .apply();
    }

    /**
     * Swaps the reader colors
     */
    public static void swapReaderColor() {
        if (isReaderNightMode()) {
            setReaderColor(Color.BLACK, Color.WHITE);
        } else {
            setReaderColor(Color.WHITE, Color.BLACK);
        }
    }


    /**
     * Gets y position of a bookmark
     *
     * @param chapterURL chapter chapterURL
     * @return y position
     */
    public static int getYBookmark(String chapterURL) {
        return Database.DatabaseChapter.getY(chapterURL);
    }

    /**
     * Toggles bookmark
     *
     * @param chapterURL imageURL of chapter
     * @return true means added, false means removed
     */
    public static boolean toggleBookmarkChapter(String chapterURL) {
        if (Database.DatabaseChapter.isBookMarked(chapterURL)) {
            Database.DatabaseChapter.setBookMark(chapterURL, 0);
            return false;
        } else {
            Database.DatabaseChapter.setBookMark(chapterURL, 1);
            return true;
        }
    }


    public static void setTextSize(int size) {
        Settings.ReaderTextSize = size;
        view.edit()
                .putInt("ReaderTextSize", size)
                .apply();
    }

    public static void openChapter(Activity activity, NovelChapter novelChapter) {
        Database.DatabaseChapter.setChapterStatus(novelChapter.link, Status.READING);
        Intent intent = new Intent(activity, ChapterReader.class);
        intent.putExtra("title", novelChapter.chapterNum);
        intent.putExtra("chapterURL", novelChapter.link);
        intent.putExtra("novelURL", StaticNovel.novelURL);
        intent.putExtra("formatter", StaticNovel.formatter.getID());
        activity.startActivity(intent);
    }

    //TODO Online Trackers
    //Methods below when tracking system setup

    @SuppressWarnings({"EmptyMethod", "unused"})
    public static boolean isTrackingEnabled() {
        return tracking.getBoolean("enabled", false);
    }

    @SuppressWarnings({"EmptyMethod", "unused"})
    public static void addTracker() {
    }
}
