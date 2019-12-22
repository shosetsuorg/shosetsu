package com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects;

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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelCard extends RecycleCard {

    public final int novelID;
    /**
     * NovelURL
     */
    public final String novelURL;
    /**
     * ImageURL
     */
    public final String imageURL;
    /**
     * ID of formatter
     */
    public final int formatterID;

    /**
     * Constructor
     *
     * @param title       title
     * @param novelID   novel ID
     * @param novelURL    novelURL
     * @param imageURL    imageURL
     * @param formatterID id of formatter
     */
    public NovelCard(String title, int novelID, String novelURL, String imageURL, int formatterID) {
        super(title);
        this.novelID = novelID;
        this.novelURL = novelURL;
        this.imageURL = imageURL;
        this.formatterID = formatterID;
    }


}
