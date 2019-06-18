package com.github.doomsdayrs.apps.shosetsu.recycleObjects;

import java.net.URI;

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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class CatalogueNovelCard {
    public final String libraryImageResource;
    public final String title;
    public final String URL;

    public CatalogueNovelCard(String libraryImageResource, String title, URI URL) {
        this.libraryImageResource = libraryImageResource;
        this.title = title;
        this.URL = URL.getPath();
    }

}
