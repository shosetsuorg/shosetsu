package com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects;

import java.io.Serializable;

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
 * along with Shosetsu.  If not, see https://www.gnu.org/licenses/ .
 * ====================================================================
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class CatalogueNovelCard extends RecycleCard implements Serializable {
    /**
     * Image novelURL
     */
    public final String imageURL;

    /**
     * link to the novel
     */
    public final String novelURL;

    /**
     * Constructor
     *
     * @param imageURL image chapterURL
     * @param title    title
     * @param novelURL novelURL
     */
    public CatalogueNovelCard(String imageURL, String title, String novelURL) {
        super(title);
        this.imageURL = imageURL;
        this.novelURL = novelURL;
    }

}
