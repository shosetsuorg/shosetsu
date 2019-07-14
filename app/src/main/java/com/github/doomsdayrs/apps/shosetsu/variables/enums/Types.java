package com.github.doomsdayrs.apps.shosetsu.variables.enums;

import org.jetbrains.annotations.NotNull;

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
 * 14 / June / 2019
 *
 * @author github.com/doomsdayrs
 */

/**
 * Used for setting fragments
 */
public enum Types {
    DOWNLOAD("Download"),
    VIEW("View"),
    ADVANCED("Advanced"),
    CREDITS("Credits"),
    BACKUP("Backup");

    /**
     * Type name
     */
    private final String name;

    /**
     * Constructor
     *
     * @param name name of type
     */
    Types(String name) {
        this.name = name;
    }

    /**
     * toString overriding method
     *
     * @return name of type
     */
    @NotNull
    @Override
    public String toString() {
        return name;
    }
}