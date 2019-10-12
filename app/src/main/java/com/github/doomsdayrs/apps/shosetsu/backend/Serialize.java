package com.github.doomsdayrs.apps.shosetsu.backend;

import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.Stati;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.shoDir;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.deserialize;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.serialize;

/*
 * This file is part of shosetsu-services.
 * shosetsu-services is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * shosetsu-services is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with shosetsu-services.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 * shosetsu-services
 * 07 / 09 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class Serialize {
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
            return serialize(novelChapterToJSON(novelChapter).toString());
        } else if (object.getClass().equals(NovelPage.class)) {
            NovelPage novelPage = (NovelPage) object;
            JSONObject jsonObject = new JSONObject();

            if (novelPage.title != null)
                jsonObject.put("title", serialize(novelPage.title));
            else jsonObject.put("title", "null");

            if (novelPage.imageURL != null)
                jsonObject.put("imageURL", serialize(novelPage.imageURL));
            else jsonObject.put("imageURL", "null");

            if (novelPage.description != null)
                jsonObject.put("description", serialize(novelPage.description));
            else jsonObject.put("description", "null");

            if (novelPage.genres != null) {
                JSONArray jsonArray = new JSONArray();
                for (String genre : novelPage.genres)
                    jsonArray.put(serialize(genre));
                jsonObject.put("genres", jsonArray);
            } else jsonObject.put("genres", new JSONArray());

            if (novelPage.authors != null) {
                JSONArray jsonArray = new JSONArray();
                for (String author : novelPage.authors)
                    jsonArray.put(serialize(author));
                jsonObject.put("authors", jsonArray);
            } else jsonObject.put("authors", new JSONArray());

            if (novelPage.status != null) {
                jsonObject.put("status", novelPage.status.toString());
            } else jsonObject.put("status", "Unknown");

            if (novelPage.tags != null) {
                JSONArray jsonArray = new JSONArray();
                for (String tag : novelPage.tags)
                    jsonArray.put(serialize(tag));
                jsonObject.put("tags", jsonArray);
            } else jsonObject.put("tags", new JSONArray());

            if (novelPage.artists != null) {
                JSONArray jsonArray = new JSONArray();
                for (String artist : novelPage.artists)
                    jsonArray.put(serialize(artist));
                jsonObject.put("artists", jsonArray);
            } else jsonObject.put("artists", new JSONArray());

            if (novelPage.language != null) {
                jsonObject.put("language", serialize(novelPage.language));
            } else jsonObject.put("language", "null");

            jsonObject.put("maxChapterPage", novelPage.maxChapterPage);

            if (novelPage.novelChapters != null) {
                JSONArray jsonArray = new JSONArray();
                for (NovelChapter novelChapter : novelPage.novelChapters)
                    jsonArray.put(serialize(novelChapterToJSON(novelChapter).toString()));
                jsonObject.put("novelChapters", jsonArray);
            } else jsonObject.put("novelChapters", new JSONArray());

            if (debug)
                System.out.println("JSON to be serialized: " + jsonObject.toString());

            return serialize(jsonObject.toString());
        } else throw new Exception("Illegal class");
    }

    /**
     * Deserializes a NovelPage from JSON
     *
     * @param serial SERIAL String
     * @return NovelPage
     * @throws Exception If something goes wrong
     */
    public static NovelPage deserializeNovelPageJSON(String serial) throws Exception {
        NovelPage novelPage = new NovelPage();
        JSONObject jsonObject = new JSONObject((String) deserialize(serial));
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
                        strings[x] = (String) deserialize(s);
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
                        response = (String) deserialize(response);
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

    /**
     * Deserializes a NovelChapter from JSON
     *
     * @param serial SERIAL String
     * @return NovelChapter
     * @throws Exception If something goes wrong
     */
    public static NovelChapter deserializeNovelChapterJSON(String serial) throws Exception {
        NovelChapter novelChapter = new NovelChapter();
        JSONObject jsonObject = new JSONObject((String) deserialize(serial));
        for (String key : NOVELCHAPTERKEYS) {
            if (!jsonObject.has(key))
                throw new Exception("JSON is invalid due to missing key[" + key + "]");

            String response = (String) deserialize(jsonObject.getString(key));
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

    /**
     * Converts a NovelChapter TO json
     *
     * @param novelChapter NovelChapter to convert
     * @return JSON version of NovelChapter
     * @throws IOException   EXCEPTION
     * @throws JSONException EXCEPTION
     */
    private static JSONObject novelChapterToJSON(NovelChapter novelChapter) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("release", serialize(novelChapter.release));
        jsonObject.put("chapterNum", serialize(novelChapter.chapterNum));
        jsonObject.put("link", serialize(novelChapter.link));
        return jsonObject;
    }

    /**
     * Returns current settings in JSON format, Follows schema.json
     *
     * @return JSON of settings
     * @throws JSONException EXCEPTION
     * @throws IOException   EXCEPTION IN SERIALIZING
     */
    public static JSONObject getSettingsInJSON() throws JSONException, IOException {
        JSONObject settings = new JSONObject();
        settings.put("reader_text_color", Settings.ReaderTextColor);
        settings.put("reader_text_background_color", Settings.ReaderTextBackgroundColor);
        settings.put("shoDir", Database.serialize(shoDir));
        settings.put("paused", Settings.downloadPaused);
        settings.put("textSize", Settings.ReaderTextSize);
        settings.put("themeMode", Settings.themeMode);
        settings.put("paraSpace", Settings.paragraphSpacing);
        settings.put("indent", Settings.indentSize);
        settings.put("tap_to_scroll", Utilities.isTapToScroll());
        return settings;
    }
}
