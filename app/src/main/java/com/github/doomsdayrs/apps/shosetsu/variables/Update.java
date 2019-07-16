package com.github.doomsdayrs.apps.shosetsu.variables;
/*
 * This file is part of shosetsu.
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see https://www.gnu.org/licenses/ .
 * ====================================================================
 * shosetsu
 * 15 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class Update {
    public final String novelURL;
    public final String chapterURL;
    public final long timeMS;
    public final String timeText;

    public Update(String novelURL, String chapterURL, long timeMS) {
        this.novelURL = novelURL;
        this.chapterURL = chapterURL;
        this.timeMS = timeMS;
        timeText = new SimpleDateFormat("MMM dd,yyyy HH:mm").format(new Date(timeMS));
    }
}
