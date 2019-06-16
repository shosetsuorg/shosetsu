package com.github.Doomsdayrs.apps.shosetsu.tracking;

import android.content.res.Resources;

import com.github.Doomsdayrs.apps.shosetsu.R;

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
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public enum Trackers {
    ANILIST(getString(R.string.anilist), 1);
    final String name;
    final int id;

    Trackers(String name, int id) {
        this.name = name;
        this.id = id;
    }

    static String getString(int id) {
        return Resources.getSystem().getString(id);
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Trackers{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }}
