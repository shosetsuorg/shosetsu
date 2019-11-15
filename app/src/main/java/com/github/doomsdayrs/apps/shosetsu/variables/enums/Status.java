package com.github.doomsdayrs.apps.shosetsu.variables.enums;

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
 * 20 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */

import androidx.annotation.NonNull;

/**
 * Status of novel/Chapter
 */
public enum Status {
    // Novels and chapters
    UNREAD(0, "Unread"),
    READING(1, "Reading"),
    READ(2, "Read"),
    // These two are for novels only
    ONHOLD(3, "OnHold"),
    DROPPED(4, "Dropped"),
    UNKNOWN(-1, "Unknown");

    private final int a;

    private final String status;

    Status(int a, String status) {
        this.a = a;
        this.status = status;
    }

    public int getA() {
        return a;
    }

    public String getStatus() {
        return status;
    }

    @NonNull
    @Override
    public String toString() {
        return "" + a;
    }

    @NonNull
    public static Status getStatus(int a) {
        switch (a) {
            case 0:
                return UNREAD;
            case 1:
                return READING;
            case 2:
                return READ;
            case 3:
                return ONHOLD;
            case 4:
                return DROPPED;
            default:
                return UNKNOWN;
        }
    }
}
