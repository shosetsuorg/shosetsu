package com.github.doomsdayrs.apps.shosetsu.backend;

import androidx.annotation.NonNull;

import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.deserializeString;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.serializeToString;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.shoDir;

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

// --Commented out by Inspection START (12/22/19 11:10 AM):
//    public static void toggleDebug() {
//        debug = !debug;
//    }
// --Commented out by Inspection STOP (12/22/19 11:10 AM)

// --Commented out by Inspection START (12/22/19 11:10 AM):
//    /**
//     * Turns an object into a JSON counterpart, then serializes it along with data in it
//     *
//     * @param object NovelPage or NovelChapter
//     * @return Serialized JSON
//     */
//    @NonNull
//    public static String serializeOBJECT(@NonNull Object object) throws Exception {
//        if (object.getClass().equals(NovelChapter.class)) {
//            NovelChapter novelChapter = (NovelChapter) object;
//            return serializeToString(novelChapterToJSON(novelChapter).toString());
//        } else if (object.getClass().equals(NovelPage.class)) {
//            NovelPage novelPage = (NovelPage) object;
//            JSONObject jsonObject = new JSONObject();
//
//            jsonObject.put("title", serializeToString(novelPage.getTitle()));
//
//            jsonObject.put("imageURL", serializeToString(novelPage.getImageURL()));
//
//            jsonObject.put("description", serializeToString(novelPage.getDescription()));
//
//            JSONArray jsonArray = new JSONArray();
//            for (String genre : novelPage.getGenres())
//                jsonArray.put(serializeToString(genre));
//            jsonObject.put("genres", jsonArray);
//
//            jsonArray = new JSONArray();
//            for (String author : novelPage.getAuthors())
//                jsonArray.put(serializeToString(author));
//            jsonObject.put("authors", jsonArray);
//
//            jsonObject.put("status", novelPage.getStatus().toString());
//
//            jsonArray = new JSONArray();
//            for (String tag : novelPage.getTags())
//                jsonArray.put(serializeToString(tag));
//            jsonObject.put("tags", jsonArray);
//
//            jsonArray = new JSONArray();
//            for (String artist : novelPage.getArtists())
//                jsonArray.put(serializeToString(artist));
//            jsonObject.put("artists", jsonArray);
//
//            jsonObject.put("language", serializeToString(novelPage.getLanguage()));
//
//            jsonObject.put("maxChapterPage", novelPage.getMaxChapterPage());
//
//            jsonArray = new JSONArray();
//            for (NovelChapter novelChapter : novelPage.getNovelChapters())
//                jsonArray.put(serializeToString(novelChapterToJSON(novelChapter).toString()));
//            jsonObject.put("novelChapters", jsonArray);
//
//            if (debug)
//                System.out.println("JSON to be serialized: " + jsonObject.toString());
//
//            return serializeToString(jsonObject.toString());
//        } else throw new Exception("Illegal class");
//    }
// --Commented out by Inspection STOP (12/22/19 11:10 AM)

// --Commented out by Inspection START (12/22/19 11:10 AM):
//    /**
//     * Deserializes a NovelPage from JSON
//     *
//     * @param serial SERIAL String
//     * @return NovelPage
//     * @throws Exception If something goes wrong
//     */
//    @NonNull
//    public static NovelPage deserializeNovelPageJSON(@NonNull String serial) throws Exception {
//        NovelPage novelPage = new NovelPage();
//        JSONObject jsonObject = new JSONObject((String) deserializeString(serial));
//        if (debug)
//            System.out.println("Deserialize-d json: " + jsonObject);
//        for (String key : NOVELPAGEKEYS) {
//            if (!jsonObject.has(key))
//                throw new Exception("JSON is invalid due to missing key[" + key + "]");
//
//            switch (key) {
//                case "maxChapterPage":
//                    novelPage.setMaxChapterPage(jsonObject.getInt(key));
//                    break;
//
//                case "status":
//                    switch (jsonObject.getString(key)) {
//                        case "Publishing":
//                            novelPage.setStatus(NovelStatus.PUBLISHING);
//                            break;
//                        case "Completed":
//                            novelPage.setStatus(NovelStatus.COMPLETED);
//                            break;
//                        case "Paused":
//                            novelPage.setStatus(NovelStatus.PAUSED);
//                            break;
//                        case "Unknown":
//                            novelPage.setStatus(NovelStatus.UNKNOWN);
//                            break;
//                    }
//                    break;
//
//                case "genres":
//                case "authors":
//                case "tags":
//                case "artists":
//                    JSONArray array = jsonObject.getJSONArray(key);
//                    String[] strings = new String[array.length()];
//                    for (int x = 0; x < array.length(); x++) {
//                        String s = array.getString(x);
//                        strings[x] = (String) deserializeString(s);
//                    }
//                    switch (key) {
//                        case "genres":
//                            novelPage.setGenres(strings);
//                            break;
//                        case "authors":
//                            novelPage.setAuthors(strings);
//                            break;
//                        case "tags":
//                            novelPage.setTags(strings);
//                            break;
//                        case "artists":
//                            novelPage.setArtists(strings);
//                            break;
//                    }
//                    break;
//                case "novelChapters":
//                    JSONArray jsonArray = jsonObject.getJSONArray(key);
//                    ArrayList<NovelChapter> novelChapters = new ArrayList<>();
//                    for (int x = 0; x < jsonArray.length(); x++) {
//                        novelChapters.add(deserializeNovelChapterJSON(jsonArray.getString(x)));
//                    }
//                    novelPage.setNovelChapters(novelChapters);
//                    break;
//                default:
//                    String response = jsonObject.getString(key);
//                    if (!response.equals("null")) {
//                        if (debug)
//                            System.out.println("Serial response of novelChapter key [" + key + "]: " + response);
//                        response = (String) deserializeString(response);
//                    }
//                    if (response != null)
//                        switch (key) {
//                            case "title":
//                                if (response.equals("null"))
//                                    novelPage.setTitle("unknown");
//                                else novelPage.setTitle(response);
//                                break;
//                            case "imageURL":
//                                if (response.equals("null"))
//                                    novelPage.setImageURL("unknown");
//                                else novelPage.setImageURL(response);
//                                break;
//                            case "description":
//                                if (response.equals("null"))
//                                    novelPage.setDescription("unknown");
//                                else novelPage.setDescription(response);
//                                break;
//                            case "language":
//                                if (response.equals("null"))
//                                    novelPage.setLanguage("unknown");
//                                else novelPage.setLanguage(response);
//                                break;
//                        }
//                    break;
//            }
//        }
//        return novelPage;
//    }
// --Commented out by Inspection STOP (12/22/19 11:10 AM)

    /**
     * Deserializes a NovelChapter from JSON
     *
     * @param serial SERIAL String
     * @return NovelChapter
     * @throws Exception If something goes wrong
     */
    @NonNull
    private static NovelChapter deserializeNovelChapterJSON(@NonNull String serial) throws Exception {
        NovelChapter novelChapter = new NovelChapter();
        JSONObject jsonObject = new JSONObject((String) deserializeString(serial));
        for (String key : NOVELCHAPTERKEYS) {
            if (!jsonObject.has(key))
                throw new Exception("JSON is invalid due to missing key[" + key + "]");

            String response = (String) deserializeString(jsonObject.getString(key));
            if (response != null)
                switch (key) {
                    case "release":
                        if (response.equals("null"))
                            novelChapter.setRelease("unknown");
                        else novelChapter.setRelease(response);
                        break;
                    case "chapterNum":
                        if (response.equals("null"))
                            novelChapter.setTitle("unknown");
                        else novelChapter.setTitle(response);
                        break;
                    case "link":
                        if (response.equals("null"))
                            novelChapter.setLink("unknown");
                        else novelChapter.setLink(response);
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
    @NonNull
    private static JSONObject novelChapterToJSON(@NonNull NovelChapter novelChapter) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("release", serializeToString(novelChapter.getRelease()));
        jsonObject.put("chapterNum", serializeToString(novelChapter.getTitle()));
        jsonObject.put("link", serializeToString(novelChapter.getLink()));
        return jsonObject;
    }

    /**
     * Returns current settings in JSON format, Follows schema.json
     *
     * @return JSON of settings
     * @throws JSONException EXCEPTION
     * @throws IOException   EXCEPTION IN SERIALIZING
     */
    @NonNull
    public static JSONObject getSettingsInJSON() throws JSONException, IOException {
        JSONObject settings = new JSONObject();
        settings.put("reader_text_color", Settings.ReaderTextColor);
        settings.put("reader_text_background_color", Settings.ReaderTextBackgroundColor);
        settings.put("shoDir", serializeToString(shoDir));
        settings.put("paused", Settings.downloadPaused);
        settings.put("textSize", Settings.ReaderTextSize);
        settings.put("themeMode", Settings.themeMode);
        settings.put("paraSpace", Settings.paragraphSpacing);
        settings.put("indent", Settings.indentSize);
        settings.put("tap_to_scroll", Utilities.isTapToScroll());
        return settings;
    }
}
