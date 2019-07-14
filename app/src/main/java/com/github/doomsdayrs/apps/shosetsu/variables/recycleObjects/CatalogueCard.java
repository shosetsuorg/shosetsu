package com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;

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
public class CatalogueCard extends RecycleCard {
    /**
     * Formatter
     */
    public final Formatter formatter;

    /**
     * Constructor
     *
     * @param formatter the formatter
     */
    public CatalogueCard(Formatter formatter) {
        super(formatter.getName());
        this.formatter = formatter;
    }


}
