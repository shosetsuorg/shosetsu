package com.github.doomsdayrs.apps.shosetsu.backend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.Stati;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader;
import com.github.doomsdayrs.apps.shosetsu.ui.webView.Actions;
import com.github.doomsdayrs.apps.shosetsu.ui.webView.WebViewApp;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getChapterIDFromChapterURL;


/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 * shosetsu
 * 26 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class Utilities {

    /**
     * Demarks a list of items, setting only one to be checked.
     *
     * @param menuItems      Items to sort through
     * @param positionSpared Item to set checked
     * @param demarkAction   Any action to proceed with
     */
    public static void demarkMenuItems(@NotNull MenuItem[] menuItems, int positionSpared, @Nullable DemarkAction demarkAction) {
        for (int x = 0; x < menuItems.length; x++)
            if (x != positionSpared)
                menuItems[x].setChecked(false);
            else menuItems[x].setChecked(true);

        if (demarkAction != null)
            demarkAction.action(positionSpared);
    }

    /**
     * Abstraction for Actions to take after demarking items. To simplify bulky code
     */
    public interface DemarkAction {
        void action(int spared);
    }

    /**
     * Cleans a string
     *
     * @param input String to clean
     * @return string without specials
     */
    @NonNull
    public static String cleanString(@NonNull String input) {
        return input.replaceAll("[^A-Za-z0-9]", "_");
    }

    public static final int SELECTED_STROKE_WIDTH = 8;
    @Nullable
    public static String shoDir = "/Shosetsu/";

    public static int calculateNoOfColumns(@NonNull Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidthDp / columnWidthDp + 0.5);
    }

    /**
     * Serialize object to string
     *
     * @param object object serialize
     * @return Serialised string
     * @throws IOException exception
     */
    @NonNull
    public static String serializeToString(@NotNull Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return "serial-" + Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    /**
     * Deserialize a string to the object
     *
     * @param string serialized string
     * @return Object from string
     * @throws IOException            exception
     * @throws ClassNotFoundException exception
     */
    @Nullable
    public static Object deserializeString(@NotNull String string) throws IOException, ClassNotFoundException {
        if (!string.equals("null")) {
            string = string.substring(7);
            //Log.d("Deserialize", string);
            byte[] bytes = Base64.decode(string, Base64.NO_WRAP);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return objectInputStream.readObject();
        }
        return null;
    }

    /**
     * Checks string before deserialization
     * If null or empty, returns "". Else deserializes the string and returns
     *
     * @param string String to be checked
     * @return Completed String
     */
    @Nullable
    public static String checkStringDeserialize(@Nullable String string) {
        if (string == null || string.isEmpty()) {
            return "";
        } else {
            try {
                return (String) deserializeString(string);
            } catch (@NonNull IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * Checks string before serialization
     * If null or empty, returns "". Else serializes the string and returns
     *
     * @param string String to be checked
     * @return Completed String
     */
    @NonNull
    public static String checkStringSerialize(@Nullable String string) {
        if (string == null || string.isEmpty()) {
            return "";
        } else {
            try {
                return serializeToString(string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    /**
     * Converts String Stati back into Stati
     *
     * @param s String title
     * @return Stati
     */
    @NonNull
    public static Stati convertStringToStati(@NonNull String s) {
        switch (s) {
            case "Publishing":
                return Stati.PUBLISHING;
            case "Completed":
                return Stati.COMPLETED;
            case "Paused":
                return Stati.PAUSED;
            default:
            case "Unknown":
                return Stati.UNKNOWN;
        }
    }


    /**
     * Converts Array of Strings into a String
     *
     * @param a array of strings
     * @return String Array
     */
    @NonNull
    public static String convertArrayToString(@NotNull String[] a) {
        if (a != null && a.length != 0) {
            for (int x = 0; x < a.length; x++) {
                a[x] = a[x].replace(",", ">,<");
            }
            return Arrays.toString(a);
        }
        return "[]";
    }


    /**
     * Converts a String Array back into an Array of Strings
     *
     * @param s String array
     * @return Array of Strings
     */
    @NonNull
    public static String[] convertStringToArray(@NotNull String s) {
        String[] a = s.substring(1, s.length() - 1).split(", ");

        for (int x = 0; x < a.length; x++) {
            a[x] = a[x].replace(">,<", ",");
        }

        return a;
    }


    // Preference objects
    public static SharedPreferences download;
    public static SharedPreferences view;
    public static SharedPreferences advanced;
    public static SharedPreferences tracking;
    public static SharedPreferences backup;

    /**
     * Initializes the settings
     *
     * @param
     */
    public static void initPreferences(@NonNull AppCompatActivity mainActivity) {
        Settings.ReaderTextColor = view.getInt("ReaderTextColor", Color.BLACK);
        Settings.ReaderTextBackgroundColor = view.getInt("ReaderBackgroundColor", Color.WHITE);
        String dir = mainActivity.getExternalFilesDir(null).getAbsolutePath();
        dir = dir.substring(0, dir.indexOf("/Android"));
        shoDir = download.getString("dir", dir + "/Shosetsu/");
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


    public static void changeMode(@NonNull Activity activity, int newMode) {
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
     * @param chapterID chapter id
     * @return y position
     */
    public static int getYBookmark(int chapterID) {
        return Database.DatabaseChapter.getY(chapterID);
    }

    /**
     * Toggles bookmark
     *
     * @param chapterID id
     * @return true means added, false means removed
     */
    public static boolean toggleBookmarkChapter(int chapterID) {
        //TODO Simplify
        if (Database.DatabaseChapter.isBookMarked(chapterID)) {
            Database.DatabaseChapter.setBookMark(chapterID, 0);
            return false;
        } else {
            Database.DatabaseChapter.setBookMark(chapterID, 1);
            return true;
        }
    }


    public static void setTextSize(int size) {
        Settings.ReaderTextSize = size;
        view.edit()
                .putInt("ReaderTextSize", size)
                .apply();
    }

    /**
     * Pre resquite requires chapter to already have been added to library
     *
     * @param activity
     * @param novelChapter
     * @param novelID
     * @param formatterID
     */
    public static void openChapter(@NonNull Activity activity, @NonNull NovelChapter novelChapter, int novelID, int formatterID) {
        openChapter(activity, novelChapter, novelID, formatterID, null);
    }

    private static void openChapter(@NonNull Activity activity, @NonNull NovelChapter novelChapter, int novelID, int formatterID, String[] chapters) {
        int chapterID = getChapterIDFromChapterURL(novelChapter.link);
        Database.DatabaseChapter.setChapterStatus(chapterID, Status.READING);
        Intent intent = new Intent(activity, ChapterReader.class);
        intent.putExtra("title", novelChapter.title);
        intent.putExtra("chapterID", chapterID);
        intent.putExtra("chapterURL", novelChapter.link);
        intent.putExtra("novelID", novelID);
        intent.putExtra("formatter", formatterID);
        intent.putExtra("chapters", chapters);
        activity.startActivity(intent);

    }

    public static void openInWebview(@NotNull Activity activity, @NotNull String url) {
        Intent intent = new Intent(activity, WebViewApp.class);
        intent.putExtra("url", url);
        intent.putExtra("action", Actions.VIEW.getAction());
        activity.startActivity(intent);
    }

    public static void openInBrowser(@NotNull Activity activity, @NotNull String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
    }


    /**
     * Freezes the thread for x time
     *
     * @param time time in MS
     */
    public static void wait(int time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            if (e.getMessage() != null)
                Log.e("Error", e.getMessage());
        }
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

    private static boolean debug = false;

    public static void toggleDebug() {
        debug = !debug;
    }

}
