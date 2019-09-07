package com.github.doomsdayrs.apps.shosetsu.backend.database.objects;

import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

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
 * shosetsu
 * 27 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class SettingsSerialized implements Serializable {
    static final long serialVersionUID = 2005;


    public int reader_text_color = Settings.ReaderTextColor;
    public int reader_text_background_color = Settings.ReaderTextBackgroundColor;
    public String shoDir = Utilities.shoDir;
    public boolean paused = Settings.downloadPaused;
    public float textSize = Settings.ReaderTextSize;
    public int themeMode = Settings.themeMode;
    public int paraSpace = Settings.paragraphSpacing;
    public int indent = Settings.indentSize;
    public boolean tap_to_scroll = Utilities.isTapToScroll();

    @NotNull
    @Override
    public String toString() {
        return "SettingsSerialized{" +
                "reader_text_color=" + reader_text_color +
                ", reader_text_background_color=" + reader_text_background_color +
                ", shoDir='" + shoDir + '\'' +
                ", paused=" + paused +
                ", textSize=" + textSize +
                ", themeMode=" + themeMode +
                ", paraSpace=" + paraSpace +
                ", indent=" + indent +
                '}';
    }
}
