package com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects

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
class NovelCard
/**
 * Constructor
 *
 * @param title       title
 * @param novelID   novel ID
 * @param novelURL    novelURL
 * @param imageURL    imageURL
 * @param formatterID id of formatter
 */(title: String, val novelID: Int,
    /**
     * NovelURL
     */
    val novelURL: String,
    /**
     * ImageURL
     */
    val imageURL: String,
    /**
     * ID of formatter
     */
    val formatterID: Int) : RecycleCard(title)