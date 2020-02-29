package com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions

import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.reader.fragments.ChapterView

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */

/**
 * shosetsu
 * 01 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */

class ThemeChange(private val chapterReader: ChapterView) : Utilities.DeMarkAction {

    override fun action(spared: Int) {
        when (spared) {
            0 -> {
                Utilities.setNightNode()
            }
            1 -> {
                Utilities.setLightMode()
            }
            2 -> {
                Utilities.setSepiaMode(chapterReader.context!!)
            }
        }
        chapterReader.setUpReader()
    }

}