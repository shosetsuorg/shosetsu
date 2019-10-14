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

import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.Stati;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader;
import com.github.doomsdayrs.apps.shosetsu.ui.webView.Actions;
import com.github.doomsdayrs.apps.shosetsu.ui.webView.WebViewApp;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


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

    public static final int SELECTED_STROKE_WIDTH = 8;
    public static String shoDir = "/Shosetsu/";

    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
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
    public static Object deserializeString(@NotNull String string) throws IOException, ClassNotFoundException {
        if (!string.equals("null")) {
            string = string.substring(7);
            //Log.d("Deserialize", string);
            byte[] bytes = Base64.decode(string, Base64.NO_WRAP);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            return object;
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
    public static String checkStringDeserialize(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        } else {
            try {
                return (String) deserializeString(string);
            } catch (IOException | ClassNotFoundException e) {
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
    public static String checkStringSerialize(String string) {
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
    public static Stati convertStringToStati(String s) {
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

    public static void openChapter(Activity activity, NovelChapter novelChapter, String nurl, int formatterID) {
        Database.DatabaseChapter.setChapterStatus(novelChapter.link, Status.READING);
        Intent intent = new Intent(activity, ChapterReader.class);
        intent.putExtra("title", novelChapter.chapterNum);
        intent.putExtra("chapterURL", novelChapter.link);
        intent.putExtra("novelURL", nurl);
        intent.putExtra("formatter", formatterID);
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


    private static final String[] NOVELPAGEKEYS = {"title", "imageURL", "description", "genres", "authors", "status", "tags", "artists", "language", "maxChapterPage", "novelChapters"};
    private static final String[] NOVELCHAPTERKEYS = {"release", "chapterNum", "link"};
    private static boolean debug = false;

    public static void toggleDebug() {
        debug = !debug;
    }

    /**
     * Turns an object into a JSON counterpart, then serializes it along with data in it
     *
     * @param object NovelPage or NovelChapter
     * @return Serialized JSON
     */
    public static String serializeOBJECT(Object object) throws Exception {
        if (object.getClass().equals(NovelChapter.class)) {
            NovelChapter novelChapter = (NovelChapter) object;
            return serializeToString(novelChapterToJSON(novelChapter).toString());
        } else if (object.getClass().equals(NovelPage.class)) {
            NovelPage novelPage = (NovelPage) object;
            JSONObject jsonObject = new JSONObject();

            if (novelPage.title != null)
                jsonObject.put("title", serializeToString(novelPage.title));
            else jsonObject.put("title", "null");

            if (novelPage.imageURL != null)
                jsonObject.put("imageURL", serializeToString(novelPage.imageURL));
            else jsonObject.put("imageURL", "null");

            if (novelPage.description != null)
                jsonObject.put("description", serializeToString(novelPage.description));
            else jsonObject.put("description", "null");

            if (novelPage.genres != null) {
                JSONArray jsonArray = new JSONArray();
                for (String genre : novelPage.genres)
                    jsonArray.put(serializeToString(genre));
                jsonObject.put("genres", jsonArray);
            } else jsonObject.put("genres", new JSONArray());

            if (novelPage.authors != null) {
                JSONArray jsonArray = new JSONArray();
                for (String author : novelPage.authors)
                    jsonArray.put(serializeToString(author));
                jsonObject.put("authors", jsonArray);
            } else jsonObject.put("authors", new JSONArray());

            if (novelPage.status != null) {
                jsonObject.put("status", novelPage.status.toString());
            } else jsonObject.put("status", "Unknown");

            if (novelPage.tags != null) {
                JSONArray jsonArray = new JSONArray();
                for (String tag : novelPage.tags)
                    jsonArray.put(serializeToString(tag));
                jsonObject.put("tags", jsonArray);
            } else jsonObject.put("tags", new JSONArray());

            if (novelPage.artists != null) {
                JSONArray jsonArray = new JSONArray();
                for (String artist : novelPage.artists)
                    jsonArray.put(serializeToString(artist));
                jsonObject.put("artists", jsonArray);
            } else jsonObject.put("artists", new JSONArray());

            if (novelPage.language != null) {
                jsonObject.put("language", serializeToString(novelPage.language));
            } else jsonObject.put("language", "null");

            jsonObject.put("maxChapterPage", novelPage.maxChapterPage);

            if (novelPage.novelChapters != null) {
                JSONArray jsonArray = new JSONArray();
                for (NovelChapter novelChapter : novelPage.novelChapters)
                    jsonArray.put(serializeToString(novelChapterToJSON(novelChapter).toString()));
                jsonObject.put("novelChapters", jsonArray);
            } else jsonObject.put("novelChapters", new JSONArray());

            if (debug)
                System.out.println("JSON to be serialized: " + jsonObject.toString());

            return serializeToString(jsonObject.toString());
        } else throw new Exception("Illegal class");
    }

    public static NovelPage deserializeNovelPageJSON(String serial) throws Exception {
        NovelPage novelPage = new NovelPage();
        JSONObject jsonObject = new JSONObject((String) deserializeString(serial));
        if (debug)
            System.out.println("Deserialize-d json: " + jsonObject);
        for (String key : NOVELPAGEKEYS) {
            if (!jsonObject.has(key))
                throw new Exception("JSON is invalid due to missing key[" + key + "]");

            switch (key) {
                case "maxChapterPage":
                    novelPage.maxChapterPage = jsonObject.getInt(key);
                    break;

                case "status":
                    switch (jsonObject.getString(key)) {
                        case "Publishing":
                            novelPage.status = Stati.PUBLISHING;
                            break;
                        case "Completed":
                            novelPage.status = Stati.COMPLETED;
                            break;
                        case "Paused":
                            novelPage.status = Stati.PAUSED;
                            break;
                        case "Unknown":
                            novelPage.status = Stati.UNKNOWN;
                            break;
                    }
                    break;

                case "genres":
                case "authors":
                case "tags":
                case "artists":
                    JSONArray array = jsonObject.getJSONArray(key);
                    String[] strings = new String[array.length()];
                    for (int x = 0; x < array.length(); x++) {
                        String s = array.getString(x);
                        strings[x] = (String) deserializeString(s);
                    }
                    switch (key) {
                        case "genres":
                            novelPage.genres = strings;
                            break;
                        case "authors":
                            novelPage.authors = strings;
                            break;
                        case "tags":
                            novelPage.tags = strings;
                            break;
                        case "artists":
                            novelPage.artists = strings;
                            break;
                    }
                    break;
                case "novelChapters":
                    JSONArray jsonArray = jsonObject.getJSONArray(key);
                    ArrayList<NovelChapter> novelChapters = new ArrayList<>();
                    for (int x = 0; x < jsonArray.length(); x++) {
                        novelChapters.add(deserializeNovelChapterJSON(jsonArray.getString(x)));
                    }
                    novelPage.novelChapters = novelChapters;
                    break;
                default:
                    String response = jsonObject.getString(key);
                    if (!response.equals("null")) {
                        if (debug)
                            System.out.println("Serial response of novelChapter key [" + key + "]: " + response);
                        response = (String) deserializeString(response);
                    }
                    switch (key) {
                        case "title":
                            if (response.equals("null"))
                                novelPage.title = null;
                            else novelPage.title = response;
                            break;
                        case "imageURL":
                            if (response.equals("null"))
                                novelPage.imageURL = null;
                            else novelPage.imageURL = response;
                            break;
                        case "description":
                            if (response.equals("null"))
                                novelPage.description = null;
                            else novelPage.description = response;
                            break;
                        case "language":
                            if (response.equals("null"))
                                novelPage.language = null;
                            else novelPage.language = response;
                            break;
                    }
                    break;
            }
        }
        return novelPage;
    }

    public static NovelChapter deserializeNovelChapterJSON(String serial) throws Exception {
        NovelChapter novelChapter = new NovelChapter();
        JSONObject jsonObject = new JSONObject((String) deserializeString(serial));
        for (String key : NOVELCHAPTERKEYS) {
            if (!jsonObject.has(key))
                throw new Exception("JSON is invalid due to missing key[" + key + "]");

            String response = (String) deserializeString(jsonObject.getString(key));
            switch (key) {
                case "release":
                    if (response.equals("null"))
                        novelChapter.release = null;
                    else novelChapter.release = response;
                    break;
                case "chapterNum":
                    if (response.equals("null"))
                        novelChapter.chapterNum = null;
                    else novelChapter.chapterNum = response;
                    break;
                case "link":
                    if (response.equals("null"))
                        novelChapter.link = null;
                    else novelChapter.link = response;
                    break;
            }
        }
        return novelChapter;
    }

    private static JSONObject novelChapterToJSON(NovelChapter novelChapter) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("release", serializeToString(novelChapter.release));
        jsonObject.put("chapterNum", serializeToString(novelChapter.chapterNum));
        jsonObject.put("link", serializeToString(novelChapter.link));
        return jsonObject;
    }
}
