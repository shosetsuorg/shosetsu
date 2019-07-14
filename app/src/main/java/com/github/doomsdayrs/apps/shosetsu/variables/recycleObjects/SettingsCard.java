package com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects;


import com.github.doomsdayrs.apps.shosetsu.variables.enums.Types;

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

public class SettingsCard {

    /**
     * What kind of settings card this is
     */
    public final Types ID;

    /**
     * Constructor
     *
     * @param ID card type
     */
    public SettingsCard(Types ID) {
        this.ID = ID;
    }
}
