package com.github.doomsdayrs.apps.shosetsu.variables;

import com.github.Doomsdayrs.api.novelreader_core.extensions.lang.en.box_novel.BoxNovel;
import com.github.Doomsdayrs.api.novelreader_core.extensions.lang.en.novel_full.NovelFull;
import com.github.Doomsdayrs.api.novelreader_core.extensions.lang.en.syosetu.Syosetu;
import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelGenre;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
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
    NOVELFULL(new NovelFull(1)),
    BOXNOVEL(new BoxNovel(2)),
    SYOSETU(new Syosetu(3));

    public static final ArrayList<Formatter> formatters = new ArrayList<>();

    static {
        formatters.add(NOVELFULL);
        formatters.add(BOXNOVEL);
        formatters.add(SYOSETU);
    }

    private final Formatter formatter;

    DefaultScrapers(Formatter formatter) {
        this.formatter = formatter;
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

    public String getNovelPassage(String URL) throws IOException {
        return formatter.getNovelPassage(URL);
    }

    public NovelPage parseNovel(String URL) throws IOException {
        return formatter.parseNovel(URL);
    }

    public NovelPage parseNovel(String URL, int increment) throws IOException {
        return formatter.parseNovel(URL, increment);
    }

    public String getLatestURL(int page) {
        return formatter.getLatestURL(page);
    }

    public List<Novel> parseLatest(String URL) throws IOException {
        return formatter.parseLatest(URL);
    }

    @Override
    public List<Novel> search(String query) throws IOException {
        return formatter.search(query);
    }

    @Override
    public NovelGenre[] getGenres() {
        return formatter.getGenres();
    }
}
