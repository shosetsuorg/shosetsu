package com.github.doomsdayrs.apps.shosetsu.variables;

import com.github.Doomsdayrs.api.shosetsu.extensions.lang.en.bestlightnovel.BestLightNovel;
import com.github.Doomsdayrs.api.shosetsu.extensions.lang.en.box_novel.BoxNovel;
import com.github.Doomsdayrs.api.shosetsu.extensions.lang.en.novel_full.NovelFull;
import com.github.Doomsdayrs.api.shosetsu.extensions.lang.en.syosetu.Syosetu;
import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.Novel;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelGenre;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.Ordering;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueCard;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

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
 * Shosetsu
 * 30 / May / 2019
 *
 * @author github.com/doomsdayrs
 */

/**
 * Moved from deprecated novelreader-core
 * Contains functional novels and their IDs
 */
// TODO Make this full dynamic, not needing to be predefined
// > Make IDs built into the formatter
public enum DefaultScrapers implements Formatter {
    NOVELFULL(new NovelFull()),
    BOXNOVEL(new BoxNovel()),
    SYOSETU(new Syosetu()),
    //NOVELPLANENT(new NovelPlanet(4)),
    BESTLIGHTNOVEL(new BestLightNovel());

    private static final ArrayList<Formatter> formatters = new ArrayList<>();

    public static Formatter getByID(int ID) {
        for (Formatter formatter : formatters) {
            if (formatter.getID() == ID)
                return formatter;
        }
        return null;
    }

    public static ArrayList<CatalogueCard> getAsCatalogue() {
        ArrayList<CatalogueCard> catalogueCards = new ArrayList<>();
        for (Formatter formatter : DefaultScrapers.formatters)
            catalogueCards.add(new CatalogueCard(formatter));
        return catalogueCards;
    }

    static {
        formatters.add(NOVELFULL);
        formatters.add(BOXNOVEL);
        formatters.add(SYOSETU);
        // formatters.add(NOVELPLANENT);
        formatters.add(BESTLIGHTNOVEL);
    }

    private final Formatter formatter;

    DefaultScrapers(Formatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void setBuilder(Request.Builder builder) {
        formatter.setBuilder(builder);
    }

    @Override
    public void setClient(OkHttpClient okHttpClient) {
        formatter.setClient(okHttpClient);
    }

    @Override
    public boolean hasCloudFlare() {
        return formatter.hasCloudFlare();
    }

    @Override
    public String getName() {
        return formatter.getName();
    }

    @Override
    public String getImageURL() {
        return formatter.getImageURL();
    }

    @Override
    public int getID() {
        return formatter.getID();
    }

    @Override
    public boolean hasSearch() {
        return formatter.hasSearch();
    }

    @Override
    public boolean hasGenres() {
        return formatter.hasGenres();
    }

    public boolean isIncrementingChapterList() {
        return formatter.isIncrementingChapterList();
    }

    @Override
    public boolean isIncrementingPassagePage() {
        return formatter.isIncrementingPassagePage();
    }

    @Override
    public Ordering chapterOrder() {
        return formatter.chapterOrder();
    }

    @Override
    public Ordering latestOrder() {
        return formatter.latestOrder();
    }

    @Override
    public String getNovelPassage(Document document) {
        return formatter.getNovelPassage(document);
    }

    @Deprecated
    public String getNovelPassage(String URL) throws IOException {
        return formatter.getNovelPassage(URL);
    }

    @Override
    public NovelPage parseNovel(Document document) {
        return formatter.parseNovel(document);
    }

    @Override
    public NovelPage parseNovel(Document document, int i) {
        return formatter.parseNovel(document, i);
    }

    @Override
    public String novelPageCombiner(String s, int i) {
        return formatter.novelPageCombiner(s, i);
    }

    @Deprecated
    public NovelPage parseNovel(String URL) throws IOException {
        return formatter.parseNovel(URL);
    }

    @Deprecated
    public NovelPage parseNovel(String URL, int increment) throws IOException {
        return formatter.parseNovel(URL, increment);
    }

    public String getLatestURL(int page) {
        return formatter.getLatestURL(page);
    }

    @Override
    public List<Novel> parseLatest(Document document) {
        return formatter.parseLatest(document);
    }

    @Deprecated
    public List<Novel> parseLatest(String URL) throws IOException {
        return formatter.parseLatest(URL);
    }

    @Override
    public String getSearchString(String s) {
        return formatter.getSearchString(s);
    }

    @Override
    public List<Novel> parseSearch(Document document) {
        return formatter.parseSearch(document);
    }

    @Deprecated
    public List<Novel> search(String query) throws IOException {
        return formatter.search(query);
    }

    @Override
    public NovelGenre[] getGenres() {
        return formatter.getGenres();
    }
}
